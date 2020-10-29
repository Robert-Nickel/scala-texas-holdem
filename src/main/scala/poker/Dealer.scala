package main.scala.poker

import main.scala.poker.model.{Card, Player}

object Dealer {
 def handOutCards(players: List[Player], deck: List[Card]): (List[Player], List[Card]) = {
    var varDeck = deck
    (players.map(player => {
      val firstCard = varDeck.head
      varDeck = varDeck.tail
      val secondCard = varDeck.head
      varDeck = varDeck.tail
      Player(player.name, player.stack, (Option.apply(firstCard), Option.apply(secondCard)))
    }), varDeck)
  }
}
