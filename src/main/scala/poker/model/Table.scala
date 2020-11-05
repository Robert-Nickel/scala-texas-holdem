package main.scala.poker.model

case class Table(players: List[Player], deck: List[Card], currentPlayer: Int = 0) {
  def nextPlayer(): Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }
}
