package poker.model

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.{bb, getDeck, sb}
import poker.dsl.{PlayerDSL, TableDSL}

import scala.util.Failure

class TableSpec extends AnyWordSpec with Matchers {

  val twoPlayers = List(
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')), currentBet = 10),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's')))
  )

  val threePlayers = List(
    Player("Alice").is(10).deep.hasCards("7h 2s"),
    Player("Bob").is(100).deep.hasCards("7c 2d"),
    Player("Charles").is(50).deep.hasCards("7s 2h")
  )

  "Given a Table with no specified current player" when {
    val table = Table(twoPlayers)
    "next player is called" should {
      "default currentPlayer = 0 and return a table with current player = 1" in {
        table.nextPlayer shouldBe (table.copy(currentPlayer = 1))
      }
    }
  }

  "Given a Table where the current player is not in the round" when {
    val table = Table(List(Player("Jack")))
    "tryCurrentPlayerAct" should {
      "skip" in {
        table.tryCurrentPlayerAct(None).get shouldBe (table)
      }
    }
  }

  "Given a table with current player = 1 and currentBettingRound = 1" when {
    val table = Table(twoPlayers, currentPlayer = 1, currentBettingRound = 1)
    "next player" should {
      "return a table with current player = 0" in {
        table.nextPlayer shouldBe (table.copy(currentPlayer = 0))
      }
    }
    "get current player" should {
      "return the current player" in {
        table.getCurrentPlayer.name shouldBe ("Jon")
      }
    }
    "get highest overall bet" should {
      "return 10" in {
        table.getHighestOverallBet shouldBe (10)
      }
    }
    "tryCurrentPlayerAct with input 'fold'" should {
      "return a table with active player has no cards" in {
        table
          .tryCurrentPlayerAct(Some("fold"))
          .get
          .players(1)
          .holeCards shouldBe (None)
      }
    }
    "tryCurrentPlayerAct with input 'call'" should {
      "return a table where active player has a reduced stack" in {
        table
          .tryCurrentPlayerAct(Some("call"))
          .get
          .players(1)
          .stack shouldBe (190)
      }
    }
    "tryCurrentPlayerAct with input 'raise 120'" should {
      "return a table where active player has stack = 120" in {
        val player = table.tryCurrentPlayerAct(Some("raise 120")).get.players(1)
        player.stack shouldBe (80)
        player.currentBet shouldBe (120)
      }
    }
    "tryCurrentPlayerAct with input 'all-in'" should {
      "return a table where active player has stack = 0" in {
        val player = table.tryCurrentPlayerAct(Some("all-in")).get.players(1)
        player.stack shouldBe (0)
        player.currentBet shouldBe (200)
      }
    }
    "tryCurrentPlayerAct with input 'check', but the first player's current bet is 10" should {
      "return a Failure" in {
        table.tryCurrentPlayerAct(Some("check")) shouldBe a[Failure[_]]
      }
    }
    "tryCurrentPlayerAct with no input" should {
      "return a table where the active player raises 5 bb" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet shouldBe (10)
      }
    }
    "tryCurrentPlayerAct with 'abc'" should {
      "return a Failure" in {
        table.tryCurrentPlayerAct(Some("abc")) shouldBe a[Failure[_]]
      }
    }
  }

  "Given a table with current player is SB and currentBettingRound = 0" when {
    val table = Table(threePlayers, currentPlayer = 1, currentBettingRound = 0)
    "tryCurrentPlayerAct" should {
      "post sb" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet shouldBe (sb)
      }
    }
    "tryCurrentPlayerAct with input" should {
      "post sb and ignore input" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet shouldBe (sb)
      }
    }
  }

  "Given table with highestOverallBet = 0" when {
    val table = Table(threePlayers, currentPlayer = 1, currentBettingRound = 1)
    "tryCurrentPlayerAct with input 'check'" should {
      "return a table where active player has checked" in {
        val player = table.tryCurrentPlayerAct(Some("check")).get.players(1)
        player.stack shouldBe (100)
        player.currentBet shouldBe (0)
      }
    }
  }

  "Given a table with current player is BB and currentBettingRound = 0" when {
    val table = Table(threePlayers, currentPlayer = 2, currentBettingRound = 0)
    "tryCurrentPlayerAct" should {
      "post bb" in {
        table.tryCurrentPlayerAct(None).get.players(2).currentBet shouldBe (bb)
      }
    }
    "tryCurrentPlayerAct with input" should {
      "post bb and ignore input" in {
        table.tryCurrentPlayerAct(None).get.players(2).currentBet shouldBe (bb)
      }
    }
  }

  "Given a table and players with current bet and flop and current player = 0" when {
    val table = Dealer.flop(Table(twoPlayers, getDeck()))
    "reset board" should {
      "leave an empty board" in {
        table.resetBoard.board.length shouldBe (0)
      }
    }
    "set the current player to SB" should {
      "set current player = 1" in {
        table.setFirstPlayerForBettingRound.currentPlayer shouldBe (1)
      }
    }
  }

  "Given table with players without cards" should {
    val table =
      Table(List(Player("Bernard") is 200 deep, Player("Arnold") is 50 deep))
    "give cards to the players" in {
      val newTable = table.handOutCards(getDeck())
      newTable.players.head.holeCards.isDefined shouldBe true
      newTable.players(1).holeCards.isDefined shouldBe true
      newTable.deck.length shouldBe (48)
    }
  }

  "Given table where all players have acted " should {
    val table = Table(players =
      List(
        Player("Bernard", hasActedThisBettingRound = true).hasCards("2♠ 7♥"),
        Player("Arnold", hasActedThisBettingRound = true)
      )
    )

    "reset the hasActedThisBettingRoundFlag" in {
      val newTable = table.resetPlayerActedThisBettingRound()
      newTable.players.exists(player =>
        player.hasActedThisBettingRound
      ) shouldBe (false)
    }
  }

  "Given table where everyone except the dealer have folded" should {
    val table = Table(
      List(
        Player("Amy").hasCards("Ah As"),
        Player("Bob the SB"),
        Player("Jim the BB")
      ),
      currentPlayer = 0
    )
    "set the dealer as current player" in {
      table.setFirstPlayerForBettingRound.currentPlayer shouldBe (0)
    }
  }

  "Given table where Jim is not in game" should {
    val table = Table(List(Player("Amy") is 200 deep, Player("Jim") is 0 deep))
    "handout cards to Amy but not to Jim" in {
      val players = table.handOutCards(getDeck()).players
      players(0).holeCards.isDefined shouldBe (true)
      players(1).holeCards.isDefined shouldBe (false)
    }
  }

  "Given table where Bob is all-in" should {
    val table = Table(
      List(
        Player("Bob", currentBet = 50) hasCards ("Ah Ac") is 0 deep
      )
    )
    "skip Bob" in {
      table
        .tryCurrentPlayerAct(None)
        .get
        .players
        .head
        .hasActedThisBettingRound shouldBe (true)
    }
  }

  "Given table where Alice has two pair" should {
    val table = Table(
      players =
        List(Player("Alice", holeCards = Some(Card('A', '♥'), Card('A', '♠')))),
      board = List(
        Card('K', '♥'),
        Card('K', '♠'),
        Card('2', '♣'),
        Card('6', '♣'),
        Card('9', '♣')
      )
    )
    "evaluate the hand name as 'two pair'" in {
      table.evaluate(table.players(0)).handName shouldBe ("two pairs")
    }
  }
}
