package poker.expected_value

import poker.model.Table

object ExpectedValueCalculator {

  def calculateCallingEV(table: Table, equities: List[Double]): Double = {
    val currentPlayer = table.currentPlayer
    val potAndCurrentBets = table.pot + table.players.map(player => player.currentBet).sum
    if (table.players(currentPlayer).isInRound && equities(currentPlayer) != 0) {
      calculateExpectedValue(equities(currentPlayer),
        potAndCurrentBets,
        table.getHighestOverallBet - table.players(currentPlayer).currentBet)
    } else {
      0
    }
  }

  def calculateExpectedValue(equity: Double, amountToWin: Int, amountToPay: Int): Double = {
    BigDecimal((equity / 100 * amountToWin) - (1 - (equity / 100) * amountToPay))
      .setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

}
