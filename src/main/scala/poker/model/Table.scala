package main.scala.poker.model

import poker.{PlayerDSL, bb, cardValues, sb}

import scala.util.{Failure, Success, Try}

case class Table(players: List[Player],
                 deck: List[Card] = List(),
                 currentPlayer: Int = 0,
                 currentBettingRound: Int = 0,
                 pot: Int = 0) {

  def tryCurrentPlayerAct(humanInput: Option[String]): Try[Table] = {
    val newActivePlayerTry = (players(currentPlayer), humanInput) match {
      // skip
      case (activePlayer, _) if !activePlayer.isInRound() => Success(activePlayer)
      // small blind
      case (activePlayer, _) if isPreFlop && isSB(activePlayer) && activePlayer.currentBet < sb =>
        activePlayer.post(sb)
      // big blind
      case (activePlayer, _) if isPreFlop && isBB(activePlayer) && activePlayer.currentBet < bb =>
        activePlayer.post(bb)
      case (activePlayer, Some("fold")) => Success(activePlayer.fold())
      case (activePlayer, Some("call")) => Success(activePlayer.call(getHighestOverallBet))
      // bot player
      case (activePlayer, None) => Success(activePlayer.actAsBot(getHighestOverallBet))
      case _ => Failure(new Throwable("invalid move by player"))
    }
    newActivePlayerTry match {
      case Success(newActivePlayer) => {
        val newPlayers = players.patch(currentPlayer, Seq(newActivePlayer), 1)
        Success(this.copy(players = newPlayers))
      }
      case _ => Failure(new Throwable("invalid move by player"))
    }
  }

  def collectCurrentBets: Table = {
    this.copy(
      pot = pot + this.players.map(player => player.currentBet).sum,
      players = this.players.map(player => player.copy(currentBet = 0)))
  }

  def payTheWinner: Table = {
    val winner = getWinner
    this.copy(
      players = players.patch(players.indexOf(winner), Seq(winner.copy(stack = winner.stack + pot)), 1),
      pot = 0
    )
  }

  def getWinner: Player = {
    if (isOnlyOnePlayerInRound) {
      players.find(player => player.isInRound()).get
    } else {
      // TODO: go the right way to decide which hand wins
      players.maxBy(player => player.getHandValue())
    }
  }

  def nextPlayer: Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }

  def isOnlyOnePlayerInRound: Boolean = {
    players.count(p => p.isInRound()) == 1
  }

  def getCurrentPlayer: Player = {
    this.players(this.currentPlayer)
  }

  def getHighestOverallBet: Int = {
    players.map(player => player.currentBet).max
  }

  def isPreFlop: Boolean = currentBettingRound == 0

  def isSB(player: Player): Boolean = players(1) == player

  def isBB(player: Player): Boolean = players(2) == player

  def getPrintableTable: String = {
    val currentBets = players.flatMap(player => {
      val spacesAfterCurrentBet = 16 - player.currentBet.toString.length
      s"${player.currentBet}" + " " * spacesAfterCurrentBet
    }).mkString

    val holeCards = players.map(player => {
      s"${player.getHoleCardsString()}" + " " * 8
    }).mkString

    val names = players.map(player => {
      s"${player.name} " + " " * 12
    }).mkString

    val stacks = players.map(player => {
      val spacesAfterStack = 16 - player.stack.toString.length
      s"${player.stack}" + " " * spacesAfterStack
    }).mkString

    "\n" + " " * (39 - (pot.toString.length / 2)) + s"Pot: ${pot}\n" +
      currentBets + "\n" +
      "_" * 88 + "\n" +
      holeCards + "\n" +
      names + "\n" +
      stacks + "\n" +
      s"${" " * 16 * currentPlayer}________" + "\n"
  }
}
