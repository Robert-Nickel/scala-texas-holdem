import poker.model.{Card, Player}

import scala.collection.immutable.HashMap

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
}