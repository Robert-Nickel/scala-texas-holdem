package main.scala.poker

import main.scala.poker.model.{Player, Table}
import poker.InitHelper

import scala.collection.immutable.HashMap
import scala.util.Random

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
  val names = List("Alice", "Bob", "Charlie", "Dora", "Emil", "You")
  val positions = Vector((0, 8), (20, 8), (40, 8), (0, 16), (20, 16), (40, 16))
  val deck = Random.shuffle(InitHelper.createDeck(values, symbols))
  val table = Table(InitHelper.createPlayers(names, startingStack))
  var isRunning = true

  Dealer.handOutCards(table.players, deck) match {
    case None => println("Something terrible happened.")
      System.exit(0)
    case Some((newPlayers, newDeck)) =>
      while (isRunning) {
        for (_ <- 1 to 100) {
          println("");
        }
        println("   |")
        println("   v")
        newPlayers.foreach(player => print(s"${player.name} ${player.stack}" + "          "))
        println("\n________")
        Thread.sleep(5_000)
      }
  }
}