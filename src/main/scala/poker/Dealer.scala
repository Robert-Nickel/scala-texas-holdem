package main.scala.poker

import main.scala.poker.model.{Card, Player}

import scala.util.Random

case class Dealer() {
  // TODO: extract randomness
  def shuffleDeck(deck: List[Card]): List[Card] = Random.shuffle(deck)

  def handOutCards(player: Player, deck: List[Card]): (Player, List[Card]) = {
    val playerWithOneCard = player.receiveCard(deck.head)
    val newDeck = deck.tail
    val playerWithTwoCards = playerWithOneCard.receiveCard(newDeck.head)
    (playerWithTwoCards, newDeck.tail)
  }
}
