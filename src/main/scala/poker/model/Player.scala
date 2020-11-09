package main.scala.poker.model

import scala.util.{Failure, Success, Try}

case class Player(name: String, stack: Int, holeCards: Option[(Card, Card)], currentBet: Int = 0) {

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
    Thread.sleep(1_000)
    this.copy(holeCards = None)
  }

  def call(highestOverallBet: Int): Player = {
    Thread.sleep(1_000)
    val (newStack, newCurrentBet) = (stack - highestOverallBet) match {
      case x if x < 0 => (0, stack + currentBet)
      case _ => (stack - (highestOverallBet - currentBet), highestOverallBet)
    }
    this.copy(stack = newStack, currentBet = newCurrentBet)
  }

  // TODO: Mindestgroesse von raises (doppelt)
  // TODO: aktuell kann man mehr raisen als man hat :-)
  def raise(amount: Int, highestOverallBet: Int): Try[Player] = {
    Thread.sleep(1000)
     amount match {
      case x if x < highestOverallBet => Failure(new Throwable("Raise is not high enough"))
      case _ => Success(this.copy(stack = stack - (amount - currentBet), currentBet = amount))
    }
  }

  def isInRound: Boolean = {
    holeCards.isDefined
  }
}
