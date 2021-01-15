package poker.model

import poker.model.Bot

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

case class Player(name: String,
                  stack: Int = 0,
                  holeCards: Option[(Card, Card)] = None,
                  currentBet: Int = 0,
                  hasActedThisBettingRound: Boolean = false,
                  roundInvestment: Int = 0) :

  def getHoleCardsString(showCards: Boolean = false): String = 
    if holeCards.isDefined then
      if showCards || name.equals("You") then
        s"${holeCards.get._1}${holeCards.get._2}"
      else
        "[xx][xx]"
    else
      " " * 8

  def fold(): Player = this.copy(holeCards = None)

  def check(highestOverallBet: Int): Try[Player] = 
    if highestOverallBet == currentBet then
      Success(this.copy(hasActedThisBettingRound = true))
    else
      Failure(new Throwable(s"You cannot check, the current bet is $highestOverallBet"))
  
  def call(highestOverallBet: Int): Try[Player] =
    stack match {
      case 0 => Failure(new Throwable(s"You cannot call, your stack is 0"))
      case stack if stack - highestOverallBet < 0 =>
        Success(this.copy(stack = 0, currentBet = stack + currentBet, hasActedThisBettingRound = true)) // All-in
      case _ =>
        Success(this.copy(stack = stack - (highestOverallBet - currentBet), currentBet = highestOverallBet, hasActedThisBettingRound = true))
    }

  def allIn(highestOverallBet: Int): Player = raise(stack + currentBet, highestOverallBet).get
  
  def raise(amount: Int, highestOverallBet: Int): Try[Player] = 
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

  def actAsBot(highestOverallBet: Int, board: List[Card] = List()): Player = 
    Bot.act(this, highestOverallBet, board)  