package main.scala.poker

import java.io.{File, FileWriter, PrintWriter}

import main.scala.poker.Dealer.{shouldPlayNextBettingRound, shouldPlayNextMove, shouldPlayNextRound}
import main.scala.poker.model.Table
import poker.{InitHelper, PlayerDSL, cardSymbols, cardValues}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random

object Main extends App {
  new File("poker.txt").delete()
  val startingStack = 200
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val validPlayerOptions = Set("fold", "call") // TODO: add raise X and all-in if its implemented

  val gameOverTable = playRounds(resetTable(Table(
    InitHelper.createPlayers(names, startingStack),
    Random.shuffle(InitHelper.createDeck(cardValues, cardSymbols)))))
  printToConsole(gameOverTable)
  printToFile(gameOverTable)
  println("Game over!")

  @tailrec
  def playRounds(table: Table): Table = {
    println("------------- ROUND STARTS -------------")
    val newTable = playBettingRounds(table)
    val newNewTable = newTable.payTheWinner
    println("------------- ROUND ENDS -------------")
    if (shouldPlayNextRound(newNewTable)) {
      playRounds(newNewTable)
    } else {
      newNewTable
    }
  }

  @tailrec
  def playBettingRounds(table: Table): Table = {
    println("------------- BETTING ROUND STARTS -------------")
    val newTable = playMoves(table).collectCurrentBets.copy(currentPlayer = 1)
    println("------------- BETTING ROUND ENDS -------------")
    if (shouldPlayNextBettingRound(newTable)) {
      playBettingRounds(newTable.copy(currentBettingRound = table.currentBettingRound + 1))
    } else {
      newTable
    }
  }

  @tailrec
  def playMoves(table: Table): Table = {
    val newTable = playMove(table)
    if (shouldPlayNextMove(newTable)) {
      playMoves(newTable)
    } else {
      newTable
    }
  }

  @tailrec
  def playMove(table: Table): Table = {
    printToConsole(table)
    printToFile(table)
    val newTryTable = table.tryCurrentPlayerAct(getMaybeInput(table))
    if (newTryTable.isFailure) {
      playMove(table)
    } else {
      newTryTable.get.nextPlayer
    }
  }

  def getMaybeInput(table: Table): Option[String] = {
    val currentPlayer = table.getCurrentPlayer
    if (currentPlayer.isHumanPlayer &&
      currentPlayer.isInRound &&
      !(table.isSB(currentPlayer) || table.isBB(currentPlayer))) {
      Some(getValidatedInput())
    } else {
      None
    }
  }

  def resetTable(table: Table): Table = {
    val tryPlayersAndDeck = Dealer.handOutCards(table.players, Random.shuffle(InitHelper.createDeck(cardValues, cardSymbols)))
    if (tryPlayersAndDeck.isFailure) {
      System.exit(1)
      table
    } else {
      val playersAndDeck = tryPlayersAndDeck.get
      table.copy(players = playersAndDeck._1, deck = playersAndDeck._2, currentPlayer = 1)
    }
  }

  def printToConsole(table: Table): Unit = {
    for (_ <- 1 to 10) {
      println("")
    }
    println(table.getPrintableTable)
    for (_ <- 1 to 2) {
      println("")
    }
  }

  def printToFile(table: Table): Unit = {
    new PrintWriter(new FileWriter("poker.txt", true)) {
      write(table.getPrintableTable); close()
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