package poker

import java.io.{File, FileWriter, PrintWriter}
import poker.expected_value.ExpectedValueCalculator
import poker.kafka.EquityProducer
import poker.model.Table
import poker.stream.EquityCalculator

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random

object Main extends App {
  new File("poker.txt").delete()

  val table = Table(players, getDeck).handOutCards(Random.shuffle(getDeck))
  printText(playRounds(table).getPrintableTable())
  printText("Game over!")
  actorSystem.terminate()

  def playRounds(table: Table): Table = {
    printText("------------- ROUND STARTS -------------")
    val newTable = playBettingRounds(table)
    printText(newTable.getPrintableWinning)
    Thread.sleep(10000)
    printText("------------- ROUND ENDS -------------")

    val newNewTable = newTable
      .payTheWinner
      .rotateButton
      .resetBoard
      .collectHoleCards
      .handOutCards(Random.shuffle(getDeck))
    if (newNewTable.shouldPlayNextRound) {
      playRounds(newNewTable)
    }
    newNewTable
  }

  @tailrec
  def playBettingRounds(table: Table): Table = {
    printText("------------- BETTING ROUND STARTS -------------")
    val equities = if(table.currentBettingRound == 0) {
      EquityCalculator.calculatePreflopEquity(table.players.map(player => player.holeCards))
    } else {
      EquityCalculator.calculatePostflopEquity(table.players.map(player => player.holeCards), table.board)
    }

    (table.players,equities).zipped.foreach((player,equity) => EquityProducer.produceEquity(player.name, equity))

    val newTable = playMoves(table.setFirstPlayerForBettingRound.resetPlayerActedThisBettingRound())
      .collectCurrentBets
    Thread.sleep(4500)
    printText("------------- BETTING ROUND ENDS -------------")
    if (newTable.shouldPlayNextBettingRound) {
      playBettingRounds(newTable.copy(currentBettingRound = table.currentBettingRound + 1).showBoardIfRequired)
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
    printText(table.getPrintableTable())
    val maybeInput = getMaybeInput(table)
    val newTryTable = table.tryCurrentPlayerAct(maybeInput)
    if (newTryTable.isFailure) {
      playMove(table)
    } else {
      newTryTable.get.nextPlayer
    }
  }

  @tailrec
  def getValidatedInput: String = {
    printText(s"Your options are:\nfold\ncheck\ncall\nraise 123 (with any number)\nall-in")
    StdIn.readLine() match {
      case input if isValidSyntax(input) => input
      case _ => getValidatedInput
    }
  }

  def getMaybeInput(table: Table): Option[String] = {
    val currentPlayer = table.getCurrentPlayer
    if (currentPlayer.isHumanPlayer &&
      currentPlayer.isInRound &&
      !currentPlayer.isAllIn &&
      !(table.isSB(currentPlayer) || table.isBB(currentPlayer))) {
      Some(getValidatedInput)
    } else {
      Thread.sleep(Random.nextInt(3000) + 1000)
      None
    }
  }

  def printText(text: String): Unit = {
    new PrintWriter(new FileWriter("poker.txt", true)) {
      write(text + "\n")
      close()
    }
    println(text)
  }

  def isValidSyntax(input: String) = {
    input.matches("""fold|check|call|raise \d+|all-in""")
  }

  def printCallingExpectedValue() = {
    val equities = if (table.currentBettingRound == 0) {
      EquityCalculator.calculatePreflopEquity(table.players.map(player => player.holeCards))
    } else {
      EquityCalculator.calculatePostflopEquity(table.players.map(player => player.holeCards), table.board)
    }
    val callEV = ExpectedValueCalculator.calculateCallingEV(table, equities)
    printText(s"Expected value for calling: $callEV")
  }
}