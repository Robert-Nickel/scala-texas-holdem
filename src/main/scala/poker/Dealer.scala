package main.scala.poker

import main.scala.poker.model.{Card, Player}

import scala.util.{Failure, Success, Try}

object Dealer {
  def handOutCards(players: List[Player], deck: List[Card]): Try[(List[Player], List[Card])] = {
    (players.size, deck.size) match {
      case (playersize, _) if playersize == 0 => Success(players, deck)
      case (_, decksize) if decksize < players.size * 2 =>
        Failure(new Throwable("Not enough cards for remaining players."))
      case _ => Success((players.map(player => {
        val firstCard = deck.head
        val secondCard = deck.tail.head
        Player(player.name, player.stack, Some(firstCard, secondCard))
      }), deck.tail.tail))
    }
  }
}
