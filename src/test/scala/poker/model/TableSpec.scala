package poker.model

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.{bb, getDeck, sb}

import scala.util.Failure

class TableSpec extends AnyWordSpec with Matchers {

  val twoPlayers = List(
    Player("Bob", 200, Some(Card('A', 'h'), Card('A', 's')), currentBet = 10),
    Player("Jon", 200, Some(Card('K', 'h'), Card('K', 's'))))

  val threePlayers = List(
    Player("Alice").is(10).deep().hasCards("7h 2s"),
    Player("Bob").is(100).deep().hasCards("7c 2d"),
    Player("Charles").is(50).deep().hasCards("7s 2h"))

  "Given a Table with no specified current player" when {
    val table = Table(twoPlayers)
    "next player is called" should {
      "default currentPlayer = 0 and return a table with current player = 1" in {
        table.nextPlayer should be(table.copy(currentPlayer = 1))
      }
    }
  }

  "Given a Table where the current player is not in the round" when {
    val table = Table(List(Player("Jack")))
    "tryCurrentPlayerAct" should {
      "skip" in {
        table.tryCurrentPlayerAct(None).get should be(table)
      }
    }
  }

  "Given a table with current player = 1 and currentBettingRound = 1" when {
    val table = Table(twoPlayers, currentPlayer = 1, currentBettingRound = 1)
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
    "tryCurrentPlayerAct with input 'raise 120'" should {
      "return a table where active player has stack = 120" in {
        val player = table.tryCurrentPlayerAct(Some("raise 120")).get.players(1)
        player.stack should be(80)
        player.currentBet should be(120)
      }
    }
    "tryCurrentPlayerAct with input 'all-in'" should {
      "return a table where active player has stack = 0" in {
        val player = table.tryCurrentPlayerAct(Some("all-in")).get.players(1)
        player.stack should be(0)
        player.currentBet should be(200)
      }
    }
    "tryCurrentPlayerAct with input 'check', but the first player's current bet is 10" should {
      "return a Failure" in {
        table.tryCurrentPlayerAct(Some("check")) shouldBe a[Failure[_]]
      }
    }
    "tryCurrentPlayerAct with no input" should {
      "return a table where the active player raises 5 bb" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet should be(10)
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
        table.tryCurrentPlayerAct(None).get.players(1).currentBet should be(sb)
      }
    }
    "tryCurrentPlayerAct with input" should {
      "post sb and ignore input" in {
        table.tryCurrentPlayerAct(None).get.players(1).currentBet should be(sb)
      }
    }
  }

  "Given table with highestOverallBet = 0" when {
    val table = Table(threePlayers, currentPlayer = 1, currentBettingRound = 1)
    "tryCurrentPlayerAct with input 'check'" should {
      "return a table where active player has checked" in {
        val player = table.tryCurrentPlayerAct(Some("check")).get.players(1)
        player.stack should be(100)
        player.currentBet should be(0)
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

  "Given a table and a deck" when {
    val deck = getDeck
    val table = Table(threePlayers, getDeck)
    "flop" should {
      "show 3 board cards and reduce the deck by three" in {
        val newTable = table.flop
        newTable.deck.size should be(deck.size - 3)
        newTable.board should be(List(Card('8', '♥'), Card('8', '♠'), Card('8', '♦')))
      }
    }
    "turn" should {
      "show turn card and reduce the deck by one" in {
        val newTable = table.flop.turn
        newTable.deck.size should be(deck.size - 4)
        newTable.board should be(List(Card('8', '♥'), Card('8', '♠'), Card('8', '♦'), Card('8', '♣')))
      }
    }
    "river" should {
      "show river card and reduce the deck by one" in {
        val newTable = table.flop.turn.river
        newTable.deck.size should be(deck.size - 5)
        newTable.board should be(List(Card('8', '♥'), Card('8', '♠'), Card('8', '♦'), Card('8', '♣'), Card('4', '♥')))
      }
    }
  }

  "Given a table and betting round = 0" should {
    val table = Table(threePlayers, getDeck, currentBettingRound = 0)
    "do nothing" in {
      table.showBoardIfRequired.board.length should be(0)
    }
  }

  "Given a table and betting round = 1" should {
    val table = Table(threePlayers, getDeck, currentBettingRound = 1)
    "flop" in {
      table.showBoardIfRequired.board.length should be(3)
    }
  }

  "Given a table and betting round = 2" should {
    val table = Table(threePlayers, getDeck, currentBettingRound = 2)
    "turn" in {
      table.showBoardIfRequired.board.length should be(1)
    }
  }

  "Given a table and betting round = 3" should {
    val table = Table(threePlayers, getDeck, currentBettingRound = 3)
    "river" in {
      table.showBoardIfRequired.board.length should be(1)
    }
  }

  "Given a table and players with current bet and flop and current player = 0" when {
    val table = Table(twoPlayers, getDeck).flop

    "collect current bets" should {
      "set current bets to 0 and push it to the pot" in {
        val newTable = table.collectCurrentBets
        newTable.pot should be(10)
        newTable.players.head.currentBet should be(0)
      }
    }
    "rotate button" should {
      "make Jon the new dealer" in {
        table.rotateButton.players.head.name should be("Jon")
      }
    }
    "reset board" should {
      "leave an empty board" in {
        table.resetBoard.board.length should be(0)
      }
    }
    "set the current player to SB" should {
      "set current player = 1" in {
        table.setCurrentPlayerToPlayerWithWorstPosition.currentPlayer should be(1)
      }
    }
  }

  "Given table with players without cards" should {
    val table = Table(List(Player("Bernard"), Player("Arnold")))
    "give cards to the players" in {
      val newTable = table.handOutCards(getDeck)
      newTable.players.head.holeCards.isDefined shouldBe true
      newTable.players(1).holeCards.isDefined shouldBe true
      newTable.deck.length should be(48)
    }
  }

  "Given table after the river with 2 players in the round" should {
    val table = Table(players = List(
      Player("Bernard").hasCards("A♠ K♠"),
      Player("Arnold").hasCards("Q♦ Q♥")),
      board = List(Card('Q', '♠'), Card('J', '♠'), Card('T', '♠'), Card('Q', '♣'), Card('2', '♣')),
      pot = 1_000_000)
    "pay Bernard as the winner" in {
      val newTable = table.payTheWinner
      newTable.players.head.stack should be(1_000_000)
      newTable.pot should be(0)
    }
  }

  "Given table after the turn with only 1 player in the round" should {
    val table = Table(players = List(
      Player("Bernard").hasCards("2♠ 7♥"),
      Player("Arnold")),
      board = List(Card('Q', '♠'), Card('J', '♠'), Card('T', '♠'), Card('Q', '♣'), Card('2', '♣')),
      pot = 300_000)
    "pay Bernard as the winner" in {
      val newTable = table.payTheWinner
      newTable.players.head.stack should be(300_000)
      newTable.pot should be(0)
    }
  }

  "Given table where all players have acted " should {
    val table = Table(players = List(
      Player("Bernard", hasActedThisBettingRound = true).hasCards("2♠ 7♥"),
      Player("Arnold", hasActedThisBettingRound = true)))

    "reset the hasActedThisBettingRoundFlag" in {
      val newTable = table.resetPlayerActedThisBettingRound()
      newTable.players.exists(player => player.hasActedThisBettingRound) should be(false)
    }
  }

  "Given table everyone except the dealer have folded" should {
    val table = Table(List(
      Player("Amy").hasCards("Ah As"),
      Player("Bob the SB"),
      Player("Jim the BB")), currentPlayer = 0)
    "set the dealer as current player" in {
      table.setCurrentPlayerToPlayerWithWorstPosition.currentPlayer should be(0)
    }
  }
}
