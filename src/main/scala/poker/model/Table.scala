package main.scala.poker.model

case class Table(players: List[Player], deck: List[Card], currentPlayer: Int = 0) {
  def nextPlayer(): Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  def currentPlayerAct(): Table = {
    val activePlayer = players(currentPlayer)
    if (activePlayer.isInRound) {
      val newActivePlayer = activePlayer.act()
      val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
      this.copy(players = newPlayers)
    } else {
      val table = nextPlayer()
      table.currentPlayerAct()
    }
    // TODO exit condition
  }
}
