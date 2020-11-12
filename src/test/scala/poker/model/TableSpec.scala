package poker.model

import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.HashMap

class TableSpec extends AnyWordSpec with Matchers {
  val values = HashMap(
    ('2', Set(2)),
    ('3', Set(3)),
    ('4', Set(4)),
    ('5', Set(5)),
    ('6', Set(6)),
    ('7', Set(7)),
    ('8', Set(8)),
    ('9', Set(9)),
    ('T', Set(10)),
    ('J', Set(11)),
    ('Q', Set(12)),
    ('K', Set(13)),
    ('A', Set(1, 14))
  )

  val players = List(
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's'))),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's'))))

  "A Table" when {
    "given players and a deck and no current player" should {
      val table = Table(players)
      "take the default of 0 and return a table with an updated current player" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 1))
      }
    }
    "given players and a deck and currentPlayer is 1" should {
      val table = Table(players, currentPlayer = 1)
      "take the default of 0 and return a table with an updated current player" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 0))
      }
    }
    "given players" should {
      val table = Table(players)
      "fold for the current player in" in {
        val newTable = table.currentPlayerAct(None, values).get
        val currentPlayer = newTable.currentPlayer
        val playerWithoutHoleCards = Seq(newTable.players(currentPlayer).copy(holeCards = None))
        newTable should be(table.copy(players.patch(currentPlayer, playerWithoutHoleCards, 1)))
      }
    }
    "given input is 'fold'" should {
      val table = Table(List(
        Player("Ali", 200, Some(Card('A', 'h'), Card('A', 's')))))
      "return a table where the player has no hole cards" in {
        val newTable = table.currentPlayerAct(Some("fold"), values)
        newTable.get.players.head.holeCards should be(None)
      }
    }
    "given no input" should {
      val table = Table(List(
        Player("Zoe", 200, Some(Card('A', 'h'), Card('A', 's')))))
      "return a table where the player has no hole cards" in {
        val newTable = table.currentPlayerAct(None, values)
        newTable.get.players.head.holeCards should be(None)
      }
    }

    "given players" should {
      val player = Player("Zoe", 200, Some(Card('A', 'h'), Card('A', 's')))
      val table = Table(List(player))
      "return the current player" in {
        val currentPlayer = table.getCurrentPlayer()
        currentPlayer should be(player)
      }
    }
  }
}
