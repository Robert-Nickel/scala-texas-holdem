package main.scala.poker.model

import com.sun.net.httpserver.Authenticator.Failure

import scala.util
import scala.util.{Failure, Success, Try}

case class Table(players: List[Player], deck: List[Card] = List(), currentPlayer: Int = 0) {

  def nextPlayer(): Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  def currentPlayerAct(input: Option[String]): Try[Table] = {
    val activePlayer = players(currentPlayer)
    val newActivePlayerTry = input match {
      case Some("fold") => Success(activePlayer.fold())
      case Some("call") => Success(activePlayer.call(getHighestOverallBet()))
      case None => activePlayer.raise(2 * getHighestOverallBet() + 1, getHighestOverallBet())
      case _ => Success(activePlayer)
    }
    newActivePlayerTry match {
      case Success(newActivePlayer) => {
        val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
        Success(this.copy(players = newPlayers))
      }
      case _ => util.Failure(new Throwable("invalid move by player"))
    }
  }

  def getCurrentPlayer(): Player = {
    this.players(this.currentPlayer)
  }

  def getHighestOverallBet(): Int = {
    players.map(player => player.currentBet).max
  }
}
