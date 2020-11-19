package poker

object PrintHelper {
  def getCurrentPlayerUnderscore(currentPlayer: Int): String = {
    s"${" " * 16 * currentPlayer}________"
  }
}
