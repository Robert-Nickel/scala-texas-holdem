package poker.model

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import poker.actor.FlopActor
import poker.actor.PostflopEvaluator.{Start, system}
import poker.{bb, cardValues}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

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

  def call(highestOverallBet: Int): Player = {
    val (newStack, newCurrentBet) = stack - highestOverallBet match {
      case x if x < 0 => (0, stack + currentBet) // All-in
      case _ => (stack - (highestOverallBet - currentBet), highestOverallBet)
    }
    this.copy(stack = newStack, currentBet = newCurrentBet, hasActedThisBettingRound = true)
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

  // TODO: maybe consider extracting this
  def actAsBot(highestOverallBet: Int /* , handAndBoard: List[Card] */): Player = {

    val handValue = getHandValue()

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

  def getHandValue(): Int = {
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