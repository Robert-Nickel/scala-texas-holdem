package poker

import scala.util.Random

case class Dealer() {
  // TODO: extract randomness
  def shuffleDeck(deck: List[Card]): List[Card] = Random.shuffle(deck)

  def handOutCards(table: Table, deck: List[Card]) = {
    val firstCard = deck.head
    val remainingDeck = deck.tail
    val secondCard = remainingDeck.head
    (Table(table.players.map(player => Player(player.name, player.stack, (Option.apply(firstCard), Option.apply(secondCard))))),
      remainingDeck.tail)
  }
}
