package main.scala.poker.model

import poker.{PlayerDSL, bb, sb, cardValues}

import scala.util.{Failure, Success, Try}

case class Table(players: List[Player], deck: List[Card] = List(), currentPlayer: Int = 0, currentBettingRound: Int = 0) {

  def tryCurrentPlayerAct(humanInput: Option[String]): Try[Table] = {
    val newActivePlayerTry = (players(currentPlayer), humanInput) match {
      // skip
      case (activePlayer, _) if !activePlayer.isInRound() => Success(activePlayer)
      // small blind
      case (activePlayer, _) if isPreFlop && isSB(activePlayer) && activePlayer.currentBet < sb =>
        activePlayer.post(sb)
      // big blind
      case (activePlayer, _) if isPreFlop && isBB(activePlayer) && activePlayer.currentBet < bb =>
        activePlayer.post(bb)
      case (activePlayer, Some("fold")) => Success(activePlayer.fold())
      case (activePlayer, Some("call")) => Success(activePlayer.call(getHighestOverallBet))
      // bot player
      case (activePlayer, None) => Success(activePlayer.actAsBot(getHighestOverallBet, cardValues))
      case _ => Failure(new Throwable("invalid move by player"))
    }
    newActivePlayerTry match {
      case Success(newActivePlayer) => {
        val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
        Success(this.copy(players = newPlayers))
      }
      case _ => Failure(new Throwable("invalid move by player"))
    }
  }

  def nextPlayer: Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  def getCurrentPlayer: Player = {
    this.players(this.currentPlayer)
  }

  def getHighestOverallBet: Int = {
    players.map(player => player.currentBet).max
  }

  def isPreFlop: Boolean = currentBettingRound == 0

  def isSB(player: Player): Boolean = players(1) == player

  def isBB(player: Player): Boolean = players(2) == player
}
