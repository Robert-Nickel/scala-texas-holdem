package main.scala.poker.model

import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}

case class Player(name: String, stack: Int = 0, holeCards: Option[(Card, Card)] = None, currentBet: Int = 0) {

  def getHoleCardsString(): String = {
    if (name.equals("You")) {
      s"${
        if (holeCards.isDefined) {
          holeCards.get._1 + "" + holeCards.get._2
        } else {
          "None None"
        }
      }"
    } else {
      "[xx][xx]"
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
    this.copy(stack = newStack, currentBet = newCurrentBet)
  }

  def raise(amount: Int, highestOverallBet: Int): Try[Player] = {
    amount match {
      case x if x >= stack && stack < 1 => Failure(new Throwable("Stack is too small"))
      case x if x >= stack && stack >= 1 => Success(this.copy(stack = 0, currentBet = currentBet + stack)) // All-in
      case x if x < highestOverallBet * 2 => Failure(new Throwable("Raise is not high enough."))
      case _ => Success(this.copy(stack = stack - (amount - currentBet), currentBet = amount))
    }
  }

  def safeRaise(amount: Int, highestOverallBet: Int): Player = {
    if (raise(amount, highestOverallBet).isFailure) {
      safeRaise(amount + 5, highestOverallBet)
    } else {
      raise(amount, highestOverallBet).get
    }
  }

  // TODO: maybe consider extracting this
  def actAsBot(highestOverallBet: Int, BB: Int, values: HashMap[Char, Set[Int]]): Player = {
    Thread.sleep(500)
    val handValue = getHandValue(values)

    handValue match {
      case x if x > 30 => safeRaise(stack, highestOverallBet)
      case x if x > 20 => safeRaise(3 * BB, highestOverallBet)
      case _ if stack < 10 * BB => safeRaise(stack, highestOverallBet)
      case _ if highestOverallBet <= 3 * BB => call(highestOverallBet)
      case _ => fold()
    }
  }

  def getHandValue(values: HashMap[Char, Set[Int]]): Int = {
    if (holeCards.isDefined) {
      val card1 = holeCards.get._1
      val card2 = holeCards.get._2
      val sum = values(card1.value).max + values(card2.value).max
      val suitedValue = if (card1.symbol == card2.symbol) 6 else 0
      val delta = (values(card1.value).max - values(card2.value).max).abs
      val connectorValue = Set(8 - delta * 2, 0).max
      sum + suitedValue + connectorValue
    } else {
      0
    }
  }
}