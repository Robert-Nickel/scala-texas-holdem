import main.scala.poker.model.{Card, Player}

import scala.collection.immutable.HashMap

package object poker {

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

  implicit class PlayerDSL(player: Player) {
    def is(stack: Int): PlayerDSL = PlayerDSL(player = player.copy(stack = stack))

    def are(stack: Int): PlayerDSL = is(stack)

    def deep(): Player = player

    def hasCards(cards: String): Player = player.copy(holeCards = Some((Card(cards(0), cards(1)), Card(cards(3), cards(4)))))

    def haveCards(cards: String): Player = hasCards(cards)
  }

}
