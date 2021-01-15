package poker

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.{Card, Player, Table}

class packageSpec extends AnyWordSpec {

  // "Given a table" should {
  //   val table = Table(
  //     players = List(Player("Gin"), Player("Tonic"), Player("Ice")),
  //     deck = getDeck(letters = true),
  //     currentPlayer = 2,
  //     currentBettingRound = 2,
  //     pot = 1_000_000,
  //     board = List(Card('A', 'h'), Card('K', 'h'), Card('Q', 'h'), Card('J', 'h'), Card('T', 'h')),

  //   )
  //   "get a printable table" in {
  //     table.getPrintableTable() shouldBe("\n                                       Pot 1000000\n                                  [Ah][Kh][Qh][Jh][Th]\n\n0               0               0               \n________________________________________________________________________________________\n                                                \nGin (D)         Tonic             Ice             \n0               0               0               \n                                ________\n")
  //   }
  // }

  // "Given a table at showdown" should {
  //   val table = Table(
  //     players = List(
  //       Player("Amy", holeCards = Some(Card('7','c'), Card('J','h')), stack=176),
  //       Player("Bob", holeCards = Some(Card('9','s'), Card('6','c')), stack=176)
  //     ),
  //     deck = getDeck(letters = true),
  //     currentPlayer = 4,
  //     currentBettingRound = 3,
  //     pot = 100,
  //     board = List(Card('9', 'h'), Card('9', 'c'), Card('K', 'd'), Card('Q', 's'), Card('8', 'c')),

  //   )
  //   "get a printable winning" in {
  //     table.getPrintableWinning shouldBe("\n                                         Pot 100\n                                  [9h][9c][Kd][Qs][8c]\n\n0               0               \n________________________________________________________________________________________\n[7c][Jh]        [9s][6c]        \nAmy (D)         Bob             \n176             176             \n                                                                ________\n\nBob wins 100 with three of a kind\n\n")
  //   }
  // }

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
      Player("A", stack = 10, currentBet = 10, holeCards = Some(Card('A', '♥'), Card('K', '♥')), hasActedThisBettingRound = true),
      Player("B", stack = 10, currentBet = 10, holeCards = Some(Card('Q', '♥'), Card('J', '♥')), hasActedThisBettingRound = false)))
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
      Player("A", stack = 10, currentBet = 10, holeCards = Some(Card('A', '♥'), Card('K', '♥'))),
      Player("B", stack = 10, currentBet = 20, holeCards = Some(Card('Q', '♥'), Card('J', '♥')))))
    "play next move" in {
      table.shouldPlayNextMove shouldBe true
    }
  }

  "Given a table where one player is All-In with less than maxCurrentBet" should {
    val table = Table(players = List(
      Player("A", currentBet = 150, holeCards = Some(Card('A', '♥'), Card('K', '♥')), stack = 50, hasActedThisBettingRound = true),
      Player("B", currentBet = 100, holeCards = Some(Card('Q', '♥'), Card('J', '♥')), hasActedThisBettingRound = true)
    ))
    "not play next move" in {
      table.shouldPlayNextMove shouldBe false
    }
  }
}