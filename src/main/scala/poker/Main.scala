package main.scala.poker

import main.scala.poker.model.{Player, Table}
import poker.{InitHelper, PrintHelper}

import scala.Console.println
import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.io.StdIn
import scala.util.{Failure, Random, Success, Try}

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
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val positions = Vector((0, 8), (20, 8), (40, 8), (0, 16), (20, 16), (40, 16))
  val deck = Random.shuffle(InitHelper.createDeck(values, symbols))
  val table = Table(InitHelper.createPlayers(names, startingStack), deck)
  val validPlayerOptions = Set("fold", "call", "raise", "all-in")

  Dealer.handOutCards(table.players, deck) match {
    case Failure(f) => println(f.getMessage())
      System.exit(0)
    case Success((newPlayers, newDeck)) =>
      nextMove(Table(newPlayers, newDeck))
  }

  @tailrec
  def nextMove(table: Table): Table = {
    // DRAW
    drawTable(table)

    val currentPlayer = table.getCurrentPlayer()
    val input = currentPlayer.name match {
      case "You" => {
        Some(getValidatedInput())
      }
      case _ => None
    }

    val newTable = table match {
      case table if currentPlayer.isInRound =>
        // ACT
        currentPlayerAct(input, table)
      case _ =>
        // SKIP
        table
    }

    // RECURSE
    if (newTable.players.exists(p => p.isInRound)) {
      nextMove(newTable.nextPlayer())
    } else {
      drawTable(newTable)
      println("Game over")
      newTable
    }
  }

  @tailrec
  def currentPlayerAct(input: Option[String], table: Table): Table = {
    table.currentPlayerAct(input, values) match {
      case Success(newTable) => newTable
      case _ => if (input.isEmpty) {
        currentPlayerAct(None, table)
      } else currentPlayerAct(Some(getValidatedInput()), table)
    }
  }

  def drawTable(table: Table): Unit = {
    for (_ <- 1 to 100) {
      println("");
    }
    table.players.foreach(player => {
      print(s"${player.getHoleCardsString()}\t\t")
    })
    println("")
    table.players.foreach(player => {
      print(s"${player.name} ${player.stack}\t\t\t")
    })
    println("")
    print(s"${PrintHelper.getCurrentPlayerUnderscore(table.currentPlayer)}")
    for (_ <- 1 to 6) {
      println("");
    }
  }

  def getValidatedInput(): String = {
    println(s"Your options are: $validPlayerOptions")
    StdIn.readLine() match {
      case input if validPlayerOptions.contains(input) => input
      case _ => getValidatedInput()
    }
  }
}