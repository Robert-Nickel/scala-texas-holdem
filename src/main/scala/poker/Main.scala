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
  printText(gameOverTable.getPrintableTable)
  printText("Game over!")

  @tailrec
  def playRounds(table: Table): Table = {
    printText("------------- ROUND STARTS -------------")
    val newTable = playBettingRounds(table)
    val newNewTable = newTable.payTheWinner.rotateButton
    printText("------------- ROUND ENDS -------------")
    if (shouldPlayNextRound(newNewTable)) {
      playRounds(newNewTable)
    } else {
      newNewTable
    }
  }

  @tailrec
  def playBettingRounds(table: Table): Table = {
    printText("------------- BETTING ROUND STARTS -------------")
    val newTable = playMoves(table).collectCurrentBets.copy(currentPlayer = 1)
    printText("------------- BETTING ROUND ENDS -------------")
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
    printText(table.getPrintableTable)
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

  def printText(text: String): Unit = {
    new PrintWriter(new FileWriter("poker.txt", true)) {
      write(text);
      close()
    }
    println(text)
  }

  def getValidatedInput(): String = {
    printText(s"Your options are: $validPlayerOptions")
    StdIn.readLine() match {
      case input if validPlayerOptions.contains(input) => input
      case _ => getValidatedInput()
    }
  }
}