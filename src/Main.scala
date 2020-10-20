import scala.io.StdIn

object Main extends App {
  println("Welcome at the no-limit texas holdem table, whats your name?")
  val name = StdIn.readLine()
  val startingStack = 200 // TODO: config?
  println(s"Hello $name. Your starting stack is $startingStack$$.")
  val players = List(Player("Alice", startingStack), Player("Bob", startingStack))
  println(s"The other players at the table are ${players.map(player => player.name).mkString(", ")}")
}