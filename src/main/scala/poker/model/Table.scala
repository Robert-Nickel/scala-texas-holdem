package poker.model

import poker.dsl.TableDSL.{TableDSL}
import poker.{bb, sb}

import scala.util.{Failure, Success, Try}

case class Table(players: List[Player],
                 deck: List[Card] = List(),
                 currentPlayer: Int = 0,
                 currentBettingRound: Int = 0,
                 pot: Int = 0) {
  // TODO: add board like board: List[Card] = List() (3 entries after flop, 4 after turn and 5 after river)

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
    this.copy(
      players = players.patch(players.indexOf(winner), Seq(winner.copy(stack = winner.stack + pot)), 1),
      pot = 0
    )
  }

  def rotateButton: Table = {
    this.copy(players = players.drop(1) ++ players.take(1))
  }

  def handOutCards(deck: List[Card]): Try[Table] = {
    handOutCardsToAllPlayers(players, deck)
  }

  private def handOutCardsToAllPlayers(oldPlayers: List[Player], deck: List[Card], newPlayers: List[Player] = List()): Try[Table] = {
    (oldPlayers.size, deck.size) match {
      case (oldPlayerSize, _) if oldPlayerSize == 0 => Success(Table(newPlayers, deck))
      case (_, deckSize) if deckSize < oldPlayers.size * 2 =>
        Failure(new Throwable("Not enough cards for remaining players."))
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