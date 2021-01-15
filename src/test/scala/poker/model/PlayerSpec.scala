package poker.model
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker._
import poker.dsl.{PlayerDSL, TableDSL}
import poker.evaluator.Evaluator.evalHoleCards

import scala.language.postfixOps
import scala.util.Failure

class PlayerSpec extends AnyWordSpec with BeforeAndAfterAll with Matchers {

  "Given a Player with name 'You', Stack is 200 and cards are Ah As" when {
    val player = (Player("You") are 200 deep) haveCards "Ah As"
    "hole cards are Ah, As and getHoleCardsAsString is called" should {
      "return [Ah][As]" in {
        player.getHoleCardsString() shouldBe ("[Ah][As]")
      }
    }
    "holding hole cards" should {
      "return areInRound = true" in {
        player.areInRound shouldBe (true)
      }
      "return isInGame = true" in {
        player.isInGame shouldBe (true)
      }
    }
    "is human player is called" in {
      player.isHumanPlayer shouldBe (true)
    }
  }
  "Given a Player with name 'You', and no hole cards" when {
    val you = Player("You")
    "areInRound is called" should {
      "return areInRound = false" in {
        you.areInRound shouldBe (false)
      }
    }
  }

  "Given a Player with name 'You', Stack is 0 and CurrentBet is 0" when {
    val you = (Player("You") are 0 deep)
    "areInGame is called" should {
      "return areInGame = false" in {
        you.areInGame shouldBe (false)
      }
    }
    "raise is called" should {
      "return a failure" in {
        you.raise(50, 0) shouldBe a[Failure[_]]
      }
    }
  }

  "Given a Player with name 'Ali'" when {
    val ali = (Player("Ali") is 200 deep) hasCards "Ah As"
    "hole cards are Ah, As and getHoleCardsAsString is called" should {
      "return [xx][xx]" in {
        ali.getHoleCardsString() shouldBe ("[xx][xx]")
      }
    }
    "holding hole cards" should {
      "return isInRound = true" in {
        ali.isInRound shouldBe (true)
      }
    }
    "fold" should {
      "return isInRound = false" in {
        ali.fold().isInRound shouldBe (false)
      }
    }
    "is human player is called" in {
      ali.isHumanPlayer shouldBe (false)
    }
  }

  "Given a Player with name 'Ali' and no hole cards" when {
    val ali = (Player("Ali") is 200 deep)
    "getHoleCardsAsString is called" should {
      "return whitespaces" in {
        ali.getHoleCardsString() shouldBe ("        ")
      }
    }
  }

  "Given a Player with name 'Ali' with aces" when {
    val ali = (Player("Ali") is 200 deep).hasCards("Ah As")
    "actAsBot is called" should {
      "call, if a 5BB raise is smaller than double the highestOverallBet" in {
        ali.actAsBot(8).currentBet shouldBe (8)
      }
    }
  }

  "Given a Player with flopped quads" when {
    val player = (Player("Timon") is 200 deep).hasCards("A♥ A♠")
    val board = List(Card('A', '♣'), Card('A', '♦'), Card('2', '♦'))
    "actAsBot is called" should {
      "shove all-in" in {
        player.actAsBot(20, board).currentBet shouldBe (200)
      }
    }
  }

  "Given a Player with name 'Ali' with Q9" when {
    val ali = (Player("Ali") is 200 deep).hasCards("Qh 9s")
    "actAsBot is called" should {
      "call, if a 3BB raise is smaller than double the highestOverallBet" in {
        ali.actAsBot(4).currentBet shouldBe (4)
      }
    }
  }

  "Given is in round and has a stack of 200" when {
    val bob = (Player("Bob") is 200 deep) hasCards "Ah As"
    "calls 50" should {
      "return bob with stack = 150 and currentBet = 50" in {
        val newPlayer = bob.call(50).get
        newPlayer.stack shouldBe (150)
        newPlayer.currentBet shouldBe (50)
      }
    }
    "calls where the highest overall bet is 300" should {
      "return bob with stack = 0 and currentBet = 200" in {
        val newBob = bob.call(300).get
        newBob.stack shouldBe (0)
        newBob.currentBet shouldBe (200)
      }
    }
    "raises 50 where highest overall bet is 20" should {
      "return bob with stack = 150 and currentBet = 50" in {
        val newBob = bob.raise(50, 20).get
        newBob.stack shouldBe (150)
        newBob.currentBet shouldBe (50)
      }
    }
    "raises 200 where highest overall bet is 150" should {
      "return bob with stack = 0 and currentBet = 200" in {
        val newBob = bob.raise(200, 150).get
        newBob.stack shouldBe (0)
        newBob.currentBet shouldBe (200)
      }
    }
    "raises 100 where highest overall bet is 80" should {
      "fail, because the raise is not high enough" in {
        val newBob = bob.raise(100, 80)
        newBob shouldBe a[Failure[_]]
      }
    }
    "shoves all-in with 200" should {
      "return bob with stack = 0 and currentBet = 200" in {
        val newBob = bob shoves ()
        newBob.stack shouldBe (0)
        newBob.currentBet shouldBe (200)
      }
    }

    "act as bot with aces" should {
      "raise 5 bb" in {
        val newBob = bob.actAsBot(4)
        newBob.currentBet shouldBe (10)
      }
    }

    "player posts sb" should {
      "have reduced stack" in {
        val newBob = bob.posts(sb).get
        newBob.stack shouldBe (199)
      }
    }

    "you post bb" should {
      "have reduced stack" in {
        val you = (Player("You") are 200 deep) haveCards "Ah As"
        val newYou = you.post(bb).get
        newYou.stack shouldBe (198)
      }
    }
  }

  "Given 9Qo resulting in a hand value above 20" when {
    val bob = (Player("Bob") is 200 deep) hasCards "Qh 9s"
    "act as bot" should {
      "raise 3 BB" in {
        bob.actAsBot(0).currentBet shouldBe (6)
      }
    }
  }

  "Given 37o and a highest overall bet of 2 BB" when {
    val bob = (Player("Bob") is 200 deep) hasCards "3h 7s"
    "act as bot" should {
      "call" in {
        bob.actAsBot(4).currentBet shouldBe (4)
      }
    }
  }

  "Given less than 10 BB left" when {
    val bob = (Player("Bob") is 16 deep) hasCards "3h 7s"
    "act as bot" should {
      "all-in" in {
        bob.actAsBot(4).currentBet shouldBe (16)
      }
    }
  }

  "Given less than 10 BB left" when {
    val bob = (Player("Bob") is 16 deep) hasCards "3h 7s"
    "act as bot with 37o is called" should {
      "all-in" in {
        bob.actAsBot(4).currentBet shouldBe (16)
      }
    }
  }

  "Given 46o and a stack of 200 " when {
    val bob = (Player("Bob") is 200 deep) hasCards "4h 6s"
    "act as bot is called" should {
      "fold" in {
        val newBob = bob.actAsBot(20)
        newBob.currentBet shouldBe (0)
        newBob.stack shouldBe (200)
      }
    }
  }

  // TODO: This does not belong to the PlayerSpec anymore
  "Given players with different hole cards" when {
    "hand value is calculated" should {
      "return 36" in {
        evalHoleCards(Some((Card('A', 'h'), Card('A', 's')))) shouldBe (36)
      }
      "return 39" in {
        evalHoleCards(Some((Card('A', 'h'), Card('K', 'h')))) shouldBe (39)
      }
      "return 30" in {
        evalHoleCards(Some((Card('A', 'h'), Card('Q', 's')))) shouldBe (30)
      }
      "return 0 when jim has no cards" in {
        evalHoleCards(None) shouldBe (0)
      }
    }
  }

  "Given a player (bot) with hand value 12001" should {
    val handValue = 12_001
    val highestOverallBet = 50
    val bob = (Player("Bob") is 200 deep)
    "raise 3 times the highestOverallBet" in {
      bob
        .actPostFlop(handValue, highestOverallBet)
        .currentBet shouldBe (3 * highestOverallBet)
    }
  }

  "Given a player (bot) with hand value 9001" should {
    val handValue = 9_001
    val highestOverallBet = 50
    val bob = (Player("Bob") is 200 deep)
    "call" in {
      bob
        .actPostFlop(handValue, highestOverallBet)
        .currentBet shouldBe (highestOverallBet)
    }
  }

  "Given a player (bot) with hand value 1000" should {
    val handValue = 1_000
    val highestOverallBet = 50
    val bob = (Player("Bob") is 200 deep)
    "fold" in {
      bob.actPostFlop(handValue, highestOverallBet).currentBet shouldBe (0)
    }
  }

  "Given a player (bot) with hand value 12001" should {
    val handValue = 12_001
    val highestOverallBet = 50
    val bob = (Player("Bob") is 0 deep)
    "fold if invalid raise" in {
      bob.actPostFlop(handValue, highestOverallBet).isInRound shouldBe (false)
    }
  }
}
