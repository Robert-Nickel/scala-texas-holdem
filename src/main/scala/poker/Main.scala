package main.scala.poker

import main.scala.poker.model.{Player, Table}
import poker.{InitHelper, PlayerDSL, PrintHelper, cardSymbols, cardValues}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random

object Main extends App {
  val startingStack = 200
  val names = List("Amy", "Bob", "Mia", "Zoe", "Emi", "You")
  val validPlayerOptions = Set("fold", "call") // TODO: add raise X and all-in if its implemented

  val gameOverTable = playRounds(Table(
    InitHelper.createPlayers(names, startingStack),
    Random.shuffle(InitHelper.createDeck(cardValues, cardSymbols))))
  drawTable(gameOverTable)
  println("Game over!")

  @tailrec
  def playRounds(table: Table): Table = {
    val newTable = playBettingRounds(table)
    if (shouldPlayNextRound(newTable)) {
      playRounds(resetTable(newTable))
    } else {
      newTable
    }
  }

  @tailrec
  def playBettingRounds(table: Table): Table = {
    val newTable = playMoves(table)
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
    val newTryTable = table.tryCurrentPlayerAct(getMaybeInput(table))
    if (newTryTable.isFailure) {
      playMove(table)
    } else {
      val newTable = newTryTable.get.nextPlayer
      drawTable(newTable)
      newTable
    }
  }

  def shouldPlayNextRound(table: Table): Boolean = {
    table.players.count(p => p.isInGame()) > 1
  }

  def shouldPlayNextBettingRound(table: Table): Boolean = {
    table.currentBettingRound < 4 && table.players.count(p => p.isInRound()) > 1
  }

  def shouldPlayNextMove(table: Table): Boolean = {
    val maxCurrentBet = table.players.map(p => p.currentBet).max
    table.players.exists(player => player.currentBet != maxCurrentBet && player.isInRound())
  }

  def isHumanPlayer(player: Player): Boolean = {
    player.name == "You"
  }

  def getMaybeInput(table: Table): Option[String] = {
    val currentPlayer = table.getCurrentPlayer
    if (isHumanPlayer(currentPlayer) && currentPlayer.isInRound()) {
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
      table.copy(players = playersAndDeck._1, deck = playersAndDeck._2)
    }
  }

  def drawTable(table: Table): Unit = {
    for (_ <- 1 to 100) {
      println("");
    }
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