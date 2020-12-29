package poker.model

import poker.evaluator.FastPostFlopEvaluator.getPostFlopEvaluation
import poker.evaluator.Evaluator.evalHoleCards
import poker.bb

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
    if board.isEmpty then
      val handValue = evalHoleCards(holeCards)
      actPreflop(handValue, highestOverallBet)
    else
      val flopValue = getPostFlopEvaluation(board :+ holeCards.get._1 :+ holeCards.get._2)
      actPostFlop(flopValue, highestOverallBet)

  def actPostFlop(handValue: Int, highestOverallBet: Int): Player = 
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

      case handValue if handValue > 8_000 =>
        val tryCall = call(highestOverallBet)
        if (tryCall.isSuccess) tryCall.get else fold()
      case _ => fold()
    }
  
  private def actPreflop(handValue: Int, highestOverallBet: Int): Player = 
    handValue match {
      case handValue if handValue > 30 =>
        val tryRaise = raise(5 * bb, highestOverallBet)
        if tryRaise.isFailure then
          val tryCall = call(highestOverallBet)
          if tryCall.isSuccess then tryCall.get else fold()
        else
          tryRaise.get
      case x if x > 20 =>
        val tryRaise = raise(3 * bb, highestOverallBet)
        if tryRaise.isFailure then
          val tryCall = call(highestOverallBet)
          if tryCall.isSuccess then tryCall.get else fold()
        else tryRaise.get
      case _ if stack < 10 * bb =>
        raise(stack, highestOverallBet).get
      // TODO: What if no prior bet has been made? Technically its a check then.
      case _ if highestOverallBet <= 3 * bb => {
        if call(highestOverallBet).isFailure then fold()
        else call(highestOverallBet).get
      }
      case _ => fold()
    }