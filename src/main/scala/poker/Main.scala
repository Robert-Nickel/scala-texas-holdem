package main.scala.poker

import main.scala.poker.Main.{deck, table}
import main.scala.poker.model.Table
import poker.{InitHelper, PlayerDSL, PrintHelper, values}

import scala.Console.println
import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Random, Success}

object Main extends App {
  val startingStack = 200

  val symbols = List('h', 's', 'd', 'c')
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val positions = Vector((0, 8), (20, 8), (40, 8), (0, 16), (20, 16), (40, 16))
  val deck = Random.shuffle(InitHelper.createDeck(values, symbols))
  val table = Table(InitHelper.createPlayers(names, startingStack), deck)
  val validPlayerOptions = Set("fold", "call") // TODO: add raise X and all-in if its implemented

  @tailrec
  def playRound(table: Table): Table = {
    if (table.players.count(p => p.isInGame()) > 1) {
      Dealer.handOutCards(table.players, deck) match {
        case Failure(f) => println(f.getMessage)
          System.exit(0)
          table
        case Success((newPlayers, newDeck)) =>
          drawTable(table)
          // The input once per round seems odd -> shouldn't it be ~once per bettingRound
          val currentPlayer = table.getCurrentPlayer
          val input = currentPlayer.name match {
            case "You" => {
              Some(getValidatedInput())
            }
            case _ => None
          }
          val newTable = table match {
            case table if currentPlayer.isInRound() =>
              // ACT
              playBettingRound(input, table)
            case _ =>
              // SKIP
              table
          }

          // RECURSE
          if (newTable.currentBettingRound == 4 || newTable.players.count(p => p.isInRound()) == 1) {
            newTable
          } else {
            playRound(newTable.nextPlayer)
          }
        //playRound(table.copy(players = newPlayers, deck = newDeck))
      }
    } else {
      println("Game over")
      table
    }
  }

  @tailrec
  def playBettingRound(input: Option[String], table: Table): Table = {
    val maxCurrentBet = table.players.map(p => p.currentBet).max
    if (table.players.exists(p => p.currentBet != maxCurrentBet && p.isInRound())) {
      playMove(input, table)
    } else if (table.currentBettingRound < 4) {
      // TODO: collect current bets into pot
      val newTable = table.copy(currentBettingRound = table.currentBettingRound + 1)
      playBettingRound(input, newTable)
    } else {
      table
    }
  }

  @tailrec
  def playMove(input: Option[String], table: Table): Table = {
    table.tryCurrentPlayerAct(input, values) match {
      case Success(newTable) => newTable
      case _ => if (input.isEmpty) {
        playMove(None, table)
        // TODO: Try to get this in table class
      } else playMove(Some(getValidatedInput()), table)
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

  // println: Try Monad mit Success / Failure mit String fuer Main
  // readline: value in main auslesen & als argument uebergeben
  // Switch-case in validateInput (gibt zustand zurueck) zurueck
  // Main Loop bis gewuenschter Zustand erreicht
  def getValidatedInput(): String = {
    println(s"Your options are: $validPlayerOptions")
    StdIn.readLine() match {
      case input if validPlayerOptions.contains(input) => input
      case _ => getValidatedInput()
    }
  }
}