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

  // TODO: raisePotEV https://www.888poker.com/magazine/strategy/ev-poker-beginner-advanced-complete-guide#CHECKING%20VS.%20BETTING
  /*
   EV(betting) =
   P(Villain Calls)P(WeWin)amt(Won) +
   P(VillainCalls)P(WeLose)amt(Lost) +
   P(Villain Fols)amt(won)

   EV = (20%)(90%)($250) + (20%)(10%)(-$100) + (80%)($150)
   pot = 50
   amountToPay 100

  //  Equity = amount to call
  // Pro Spieler
  def calculateExpectedValueBetting(probabilityCall: Double, equity: Double, amountToWin: Int, amountToPay: Int): Double = {
    probabilityCall * equity * (amountToWin + amountToPay * 2) + // Villain Calls we win
      probabilityCall * (1 - equity) * amountToPay * -1 + // Villain calls we lose
      1 - probabilityCall * amountToWin + amountToPay // Villain Folds we win
  }
*/
}
