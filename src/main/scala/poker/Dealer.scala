package main.scala.poker

import main.scala.poker.model.{Card, Player, Table}
import poker.PlayerDSL

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object Dealer {

  // TODO: After betting round get currentBet from every player (and reset it to 0)

  @tailrec
  def handOutCards(players: List[Player], deck: List[Card], newPlayers: List[Player] = List()): Try[(List[Player], List[Card])] = {
    (players.size, deck.size) match {
      case (playersize, _) if playersize == 0 => Success(newPlayers, deck)
      case (_, decksize) if decksize < players.size * 2 =>
        Failure(new Throwable("Not enough cards for remaining players."))
      case _ =>
        handOutCards(players.tail, deck.tail.tail, newPlayers :+ players.head.copy(holeCards = Some(deck.head, deck.tail.head)))
    }
  }

  def shouldPlayNextRound(table: Table): Boolean = {
    table.players.count(p => p.isInGame()) > 1
  }

  def shouldPlayNextBettingRound(table: Table): Boolean = {
    table.currentBettingRound < 4 && !table.isOnlyOnePlayerInRound
  }

  def shouldPlayNextMove(table: Table): Boolean = {
    val maxCurrentBet = table.players.map(p => p.currentBet).max
    table.players.exists(player => player.currentBet != maxCurrentBet && player.isInRound())
  }
}
