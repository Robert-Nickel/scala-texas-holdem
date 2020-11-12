package main.scala.poker

import main.scala.poker.model.{Card, Player}

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
}
