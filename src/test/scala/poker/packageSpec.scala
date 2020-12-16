package poker

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.{Card, Player, Table}

class packageSpec extends AnyWordSpec {

  "Given a table" should {
    val table = Table(
      players = List(Player("Gin"), Player("Tonic"), Player("Ice")),
      deck = getDeck,
      currentPlayer = 2,
      currentBettingRound = 2,
      pot = 1000000,
      board = List(Card('A', '♥'), Card('K', '♥'), Card('Q', '♥'), Card('J', '♥'), Card('T', '♥')),

    )
    "get a printable table" in {
      table.getPrintableTable() should be("\n                                       Pot 1000000\n                                  [A♥][K♥][Q♥][J♥][T♥]\n\n0               0               0               \n________________________________________________________________________________________\n                                                \nGin (D)         Tonic             Ice             \n0               0               0               \n                                ________\n")
    }
  }

  "Given a table at showdown" should {
    val table = Table(
      players = List(
        Player("Amy", holeCards = Some(Card('7','♣'), Card('J','♥')), stack=176),
        Player("Bob", holeCards = Some(Card('9','♠'), Card('6','♣')), stack=176)
      ),
      deck = getDeck,
      currentPlayer = 4,
      currentBettingRound = 3,
      pot = 100,
      board = List(Card('9', '♥'), Card('9', '♣'), Card('K', '♦'), Card('Q', '♠'), Card('8', '♣')),

    )
    "get a printable winning" in {
      table.getPrintableWinning should be("\n                                         Pot 100\n                                  [9♥][9♣][K♦][Q♠][8♣]\n\n0               0               \n________________________________________________________________________________________\n[7♣][J♥]        [9♠][6♣]        \nAmy (D)         Bob             \n176             176             \n                                                                ________\n\nBob wins 100 with three of a kind\n\n")
    }
  }

  "Given a table where only one player is in round" should {
    val table = Table(players = List(Player("A", holeCards = Some(Card('A', '♥'), Card('K', '♥'))), Player("B")))
    "return only one player is in round" in {
      table.isOnlyOnePlayerInRound shouldBe true
    }
  }

  "Given a table where two players are in round" should {
    val table = Table(players = List(
      Player("A", holeCards = Some(Card('A', '♥'), Card('K', '♥'))),
      Player("B", holeCards = Some(Card('Q', '♥'), Card('J', '♥')))))
    "return not only one player is in round" in {
      table.isOnlyOnePlayerInRound shouldBe false
    }
  }

  "Given a table where no player is in round" should {
    val table = Table(players = List(Player("A"), Player("B")))
    "return not one player is in round" in {
      table.isOnlyOnePlayerInRound shouldBe false
    }
  }

  "Given a table where more than one player is in the game" should {
    val table = Table(players = List(Player("A", stack = 100), Player("B", currentBet = 100)))
    "play next round" in {
      table.shouldPlayNextRound shouldBe true
    }
  }

  "Given a table where more only one player is in the game" should {
    val table = Table(players = List(Player("A", stack = 100), Player("B")))
    "not play next round" in {
      table.shouldPlayNextRound shouldBe false
    }
  }

  "Given a table with current betting round = 4 and two players who are in round" should {
    val table = Table(players = List(
      Player("A", holeCards = Some(Card('A', '♥'), Card('K', '♥'))),
      Player("B", holeCards = Some(Card('Q', '♥'), Card('J', '♥'))),
    ), currentBettingRound = 4)
    "not play next betting round" in {
      table.shouldPlayNextBettingRound shouldBe false
    }
  }

  "Given a table with current betting round = 2 and only one player who is in round" should {
    val table = Table(players = List(
      Player("A", holeCards = Some(Card('A', '♥'), Card('K', '♥'))),
      Player("B", holeCards = None),
    ), currentBettingRound = 2)
    "not play next betting round" in {
      table.shouldPlayNextBettingRound shouldBe false
    }
  }

  "Given a table with current betting round = 2 and only two players who are in round" should {
    val table = Table(players = List(
      Player("A", holeCards = Some(Card('A', '♥'), Card('K', '♥'))),
      Player("B", holeCards = Some(Card('Q', '♥'), Card('J', '♥'))),
    ), currentBettingRound = 2)
    "play next betting round" in {
      table.shouldPlayNextBettingRound shouldBe true
    }
  }

  "Given a table where all players have the same current bet and has not acted before" should {
    val table = Table(players = List(
      Player("A", currentBet = 10, holeCards = Some(Card('A', '♥'), Card('K', '♥')), hasActedThisBettingRound = true),
      Player("B", currentBet = 10, holeCards = Some(Card('Q', '♥'), Card('J', '♥')), hasActedThisBettingRound = false)))
    "not play next move" in {
      table.shouldPlayNextMove shouldBe true
    }
  }

  "Given a table where all players have the same current bet and acted before" should {
    val table = Table(players = List(
      Player("A", currentBet = 10, holeCards = Some(Card('A', '♥'), Card('K', '♥')), hasActedThisBettingRound = true),
      Player("B", currentBet = 10, holeCards = Some(Card('Q', '♥'), Card('J', '♥')), hasActedThisBettingRound = true)))
    "not play next move" in {
      table.shouldPlayNextMove shouldBe false
    }
  }

  "Given a table where NOT all players have the same current bet" should {
    val table = Table(players = List(
      Player("A", currentBet = 10, holeCards = Some(Card('A', '♥'), Card('K', '♥'))),
      Player("B", currentBet = 20, holeCards = Some(Card('Q', '♥'), Card('J', '♥')))))
    "play next move" in {
      table.shouldPlayNextMove shouldBe true
    }
  }
}