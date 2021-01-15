import scala.collection.immutable.HashMap
import poker.model.{Player, Card, Table}
import scala.util.Try

package poker {
  val names = List("Amy", "Bob", "Dev", "Fox", "Udo", "You")
  val players = names.map(name => Player(name, 200))
  val sb = 1
  val bb = 2
  val cardSymbols = List('♥', '♠', '♦', '♣')
  val cardLetters = List('h', 's', 'd', 'c')
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
    ('A', Set(1, 14)))
  def getDeck(letters: Boolean = false): List[Card] = {
    (for {
      v <- cardValues
      s <- if letters then cardLetters else cardSymbols
    } yield Card(v._1, s)).toList
  }
}