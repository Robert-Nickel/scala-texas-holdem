package poker.model

import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.HashMap
import scala.util.Failure

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
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')), currentBet = 10),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's'))))

  "Given a Table with no specified current player" when {
    val table = Table(players)
    "next player is called" should {
      "default currentPlayer = 0 and return a table with current player = 1" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 1))
      }
    }
  }
  "Given a table with current player = 1" when {
    val table = Table(players, currentPlayer = 1)
    "next player" should {
      "return a table with current player = 0" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 0))
      }
    }
    "get current player" should {
      "return the current player" in {
        table.getCurrentPlayer().name should be("Jon")
      }
    }
    "get highest overall bet" should {
      "return 10" in {
        table.getHighestOverallBet() should be(10)
      }
    }
    "tryCurrentPlayerAct with input 'fold'" should {
      "return a table with active player has no cards" in {
        table.tryCurrentPlayerAct(Some("fold"), values).get.players(1).holeCards should be(None)
      }
    }
    "tryCurrentPlayerAct with input 'call'" should {
      "return a table where active player has a reduced stack" in {
        table.tryCurrentPlayerAct(Some("call"), values).get.players(1).stack should be < 200
      }
    }
    "tryCurrentPlayerAct with no input" should {
      "return a table where the active player goes all-in" in {
        table.tryCurrentPlayerAct(None, values).get.players(1).stack should be(0)
      }
    }
    "tryCurrentPlayerAct with 'abc'" should {
      "return a Failure" in {
        table.tryCurrentPlayerAct(Some("abc"), values) shouldBe a [Failure[_]]
      }
    }
  }
}
