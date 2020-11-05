package main.scala.poker.model

case class Table(players: List[Player], deck: List[Card] = List(), currentPlayer: Int = 0) {
  def nextPlayer(): Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  // TODO: change to more general currentPlayerAct()
  def currentPlayerFold(): Table = {
    val activePlayer = players(currentPlayer)
    if (activePlayer.isInRound) {
      // TODO: replace with more general act()
      val newActivePlayer = activePlayer.fold()
      val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
      this.copy(players = newPlayers)
    } else {
      if (players.exists(p => p.isInRound)) {
        val table = nextPlayer()
        table.currentPlayerFold()
      } else {
        this
      }

    }
    // TODO exit condition
  }
}
