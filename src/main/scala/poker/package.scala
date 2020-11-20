import poker.model.{Card, Player}

import scala.collection.immutable.HashMap
import scala.util.Try

package object poker {
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val startingStack = 200
  val sb = 1
  val bb = 2
  val cardSymbols = List('h', 's', 'd', 'c')
  val cardValues: HashMap[Char, Set[Int]] = HashMap(
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
  val syntaxValidOptions = Set("fold", "call")

  def getPlayers: List[Player] = {
    names.map(name => Player(name, startingStack))
  }

  def getDeck: List[Card] = {
    (for {
      v <- cardValues
      s <- cardSymbols
    } yield Card(v._1, s)).toList
  }

  implicit class PlayerDSL(player: Player) {
    def is(stack: Int): PlayerDSL = PlayerDSL(player = player.copy(stack = stack))

    def are(stack: Int): PlayerDSL = is(stack)

    def deep(): Player = player

    def hasCards(cards: String): Player = player.copy(holeCards = Some((Card(cards(0), cards(1)), Card(cards(3), cards(4)))))

    def haveCards(cards: String): Player = hasCards(cards)

    def posts(blind: Int): Try[Player] = player.raise(blind, 0)

    def post(blind: Int): Try[Player] = posts(blind)

    def isInRound: Boolean = {
      player.holeCards.isDefined
    }

    def areInRound() = isInRound

    def isInGame(): Boolean = {
      player.stack > 0 || player.currentBet > 0
    }

    def areInGame(): Boolean = isInGame()

    // highest overall bet is not necessary when going all-in
    // TODO: handle failure case if shove is called with stack == 0
    def shoves(unit: Unit): Player = player.raise(player.stack, 0).get

    def isHumanPlayer: Boolean = {
      player.name == "You"
    }
  }
}