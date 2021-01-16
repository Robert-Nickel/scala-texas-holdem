package poker.dsl

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.{Card, Player, Table}

class PlayerDSLSpec extends AnyWordSpec {

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
      table.players(0).evaluate(table.board).handName shouldBe ("two pairs")
    }
  }
}
