package main.scala.poker

import main.scala.poker.model.{Card, Player}

import scala.util.{Failure, Success, Try}

object Dealer {
  def handOutCards(players: List[Player], deck: List[Card]): Try[(List[Player], List[Card])] = {
    var varDeck = deck
    if (deck.size < players.size * 2) {
      Failure(new Throwable("Not enough cards for remaining players."))
    } else {
      Success((players.map(player => {
        val firstCard = varDeck.head
        varDeck = varDeck.tail
        val secondCard = varDeck.head
        varDeck = varDeck.tail
        Player(player.name, player.stack, (Option.apply(firstCard), Option.apply(secondCard)))
      }), varDeck))
    }
  }
}
