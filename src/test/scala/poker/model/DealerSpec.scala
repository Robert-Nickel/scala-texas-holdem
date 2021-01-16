package poker.model

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.getDeck
import poker.dsl.PlayerDSL

class DealerSpec extends AnyWordSpec with Matchers:

  val threePlayers = List(
    Player("Alice", currentBet = 10).is(10).deep.hasCards("7h 2s"),
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
    "collect current bets" should {
      "set current bets to 0 and push it to the pot" in {
        val newTable = Dealer.collectCurrentBets(table)
        newTable.pot shouldBe (10)
        newTable.players.head.currentBet shouldBe (0)
      }
    }
    "rotate button" should {
      "make Jon the new dealer" in {
        Dealer.rotateButton(table).players.head.name shouldBe ("Bob")
      }
    }
  }

  "Given table after the river with 2 players in the round" should {
    val table = Table(
      players = List(
        Player("1"),
        Player("2"),
        Player("Bernard").hasCards("A♠ K♠"),
        Player("Arnold").hasCards("Q♦ Q♥"),
        Player("5"),
        Player("6")
      ),
      board = List(
        Card('Q', '♠'),
        Card('J', '♠'),
        Card('T', '♠'),
        Card('Q', '♣'),
        Card('2', '♣')
      ),
      pot = 1_000_000
    )
    "pay Bernard as the winner" in {
      val newTable = Dealer.payTheWinner(table)
      newTable.players(2).stack shouldBe (1_000_000)
      newTable.pot shouldBe (0)
    }
  }

  "Given table after the turn with only 1 player in the round" should {
    val table = Table(
      players = List(Player("Bernard").hasCards("2♠ 7♥"), Player("Arnold")),
      board = List(
        Card('Q', '♠'),
        Card('J', '♠'),
        Card('T', '♠'),
        Card('Q', '♣'),
        Card('2', '♣')
      ),
      pot = 300_000
    )
    "pay Bernard as the winner" in {
      val newTable = Dealer.payTheWinner(table)
      newTable.players.head.stack shouldBe (300_000)
      newTable.pot shouldBe (0)
    }
  }

  "Given table where Jim is not in game" should {
    val table = Table(List(Player("Amy") is 200 deep, Player("Jim") is 0 deep))
    "handout cards to Amy but not to Jim" in {
      val players = Dealer.handOutCards(table, getDeck()).players
      players(0).holeCards.isDefined shouldBe (true)
      players(1).holeCards.isDefined shouldBe (false)
    }
  }

  "Given table with players without cards" should {
    val table =
      Table(List(Player("Bernard") is 200 deep, Player("Arnold") is 50 deep))
    "give cards to the players" in {
      val newTable = Dealer.handOutCards(table, getDeck())
      newTable.players.head.holeCards.isDefined shouldBe true
      newTable.players(1).holeCards.isDefined shouldBe true
      newTable.deck.length shouldBe (48)
    }
  }