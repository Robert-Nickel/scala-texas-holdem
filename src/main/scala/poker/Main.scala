package main.scala.poker

import main.scala.poker.Dealer.{shouldPlayNextBettingRound, shouldPlayNextMove, shouldPlayNextRound}
import main.scala.poker.model.Table
import poker.{InitHelper, PlayerDSL, PrintHelper, cardSymbols, cardValues}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Random}

object Main extends App {
  val startingStack = 200
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val validPlayerOptions = Set("fold", "call") // TODO: add raise X and all-in if its implemented

  val gameOverTable = playRounds(resetTable(Table(
    InitHelper.createPlayers(names, startingStack),
    Random.shuffle(InitHelper.createDeck(cardValues, cardSymbols)))))
  drawTable(gameOverTable)
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
    drawTable(table)
    val newTryTable = table.tryCurrentPlayerAct(getMaybeInput(table))
    if (newTryTable.isFailure) {
      playMove(table)
    } else {
      drawTable(newTryTable.get)
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

  def drawTable(table: Table): Unit = {
    for (_ <- 1 to 40) {
      println("")
    }
    println(" " * (39 - (table.pot.toString.length / 2)) + s"Pot: ${table.pot}")
    println("")
    table.players.foreach(player => {
      val spacesAfterCurrentBet = 16 - player.currentBet.toString.length
      print(s"${player.currentBet}" + " " * spacesAfterCurrentBet)
    })
    println("")
    println("_" * 88)
    table.players.foreach(player => {
      print(s"${player.getHoleCardsString()}" + " " * 8)
    })
    println("")
    table.players.foreach(player => {
      print(s"${player.name} " + " " * 12)
    })
    println("")
    table.players.foreach(player => {
      val spacesAfterStack = 16 - player.stack.toString.length
      print(s"${player.stack}" + " " * spacesAfterStack)
    })
    println("")
    print(s"${PrintHelper.getCurrentPlayerUnderscore(table.currentPlayer)}")
    for (_ <- 1 to 4) {
      println("")
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