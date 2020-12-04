package poker.model

import java.util.UUID.randomUUID

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import poker.actor.{FlopActor, RiverActor, Start, TurnActor}
import poker.{actorSystem, bb, cardValues}

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Random, Success, Try}

case class Player(name: String, stack: Int = 0, holeCards: Option[(Card, Card)] = None, currentBet: Int = 0, hasActedThisBettingRound: Boolean = false) {

  def getHoleCardsString(showCards: Boolean = false): String = {
    if (holeCards.isDefined) {
      if (showCards || name.equals("You")) {
        s"${holeCards.get._1}${holeCards.get._2}"
      } else {
        "[xx][xx]"
      }
    } else {
      " " * 8
    }
  }

  def fold(): Player = {
    this.copy(holeCards = None)
  }

  def check(highestOverallBet: Int): Try[Player] = {
    if (highestOverallBet == currentBet) {
      Success(this.copy(hasActedThisBettingRound = true))
    } else {
      Failure(new Throwable(s"You cannot check, the current bet is $highestOverallBet"))
    }
  }

  def call(highestOverallBet: Int): Player = {
    val (newStack, newCurrentBet) = stack - highestOverallBet match {
      case x if x < 0 => (0, stack + currentBet) // All-in
      case _ => (stack - (highestOverallBet - currentBet), highestOverallBet)
    }
    this.copy(stack = newStack, currentBet = newCurrentBet, hasActedThisBettingRound = true)
  }

  def allIn(highestOverallBet: Int): Player = {
    raise(stack + currentBet, highestOverallBet).get
  }

  def raise(amount: Int, highestOverallBet: Int): Try[Player] = {
    amount match {
      case _ if stack == 0 =>
        Failure(new Throwable("You have no more chips. You cannot raise."))
      case amount if amount >= stack && stack != 0 =>
        Success(this.copy(stack = 0, currentBet = currentBet + stack, hasActedThisBettingRound = true)) // All-in
      case amount if amount < highestOverallBet * 2 =>
        Failure(new Throwable("Raise is not high enough."))
      case _ =>
        Success(this.copy(stack = stack - amount + currentBet, currentBet = amount, hasActedThisBettingRound = true))
    }
  }

  def actAsBot(highestOverallBet: Int, board: List[Card] = List()): Player = {
    if (board.isEmpty) {
      val handValue = getHoleCardsValue()
      actPreflop(handValue, highestOverallBet)
    } else {
      val flopValue = getPostFlopValue(board)
      actPostFlop(flopValue, highestOverallBet)
    }
  }

  def getPostFlopValue(board: List[Card]): Int = {
    val handAndBoard = holeCards.get._1 :: holeCards.get._2 :: board

    val actor: ActorRef = if (handAndBoard.size == 5) {
      actorSystem.actorOf(Props(FlopActor(handAndBoard)), "FlopActor" + randomUUID())
    } else if (handAndBoard.size == 6) {
      actorSystem.actorOf(Props(TurnActor(handAndBoard)), "TurnActor" + randomUUID())
    } else {
      actorSystem.actorOf(Props(RiverActor(handAndBoard)), "RiverActor" + randomUUID())
    }
    val result: Future[Any] = actor.ask(Start)(3.seconds)
    Await.result(result, Duration.Inf).asInstanceOf[java.lang.Integer]
  }

  def actPostFlop(handValue: Int, highestOverallBet: Int): Player = {
    handValue match {
      case handValue if handValue > 24_000 => {
        allIn(highestOverallBet)
      }
      case handValue if handValue > 12_000 => {
        val tryRaise = raise(highestOverallBet * 3, highestOverallBet)
        if (tryRaise.isFailure) {
          fold()
        } else {
          tryRaise.get
        }
      }
      case handValue if handValue > 8_000 => call(highestOverallBet)
      case _ => fold()
    }
  }

  private def actPreflop(handValue: Int, highestOverallBet: Int): Player = {
    handValue match {
      case handValue if handValue > 30 =>
        val tryRaise = raise(5 * bb, highestOverallBet)
        if (tryRaise.isFailure) {
          call(highestOverallBet)
        } else {
          tryRaise.get
        }
      case x if x > 20 =>
        val tryRaise = raise(3 * bb, highestOverallBet)
        if (tryRaise.isFailure) {
          call(highestOverallBet)
        } else {
          tryRaise.get
        }
      case _ if stack < 10 * bb =>
        raise(stack, highestOverallBet).get
      // TODO: What if no prior bet has been made? Technically its a check then.
      case _ if highestOverallBet <= 3 * bb => call(highestOverallBet)
      case _ => fold()
    }
  }

  def getHoleCardsValue(): Int = {
    if (holeCards.isDefined) {
      val card1 = holeCards.get._1
      val card2 = holeCards.get._2
      val sum = cardValues(card1.value).max + cardValues(card2.value).max
      val suitedValue = if (card1.symbol == card2.symbol) 6 else 0
      val delta = (cardValues(card1.value).max - cardValues(card2.value).max).abs
      val connectorValue = Set(8 - delta * 2, 0).max
      sum + suitedValue + connectorValue
    } else {
      0
    }
  }
}