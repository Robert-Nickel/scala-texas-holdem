package poker.model

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.dsl.PlayerDSL


class BotSpec extends AnyWordSpec with Matchers {

  "Given a bot with hand value 12001" should {
    val handValue = 12_001
    val highestOverallBet = 50
    "raise 3 times the highestOverallBet" in {
      Bot
        .actPostFlop(Player("Bob", stack = 200), handValue, highestOverallBet)
        .currentBet shouldBe (3 * highestOverallBet)
    }
  }

  "Given a bot with hand value 9001" should {
    val handValue = 9_001
    val highestOverallBet = 50
      "call" in {
        Bot
          .actPostFlop(Player("Bob", stack = 200), handValue, highestOverallBet)
          .currentBet shouldBe (highestOverallBet)
      }
  }

  "Given a bot with hand value 1000" should {
    val handValue = 1_000
    val highestOverallBet = 50
    "fold" in {
      Bot.actPostFlop(Player("Bob", stack = 200), handValue, highestOverallBet).currentBet shouldBe (0)
    }
  }

  "Given a bot with hand value 12001" should {
    val handValue = 12_001
    val highestOverallBet = 50
    "fold if invalid raise" in {
      Bot
        .actPostFlop(Player("Bob", stack = 0), handValue, highestOverallBet)
        .isInRound shouldBe (false)
    }
  }
}
