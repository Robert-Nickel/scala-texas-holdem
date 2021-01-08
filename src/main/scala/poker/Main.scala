package poker

import java.io.{File, FileWriter, PrintWriter}

import poker.model.Table

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random

object Main:
  
  @main def playGame() = 
    new File("poker.txt").delete()

    val table = Table(players, getDeck).handOutCards(Random.shuffle(getDeck))
    printText(playRounds(table).getPrintableTable())
    printText("Game over!")

  def playRounds(table: Table): Table = {
    printText("------------- ROUND STARTS -------------")
    val newTable = playBettingRounds(table)
    printText(newTable.getPrintableWinning)
    Thread.sleep(10_000)
    printText("------------- ROUND ENDS -------------")

    val newNewTable = newTable
      .payTheWinner
      .rotateButton
      .resetBoard
      .collectHoleCards
      .handOutCards(Random.shuffle(getDeck))
    if newNewTable.shouldPlayNextRound then playRounds(newNewTable)
    newNewTable
  } 
  
  @tailrec
  def playBettingRounds(table: Table): Table = 
    printText("------------- BETTING ROUND STARTS -------------")
    val newTable = playMoves(table.setFirstPlayerForBettingRound.resetPlayerActedThisBettingRound())
      .collectCurrentBets
    Thread.sleep(4_500)
    printText("------------- BETTING ROUND ENDS -------------")
    if newTable.shouldPlayNextBettingRound then
      playBettingRounds(newTable.copy(currentBettingRound = table.currentBettingRound + 1).showBoardIfRequired)
    else 
      newTable

  @tailrec
  def playMoves(table: Table): Table = 
    val newTable = playMove(table)
    if newTable.shouldPlayNextMove then
      playMoves(newTable)
    else 
      newTable

  @tailrec
  def playMove(table: Table): Table = 
    printText(table.getPrintableTable())
    val maybeInput = getMaybeInput(table)
    val newTryTable = table.tryCurrentPlayerAct(maybeInput)
    if newTryTable.isFailure then
      playMove(table)
    else 
      newTryTable.get.nextPlayer

  @tailrec
  def getValidatedInput: String = 
    printText(s"Your options are:\nfold\ncheck\ncall\nraise 123 (with any number)\nall-in")
    StdIn.readLine() match {
      case input if isValidSyntax(input) => input
      case _ => getValidatedInput
    }

  def getMaybeInput(table: Table): Option[String] =
    val currentPlayer = table.getCurrentPlayer
    if currentPlayer.isHumanPlayer &&
      currentPlayer.isInRound &&
      !currentPlayer.isAllIn &&
      !(table.isSB(currentPlayer) || table.isBB(currentPlayer)) then
      Some(getValidatedInput)
    else
      Thread.sleep(Random.nextInt(3_000) + 1_000)
      None

  def printText(text: String): Unit = 
    new PrintWriter(new FileWriter("poker.txt", true)) {
      write(text + "\n")
      close()
    }
    println(text)

  def isValidSyntax(input: String) = 
    """fold|check|call|raise \d+|all-in""".r.matches(input)