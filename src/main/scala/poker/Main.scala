package main.scala.poker

import main.scala.poker.model.{Player, Table}
import poker.{InitHelper, PrintHelper, values}

import scala.Console.println
import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.io.StdIn
import scala.util.{Failure, Random, Success, Try}

object Main extends App {
  val startingStack = 200

  val symbols = List('h', 's', 'd', 'c')
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val positions = Vector((0, 8), (20, 8), (40, 8), (0, 16), (20, 16), (40, 16))
  val deck = Random.shuffle(InitHelper.createDeck(values, symbols))
  val table = Table(InitHelper.createPlayers(names, startingStack), deck)
  val validPlayerOptions = Set("fold", "call") // TODO: add raise X and all-in if its implemented

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
        safeCurrentPlayerAct(input, table)
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
  def safeCurrentPlayerAct(input: Option[String], table: Table): Table = {
    table.tryCurrentPlayerAct(input, values) match {
      case Success(newTable) => newTable
      case _ => if (input.isEmpty) {
        safeCurrentPlayerAct(None, table)
        // TODO: Try to get this in table class
      } else safeCurrentPlayerAct(Some(getValidatedInput()), table)
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