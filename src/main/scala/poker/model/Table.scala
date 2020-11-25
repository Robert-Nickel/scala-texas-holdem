package poker.model

import poker.{bb, sb}

import scala.util.{Failure, Success, Try}

case class Table(players: List[Player],
                 deck: List[Card] = List(),
                 currentPlayer: Int = 0,
                 currentBettingRound: Int = 0,
                 pot: Int = 0,
                 board: List[Card] = List()) {

  def tryCurrentPlayerAct(maybeInput: Option[String]): Try[Table] = {
    val newActivePlayerTry = (players(currentPlayer), maybeInput) match {
      // skip
      case (activePlayer, _) if !activePlayer.isInRound => Success(activePlayer)
      // small blind
      case (activePlayer, _) if this.isPreFlop && this.isSB(activePlayer) && activePlayer.currentBet < sb =>
        activePlayer.post(sb)
      // big blind
      case (activePlayer, _) if this.isPreFlop && this.isBB(activePlayer) && activePlayer.currentBet < bb =>
        activePlayer.post(bb)
      case (activePlayer, Some("fold")) => Success(activePlayer.fold())
      case (activePlayer, Some("call")) => Success(activePlayer.call(this.getHighestOverallBet))
      // bot player
      case (activePlayer, None) => Success(activePlayer.actAsBot(this.getHighestOverallBet))
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

  def showBoardIfRequired: Table = {
    if (currentBettingRound == 1) {
      flop
    } else if (currentBettingRound == 2) {
      turn
    } else if (currentBettingRound == 3) {
      river
    } else {
      this
    }
  }

  def flop: Table = {
    val newBoard = board :+ deck.head :+ deck.tail.head :+ deck.tail.tail.head
    val newDeck = deck.tail.tail.tail
    copy(board = newBoard, deck = newDeck)
  }

  def turn: Table = {
    copy(board = board :+ deck.head, deck = deck.tail)
  }

  def river: Table = {
    turn
  }

  def collectCurrentBets: Table = {
    this.copy(
      pot = pot + this.players.map(player => player.currentBet).sum,
      players = this.players.map(player => player.copy(currentBet = 0)))
  }

  def payTheWinner: Table = {
    val winner = if (this.isOnlyOnePlayerInRound) {
      players.find(player => player.isInRound).get
    } else {
      // TODO: go the right way to decide which hand wins
      players.maxBy(player => player.getHandValue())
    }
    copy(
      players = players.patch(players.indexOf(winner), Seq(winner.copy(stack = winner.stack + pot)), 1),
      pot = 0
    )
  }

  def rotateButton: Table = {
    copy(players = players.drop(1) ++ players.take(1))
  }

  def resetBoard: Table = {
    copy(board = List())
  }

  def handOutCards(deck: List[Card]): Table = {
    handOutCardsToAllPlayers(players, deck)
  }

  private def handOutCardsToAllPlayers(oldPlayers: List[Player], deck: List[Card], newPlayers: List[Player] = List()): Table = {
    (oldPlayers.size, deck.size) match {
      case (oldPlayerSize, _) if oldPlayerSize == 0 => Table(newPlayers, deck)
      case _ =>
        handOutCardsToAllPlayers(oldPlayers.tail, deck.tail.tail, newPlayers :+ oldPlayers.head.copy(holeCards = Some(deck.head, deck.tail.head)))
    }
  }

  def setCurrentPlayerToSB(): Table = {
    this.copy(currentPlayer = 1)
  }

  def nextPlayer: Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }
}