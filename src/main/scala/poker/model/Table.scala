package main.scala.poker.model

case class Table(players: List[Player], deck: List[Card] = List(), currentPlayer: Int = 0) {
  def nextPlayer(): Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  def currentPlayerAct(input: Option[String]): Table = {
    val activePlayer = players(currentPlayer)
    val newActivePlayer = input match {
      case Some("fold") => activePlayer.fold()
      case None => activePlayer.fold()
      // TODO: Handle "abc" case
    }
    val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
    this.copy(players = newPlayers)
  }

  def getCurrentPlayer(): Player = {
    this.players(this.currentPlayer)
  }
}
