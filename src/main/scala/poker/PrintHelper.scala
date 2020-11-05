package poker

object PrintHelper {
  def getCurrentPlayerUnderscore(currentTurn: Int): String = {
    s"${"\t\t\t\t" * currentTurn}________"
  }
}
