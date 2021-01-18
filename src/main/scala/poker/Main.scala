package poker

import java.io.{File, FileWriter, PrintWriter}

import poker.model.{Table, Dealer}
import poker.dsl.{TableDSL, PlayerDSL}

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random

object Main:

  @main def playGame(symbols: String) =
    new File("what_happened.txt").delete()
    val letterSymbols = symbols == "letters"
    val table = Table(players, getDeck(letterSymbols))
    val newTable = Dealer.handOutCards(table, Random.shuffle(table.deck))
    printText(playRounds(newTable, letterSymbols).getPrintableTable())
    printText("Game over!")

  def playRounds(table: Table, letterSymbols: Boolean): Table = {
    printText("------------- ROUND STARTS -------------")
    val newTable = playBettingRounds(table)
    printText(newTable.getPrintableWinning)
    Thread.sleep(10_000)
    printText("------------- ROUND ENDS -------------")

    val newNewTable = 
      Dealer.handOutCards(
        Dealer.collectHoleCards(
          Dealer.rotateButton(
            Dealer.payTheWinner(newTable)).resetBoard), Random.shuffle(getDeck(letterSymbols)))
    if newNewTable.shouldPlayNextRound then playRounds(newNewTable, letterSymbols)
    newNewTable
  } 
  
  @tailrec
  def playBettingRounds(table: Table): Table = 
    printText("------------- BETTING ROUND STARTS -------------")
    val newTable = Dealer.collectCurrentBets(
      playMoves(table
        .setFirstPlayerForBettingRound
        .resetPlayerActedThisBettingRound()))
    Thread.sleep(4_500)
    printText("------------- BETTING ROUND ENDS -------------")
    if newTable.shouldPlayNextBettingRound then
      playBettingRounds(Dealer.showBoardIfRequired(newTable.copy(currentBettingRound = table.currentBettingRound + 1)))
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
      Dealer.nextPlayer(newTryTable.get)

  @tailrec
  def getValidatedInput: String = 
    printText(s"Your options are:\nfold\ncheck\ncall\nraise 123 (with any number)\nall-in\n")
    StdIn.readLine() match {
      case input if isValidSyntax(input) => input
      case _ => getValidatedInput
    }

  def getMaybeInput(table: Table): Option[String] =
    val currentPlayer = table.getCurrentPlayer
    if currentPlayer.isHumanPlayer &&
      currentPlayer.isInRound &&
      !currentPlayer.isAllIn &&
      !(table.currentBettingRound == 0 && (table.isSB(currentPlayer) || table.isBB(currentPlayer))) then
      Some(getValidatedInput)
    else if currentPlayer.isInRound &&
      !currentPlayer.isAllIn then
        Thread.sleep(Random.nextInt(3_000) + 1_000)
        None
    else
      None

  def printText(text: String): Unit = 
    new PrintWriter(new FileWriter("what_happened.txt", true)) {
      write(text + "\n")
      close()
    }
    println(text)

  def isValidSyntax(input: String) = 
    """fold|check|call|raise \d+|all-in""".r.matches(input)