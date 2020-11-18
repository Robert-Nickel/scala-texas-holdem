package poker.model

import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.{PlayerDSL, bb, sb, cardValues}

import scala.util.Failure

class TableSpec extends AnyWordSpec with Matchers {

  val players = List(
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')), currentBet = 10),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's'))))

  "Given a Table with no specified current player" when {
    val table = Table(players)
    "next player is called" should {
      "default currentPlayer = 0 and return a table with current player = 1" in {
        table.nextPlayer should be(table.copy(currentPlayer = 1))
      }
    }
  }

  "Given a table with current player = 1 and currentBettingRound = 1" when {
    val table = Table(players, currentPlayer = 1, currentBettingRound = 1)
    "next player" should {
      "return a table with current player = 0" in {
        table.nextPlayer should be(table.copy(currentPlayer = 0))
      }
    }
    "get current player" should {
      "return the current player" in {
        table.getCurrentPlayer.name should be("Jon")
      }
    }
    "get highest overall bet" should {
      "return 10" in {
        table.getHighestOverallBet should be(10)
      }
    }
    "tryCurrentPlayerAct with input 'fold'" should {
      "return a table with active player has no cards" in {
        table.tryCurrentPlayerAct(Some("fold")).get.players(1).holeCards should be(None)
      }
    }
    "tryCurrentPlayerAct with input 'call'" should {
      "return a table where active player has a reduced stack" in {
        table.tryCurrentPlayerAct(Some("call")).get.players(1).stack should be < 200
      }
    }
    "tryCurrentPlayerAct with no input" should {
      "return a table where the active player goes all-in" in {
        table.tryCurrentPlayerAct(None).get.players(1).stack should be(0)
      }
    }
    "tryCurrentPlayerAct with 'abc'" should {
      "return a Failure" in {
        table.tryCurrentPlayerAct(Some("abc")) shouldBe a[Failure[_]]
      }
    }
  }

  val threePlayers = List(Player("Alice").is(10).deep(), Player("Bob").is(100).deep(), Player("Charles").is(50).deep())
  "Given a table with current player is SB and currentBettingRound = 0" when {
    val table = Table(threePlayers, currentPlayer = 1, currentBettingRound = 0)
    "tryCurrentPlayerAct" should {
      "post sb" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet should be(sb)
      }
    }
    "tryCurrentPlayerAct with input" should {
      "post sb and ignore input" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet should be(sb)
      }
    }
  }

  "Given a table with current player is BB and currentBettingRound = 0" when {
    val table = Table(threePlayers, currentPlayer = 2, currentBettingRound = 0)
    "tryCurrentPlayerAct" should {
      "post bb" in {
        table.tryCurrentPlayerAct(None).get.players(2).currentBet should be(bb)
      }
    }
    "tryCurrentPlayerAct with input" should {
      "post bb and ignore input" in {
        table.tryCurrentPlayerAct(None).get.players(2).currentBet should be(bb)
      }
    }
  }
}
