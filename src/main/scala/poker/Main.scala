package main.scala.poker

import main.scala.poker.model.Table
import poker.InitHelper

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.io.StdIn
import scala.util.{Failure, Random, Success}

object Main extends App {
  val startingStack = 200
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
  val botNames = List("Alice", "Bob", "Charlie", "Dora", "Emil")
  val deck = Random.shuffle(InitHelper.createDeck(values, symbols))

  println("Welcome at the no-limit texas hold'em table. What's your name?")
  val name = getUserName()
  val names = botNames :+ name
  val table = Table(InitHelper.createPlayers(names, startingStack))

  Dealer.handOutCards(table.players, deck) match {
    case Failure(exception) => println(exception)
    case Success((newPlayers, newDeck)) =>
      println(s"Hello $name. Your starting stack is $startingStack$$. " +
        s"Your starting hand is ${newPlayers.find(player => player.name.equals(name)).get.getHoleCardsString()}")
  }

  @tailrec
  private def getUserName(): String = {
    val name = StdIn.readLine()
    if (botNames.contains(name)) {
      println("Sorry, another player with this name exists. Please enter another name.")
      getUserName()
    } else {
      name
    }
  }
}