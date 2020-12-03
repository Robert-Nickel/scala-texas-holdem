package poker.model

import poker.evaluator.{Evaluation, Evaluator}
import poker.{bb, sb}

import scala.util.{Failure, Random, Success, Try}

case class Table(players: List[Player],
                 deck: List[Card] = List(),
                 currentPlayer: Int = 0,
                 currentBettingRound: Int = 0,
                 pot: Int = 0,
                 board: List[Card] = List()
                ) {

  def collectHoleCards = copy(players = players.map(player => player.copy(holeCards = None)))

  def resetPlayerActedThisBettingRound(): Table = {
    this.copy(players = players.map(player => player.copy(hasActedThisBettingRound = false)))
  }

  def tryCurrentPlayerAct(maybeInput: Option[String]): Try[Table] = {
    val newActivePlayerTry = (players(currentPlayer), maybeInput) match {
      // skip
      case (activePlayer, _) if !activePlayer.isInRound => Success(activePlayer)
      case (activePlayer, _) if activePlayer.isAllIn => Success(activePlayer.copy(hasActedThisBettingRound = true))
      // small blind
      case (activePlayer, _) if this.isPreFlop && this.isSB(activePlayer) && activePlayer.currentBet < sb =>
        Thread.sleep(750)
        activePlayer.post(sb)
      // big blind
      case (activePlayer, _) if this.isPreFlop && this.isBB(activePlayer) && activePlayer.currentBet < bb =>
        Thread.sleep(750)
        activePlayer.post(bb)
      case (activePlayer, Some("fold")) => Success(activePlayer.fold())
      case (activePlayer, Some("check")) => activePlayer.check(this.getHighestOverallBet)
      case (activePlayer, Some("call")) => Success(activePlayer.call(this.getHighestOverallBet))
      case (activePlayer, Some("all-in")) => Success(activePlayer.allIn(this.getHighestOverallBet))
      case (activePlayer, Some(_)) if maybeInput.get.startsWith("raise ") => {
        val raiseAmount = maybeInput.get.split(" ")(1)
        activePlayer.raise(raiseAmount.toInt, this.getHighestOverallBet)
      }
      // bot player
      case (activePlayer, None) => Success(activePlayer.actAsBot(this.getHighestOverallBet, board))
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
    val winner = getTheWinner
    copy(
      players = players.patch(players.indexOf(), Seq(winner.copy(stack = winner.stack + pot)), 1),
      pot = 0
    )
  }

  def evaluate(player: Player): Evaluation = {
    Evaluator.eval(
      List(player.holeCards.get._1, player.holeCards.get._2)
        .appendedAll(board))
  }

  def getTheWinner: Player = {
    if (this.isOnlyOnePlayerInRound) {
      players.find(player => player.isInRound).get
    } else {
      players
        .filter(player => player.isInRound)
        .maxBy(player => evaluate(player).value)
    }
  }

  def rotateButton: Table = {
    copy(players = players.drop(1) ++ players.take(1))
  }

  def resetBoard: Table = {
    copy(board = List())
  }

  def handOutCards(deck: List[Card]): Table = {
    handOutCardsToPlayers(players, deck)
  }

  private def handOutCardsToPlayers(oldPlayers: List[Player], deck: List[Card], newPlayers: List[Player] = List()): Table = {
    (oldPlayers.size, deck.size) match {
      case (oldPlayerSize, _) if oldPlayerSize == 0 => Table(newPlayers, deck)
      case _ =>
        if(oldPlayers.head.isInGame) {
          handOutCardsToPlayers(oldPlayers.tail, deck.tail.tail, newPlayers :+ oldPlayers.head.copy(holeCards = Some(deck.head, deck.tail.head)))
        } else {
          handOutCardsToPlayers(oldPlayers.tail, deck, newPlayers :+ oldPlayers.head)
        }
    }
  }

  def setCurrentPlayerToPlayerWithWorstPosition: Table = {
    val activePlayersExceptDealer = players.filter(player => player.isInRound).tail
    val worstPosition = if (activePlayersExceptDealer.nonEmpty) {
      players.indexWhere(player => player.name == activePlayersExceptDealer.head.name)
    } else 0
    this.copy(currentPlayer = worstPosition)
  }

  def nextPlayer: Table = {
    this.copy(currentPlayer = (currentPlayer + 1) % players.length)
  }
}