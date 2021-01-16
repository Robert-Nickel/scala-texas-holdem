package poker.model

import poker.evaluator.{Evaluation, Evaluator}
import poker.dsl.{PlayerDSL, TableDSL}
import poker.{bb,sb}
import scala.util.{Failure, Random, Success, Try}

case class Table(players: List[Player],
                 deck: List[Card] = List(),
                 currentPlayer: Int = 0,
                 currentBettingRound: Int = 0,
                 pot: Int = 0,
                 board: List[Card] = List()
                ):

  def tryCurrentPlayerAct(maybeInput: Option[String]): Try[Table] =
    val newActivePlayerTry = (players(currentPlayer), maybeInput) match {
      // skip
      case (activePlayer, _) if !activePlayer.isInRound => Success(activePlayer)
      case (activePlayer, _) if activePlayer.isAllIn => Success(activePlayer.copy(hasActedThisBettingRound = true))
      // small blind
      case (activePlayer, _) if this.isPreFlop && this.isSB(activePlayer) && activePlayer.currentBet < sb =>
        Thread.sleep(750)
        activePlayer.post(sb)
      // big blind
      case (activePlayer, _) if this.isPreFlop && this.isBB(activePlayer) && activePlayer.currentBet < bb =>
        Thread.sleep(750)
        activePlayer.post(bb)
      case (activePlayer, Some("fold")) => Success(activePlayer.fold())
      case (activePlayer, Some("check")) => activePlayer.check(this.getHighestOverallBet)
      case (activePlayer, Some("call")) => activePlayer.call(this.getHighestOverallBet)
      case (activePlayer, Some("all-in")) => Success(activePlayer.allIn(this.getHighestOverallBet))
      case (activePlayer, Some(_)) if maybeInput.get.startsWith("raise ") => {
        val raiseAmount = maybeInput.get.split(" ")(1)
        activePlayer.raise(raiseAmount.toInt, this.getHighestOverallBet)
      }
      // bot player
      case (activePlayer, None) => Success(activePlayer.actAsBot(this.getHighestOverallBet, board))
      case _ => Failure(new Throwable("invalid move by player"))
    }
    newActivePlayerTry match {
      case Success(newActivePlayer) => {
        val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
        Success(this.copy(players = newPlayers))
      }
      case _ => Failure(new Throwable("invalid move by player"))
    }

  def resetPlayerActedThisBettingRound(): Table = 
    this.copy(players = players.map(player => player.copy(hasActedThisBettingRound = false)))

  def resetBoard: Table = 
    copy(board = List())

  def setFirstPlayerForBettingRound: Table =
    val firstPlayerForBettingRound = if players.count(player => player.isInRound) == 1 then
      players.indexWhere(player => player.isInRound)
    else
      players.zipWithIndex
        .filter(playerAndIndex => playerAndIndex._1.isInRound) // filter out everyone who is not in round
        .filter(playerAndIndex => playerAndIndex._2 != 0) // filter out the dealer
        .head._2
    this.copy(currentPlayer = firstPlayerForBettingRound)