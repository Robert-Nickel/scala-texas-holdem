package poker

import java.io.{File, FileWriter, PrintWriter}

import poker.dsl.TableDSL.TableDSL
import poker.model.Table

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Random, Success}

object Main extends App {
  new File("poker.txt").delete()

  Table(getPlayers, getDeck).handOutCards(Random.shuffle(getDeck)) match {
    case Success(table) =>
      printText(playRounds(table).getPrintableTable)
      printText("Game over!")
    case Failure(throwable) => printText(throwable.getMessage)
  }

  def playRounds(table: Table): Table = {
    printText("------------- ROUND STARTS -------------")
    val newTable = playBettingRounds(table)
    printText("------------- ROUND ENDS -------------")
    val newNewTable = newTable
      .payTheWinner
      .rotateButton
      .handOutCards(Random.shuffle(getDeck)) match {
      case Success(table) =>
        if (table.shouldPlayNextRound) {
          playRounds(table)
        } else {
          table
        }
      case Failure(throwable) =>
        printText(throwable.getMessage)
        newTable
    }
    newNewTable
  }

  @tailrec
  def playBettingRounds(table: Table): Table = {
    printText("------------- BETTING ROUND STARTS -------------")
    val newTable = playMoves(table.setCurrentPlayerToSB())
      .collectCurrentBets
    printText("------------- BETTING ROUND ENDS -------------")
    if (newTable.shouldPlayNextBettingRound) {
      playBettingRounds(newTable.copy(currentBettingRound = table.currentBettingRound + 1))
    } else {
      newTable
    }
  }

  @tailrec
  def playMoves(table: Table): Table = {
    val newTable = playMove(table)
    if (newTable.shouldPlayNextMove) {
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