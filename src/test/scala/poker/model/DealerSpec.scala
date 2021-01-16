package poker.model

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.getDeck
import poker.dsl.PlayerDSL

class DealerSpec extends AnyWordSpec with Matchers:

  val threePlayers = List(
    Player("Alice").is(10).deep.hasCards("7h 2s"),
    Player("Bob").is(100).deep.hasCards("7c 2d"),
    Player("Charles").is(50).deep.hasCards("7s 2h")
  )

  "Given a table and a deck" when {
    val deck = getDeck()
    val table = Table(threePlayers, getDeck())
    "flop" should {
      "show 3 board cards and reduce the deck by three" in {
        val newTable = Dealer.flop(table)
        newTable.deck.size shouldBe (deck.size - 3)
        newTable.board shouldBe (List(
          Card('8', '♥'),
          Card('8', '♠'),
          Card('8', '♦')
        ))
      }
    }
    "turn" should {
      "show turn card and reduce the deck by one" in {
        val newTable = Dealer.flop(Dealer.turn(table))
        newTable.deck.size shouldBe (deck.size - 4)
        newTable.board shouldBe (List(
          Card('8', '♥'),
          Card('8', '♠'),
          Card('8', '♦'),
          Card('8', '♣')
        ))
      }
    }
    "river" should {
      "show river card and reduce the deck by one" in {
        val newTable = Dealer.river(Dealer.turn(Dealer.flop(table)))
        newTable.deck.size shouldBe (deck.size - 5)
        newTable.board shouldBe (List(
          Card('8', '♥'),
          Card('8', '♠'),
          Card('8', '♦'),
          Card('8', '♣'),
          Card('4', '♥')
        ))
      }
    }
  }

  "Given a table and betting round = 0" should {
    val table = Table(threePlayers, getDeck(), currentBettingRound = 0)
    "do nothing" in {
      Dealer.showBoardIfRequired(table).board.length shouldBe (0)
    }
  }

  "Given a table and betting round = 1" should {
    val table = Table(threePlayers, getDeck(), currentBettingRound = 1)
    "flop" in {
      Dealer.showBoardIfRequired(table).board.length shouldBe (3)
    }
  }

  "Given a table and betting round = 2" should {
    val table = Table(threePlayers, getDeck(), currentBettingRound = 2)
    "turn" in {
      Dealer.showBoardIfRequired(table).board.length shouldBe (1)
    }
  }

  "Given a table and betting round = 3" should {
    val table = Table(threePlayers, getDeck(), currentBettingRound = 3)
    "river" in {
      Dealer.showBoardIfRequired(table).board.length shouldBe (1)
    }
  }

  "Given a table with current player = 1 and currentBettingRound = 1" when {
    val table = Table(threePlayers, currentPlayer = 1, currentBettingRound = 1)
    "collectHoleCards" should {
      "return a table with players without cards" in {
        Dealer.collectHoleCards(table).players.exists(p =>
          p.holeCards.isDefined
        ) shouldBe (false)
      }
    }
  }