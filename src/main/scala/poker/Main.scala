package main.scala.poker

import java.io.{File, FileWriter, PrintWriter}

import main.scala.poker.Dealer.{shouldPlayNextBettingRound, shouldPlayNextMove, shouldPlayNextRound}
import poker.dsl.PlayerDSL.PlayerDSL
import poker.dsl.TablePrintingDSL.TablePrintingDSL
import poker.model.Table
import poker.{getDeck, getPlayers, syntaxValidOptions}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success}

object Main extends App {
  new File("poker.txt").delete()

  Table(getPlayers, getDeck).reset match {
    case Success(table) =>
      printText(playRounds(table).getPrintableTable)
      printText("Game over!")
    case Failure(throwable) => printText(throwable.getMessage)
  }

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

  @tailrec
  def getValidatedInput: String = {
    printText(s"Your options are: $syntaxValidOptions")
    StdIn.readLine() match {
      case input if syntaxValidOptions.contains(input.toLowerCase) => input
      case _ => getValidatedInput
    }
  }

  def getMaybeInput(table: Table): Option[String] = {
    val currentPlayer = table.getCurrentPlayer
    if (currentPlayer.isHumanPlayer &&
      currentPlayer.isInRound &&
      !(table.isSB(currentPlayer) || table.isBB(currentPlayer))) {
      Some(getValidatedInput)
    } else {
      None
    }
  }

  def printText(text: String): Unit = {
    new PrintWriter(new FileWriter("poker.txt", true)) {
      write(text);
      close()
    }
    println(text)
  }
}