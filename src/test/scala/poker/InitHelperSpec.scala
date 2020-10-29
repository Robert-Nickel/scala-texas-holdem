package poker

import main.scala.poker.model.{Card, Player}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.HashMap

class InitHelperSpec extends AnyWordSpec {

  "InitHelper" when {
    "given a list of values and symbols" should {
      val values = HashMap(('T', Set(10)), ('A', Set(1, 14)))
      val symbols = List('h', 's')
      "create a new deck" in {
        val deck = List(Card('T', 'h'), Card('T', 's'), Card('A', 'h'), Card('A', 's'))
        InitHelper.createDeck(values, symbols) should be(deck)
      }
    }
    "given a list of names and the startingStack" should {
      val names = List("Jack D.", "Jim B.")
      "create new Players with name and startingStack and no cards" in {
        val players = List(Player("Jack D.", 1000, (Option.empty, Option.empty)), Player("Jim B.", 1000, (Option.empty, Option.empty)))
        InitHelper.createPlayers(names, 1000) should be(players)
      }
    }
  }
}
