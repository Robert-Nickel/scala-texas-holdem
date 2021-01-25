package poker.dsl

import poker.model.{Player, Card}
import scala.util.Try
import poker.evaluator.{Evaluation, Evaluator}

implicit class PlayerDSL(player: Player) {
  def is(stack: Int): PlayerDSL = PlayerDSL(player = player.copy(stack = stack))

  def are(stack: Int): PlayerDSL = is(stack)

  def deep: Player = player

  def hasCards(cards: String): Player = player.copy(holeCards =
    Some((Card(cards(0), cards(1)), Card(cards(3), cards(4))))
  )

  def haveCards(cards: String): Player = hasCards(cards)

  def posts(blind: Int): Try[Player] = player.raise(blind, 0)

  def post(blind: Int): Try[Player] = posts(blind)

  def isInRound: Boolean = player.holeCards.isDefined

  def areInRound = isInRound

  def isInGame: Boolean = player.stack > 0 || player.currentBet > 0

  def isAllIn: Boolean = player.stack == 0 && isInRound

  def areInGame: Boolean = isInGame

  def shoves(unit: Unit): Player = player.raise(player.stack, 0).get

  def isHumanPlayer: Boolean = player.name == "You"

  def evaluate(board: List[Card]): Evaluation =
    Evaluator.eval(
      List(player.holeCards.get._1, player.holeCards.get._2).appendedAll(board)
    )
}
