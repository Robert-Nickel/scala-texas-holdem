package poker.model

import main.scala.poker.model.{Card, Player, Table}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class TableSpec extends AnyWordSpec with Matchers {
  val players = List(
    Player("Bob", 200, (Option.apply(Card('A', 'h')), Option.apply(Card('A', 's')))),
    Player("Jon", 200, (Option.apply(Card('K', 'h')), Option.apply(Card('K', 's')))))

  "A Table" when {
    "given players and a deck and no current player" should {
      val table = Table(players, List(Card('Q', 'h')))
      "take the default of 0 and return a table with an updated current player" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 1))
      }
    }
    "given players and a deck and currentPlayer is 1" should {
      val table = Table(players, List(Card('Q', 'h')), 1)
      "take the default of 0 and return a table with an updated current player" in {
        table.nextPlayer() should be(table.copy(currentPlayer = 0))
      }
    }
  }
}
