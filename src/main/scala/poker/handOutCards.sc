import main.scala.poker.Dealer
import main.scala.poker.Main.{deck, names, shuffledDeck, startingStack, symbols, values}
import main.scala.poker.model.{Card, Player, Table}

import scala.collection.immutable.HashMap

val values = HashMap(
  ('2', Set(2)),
  ('3', Set(3)),
  ('4', Set(4)),
  ('5', Set(5)),
  ('6', Set(6)),
  ('7', Set(7)),
  ('8', Set(8)),
  ('9', Set(9)),
  ('T', Set(10)),
  ('J', Set(11)),
  ('Q', Set(12)),
  ('K', Set(13)),
  ('A', Set(1, 14))
)
val symbols = List('h', 's', 'd', 'c')
val deck = values.flatMap(v => symbols.map(s => Card(v._1, s))).toList
val names = List("Alice", "Bob", "Charlie", "Dora", "Emil")
val table = Table(names.map(name => Player(name, 200, (Option.empty, Option.empty))))
val dealer = Dealer()

var shuffledDeck = dealer.shuffleDeck(deck)

val playersWithCards = table.players.map(player => {
  val firstCard = shuffledDeck.head
  shuffledDeck = shuffledDeck.tail
  val secondCard = shuffledDeck.head
  shuffledDeck = shuffledDeck.tail
  Player(player.name, player.stack, (Option.apply(firstCard), Option.apply(secondCard)))
})
