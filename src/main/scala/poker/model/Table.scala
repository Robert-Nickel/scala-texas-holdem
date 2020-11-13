package main.scala.poker.model

import scala.collection.immutable.HashMap
import scala.util.{Failure, Success, Try}

case class Table(players: List[Player], deck: List[Card] = List(), currentPlayer: Int = 0) {

  val SB = 1
  val BB = 2

  def nextPlayer(): Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  def tryCurrentPlayerAct(input: Option[String], values: HashMap[Char, Set[Int]]): Try[Table] = {
    val activePlayer = players(currentPlayer)
    val newActivePlayerTry = input match {
      case Some("fold") => Success(activePlayer.fold())
      case Some("call") => Success(activePlayer.call(getHighestOverallBet()))
      case None => Success(activePlayer.actAsBot(getHighestOverallBet(), BB, values))
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

  def getCurrentPlayer(): Player = {
    this.players(this.currentPlayer)
  }

  def getHighestOverallBet(): Int = {
    players.map(player => player.currentBet).max
  }
}
