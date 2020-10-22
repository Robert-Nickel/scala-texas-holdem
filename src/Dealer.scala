import scala.util.Random

case class Dealer(deck: List[Card], table: Table) {
  def shuffleDeck(deck: List[Card]): List[Card] = Random.shuffle(deck)

  // verteilen
  // table.players
}
