package main.scala.poker

import main.scala.poker.model.{Card, Player, Table}

import scala.util.Random

case class Dealer() {
  // TODO: extract randomness
  def shuffleDeck(deck: List[Card]): List[Card] = Random.shuffle(deck)

  def handOutCards(table: Table, deck: List[Card]): (Table, List[Card]) = {
    val firstCard = deck.head
    val remainingDeck = deck.tail
    val secondCard = remainingDeck.head
    (Table(table.players.map(player => Player(player.name, player.stack, (Option.apply(firstCard), Option.apply(secondCard))))),
      remainingDeck.tail)
  }
}
