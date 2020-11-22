
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.{Action, Player, Verb}

class HandHistoryParserTest extends AnyWordSpec {
  val parser = new HandHistoryParser

  "given Seat 1: adevlupec (53368 in chips)" should {

    "return an integer" in {
       parser.parse(parser.integer, "1").get should be(1)
       parser.parse(parser.integer, "12").get should be(12)
       parser.parse(parser.integer, "1a").get should be(1)
    }

    "return a word" in {
      parser.parse(parser.word, "First Second").get should be("First")
    }

    "return stack amount" in {
      parser.parse(parser.chips, "(45334 in chips)").get should be(45334)
    }

    "return player name" in {
      parser.parse(parser.name, "Seat 1: adevlupec (53368 in chips)").get should be("adevlupec")
      parser.parse(parser.name, "adevlupec: checks").get should be("adevlupec")
    }

    "return a new player with name adevlupec and 53368 stack " in {
       parser.parse(parser.player, "Seat 1: adevlupec (53368 in chips)").get should be(Player("adevlupec", 53368))
    }
  }

  "given Seat 2: Dette32 (10845 in chips)" should {
    "Dette32: calls 100" in {
      parser.parse(parser.action, "Dette32: calls 100").get should be(Action(Verb.CALL, Some(100)))
    }

  }



}
