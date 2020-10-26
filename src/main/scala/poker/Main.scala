package main.scala.poker

import main.scala.poker.model.{Card, Player, Table}

import scala.annotation.tailrec
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
    ('A', Set(1, 14))
  )
  val symbols = List('h', 's', 'd', 'c')
  val deck = values.flatMap(v => symbols.map(s => Card(v._1, s))).toList

  val botNames = List("Alice", "Bob", "Charlie", "Dora", "Emil")
  println("Welcome at the no-limit texas holdem table. What's your name?")

  val name = getUserName()
  val names = botNames :+ name

  val table = Table(names.map(name => Player(name, startingStack, (Option.empty, Option.empty))))
  val dealer = Dealer()
  val shuffledDeck = dealer.shuffleDeck(deck)

  /*def distributeCards(players: List[Player], deck: List[Card]): Unit = {
    val remainingPlayers = players.filter( p => p.holeCards._1.isEmpty || p.holeCards._2.isEmpty)
    table.players.map(player => {
      val (player, newDeck) = dealer.handOutCards(player, deck)
    })
    distributeCards(remainingPlayers, newDeck)

    //val (player, newDeck) = dealer.handOutCards(players.head, deck)
  }*/
  /*
table.players.(player => {
  val (newPlayer, newDeck) = dealer.handOutCards(player, shuffledDeck)
})

//val (newTable, newDeck) = dealer.handOutCards(table, shuffledDeck)
//newTable.players.map(player => println(s"${player.name} joined the table"))

val player, newDeck = table.players
  .map(player => dealer.handOutCards(player, deck))
 */

  println(s"Hello $name. Your starting stack is $startingStack$$. " +
    s"Your starting hand is ${newTable.players.find(player => player.name.equals(name)).get.getHoleCardsString()}")
  // TODO: Use worksheet for the beginning
  println(newTable.players)

  @tailrec
  def getUserName(): String = {
    val name = StdIn.readLine()
    if (botNames.contains(name)) {
      println("Sorry, another player with this name exists. Please enter another name.")
      getUserName()
    } else {
      name
    }
  }
}