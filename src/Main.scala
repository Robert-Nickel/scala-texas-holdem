import scala.collection.immutable.HashMap
import scala.io.StdIn

object Main extends App {
  val startingStack = 200 // TODO: config?

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
    ('A', Set(1,14))
  )
  val symbols = List('h', 's', 'd', 'c')
  val table = Table(List(Player("Alice", startingStack), Player("Bob", startingStack)))
  val dealer = Dealer(values.flatMap(v => symbols.map(s => Card(v._1, s))).toList, table)


  println("Welcome at the no-limit texas holdem table, whats your name?")
  val name = StdIn.readLine()
  println(s"Hello $name. Your starting stack is $startingStack$$.")
  println(s"The other players at the table are ${table.players.map(player => player.name).mkString(", ")}")



}