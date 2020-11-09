package poker

import main.scala.poker.Dealer
import main.scala.poker.model.{Card, Player}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Failure


class DealerSpec extends AnyWordSpec with Matchers {
  // TODO: Unabhaengigkeit der TEST  vs DRY
  // val deck = List(Card('T', 'h'), Card('T', 's'), Card('A', 'h'), Card('A', 's'))

 "Dealer" when {
   "given a list of players with no hole cards and a deck" should {
     val deck = List(Card('T', 'h'), Card('T', 's'), Card('A', 'h'), Card('A', 's'))
     val players = List(Player("Bob", 200, None))
     "return a list of players with hole cards and a reduced deck" in {
       val (newPlayers, newDeck)   = Dealer.handOutCards(players, deck).get
       newPlayers should be(List(Player("Bob", 200, Some(Card('T', 'h'), Card('T', 's')))))
       newDeck should be(List(Card('A', 'h'), Card('A', 's')))
     }
   }
   "given an empty list of players and a deck" should {
     val deck = List(Card('T', 'h'), Card('T', 's'), Card('A', 'h'), Card('A', 's'))
     "return an empty list of players and a unchanged deck" in {
       val (newPlayers, newDeck) = Dealer.handOutCards(List(), deck).get
       newPlayers shouldBe empty
       newDeck should be(deck)
     }
   }
   "given an list of players and a single card as deck" should {
     val deck = List(Card('A', 's'))
     "return a list of players n empty list of players and a unchanged deck" in {
       val tryMonad = Dealer.handOutCards(List(Player("Bob", 200, None)), deck)
       tryMonad shouldBe a [Failure[_]]
     }
   }
 }
}
