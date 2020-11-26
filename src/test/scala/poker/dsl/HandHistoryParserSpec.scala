package poker.dsl


import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import poker.model.{Action, Player, Verb}

class HandHistoryParserSpec extends AnyWordSpec {
  val parser = new HandHistoryParser

  "Given integers" should {
    "parse single char integer" in {
      parser.parse(parser.integer, "1").get should be(1)
    }
    "parse multi char integer" in {
      parser.parse(parser.integer, "12").get should be(12)
    }
    "parse integer followed by a string" in {
      parser.parse(parser.integer, "1a").get should be(1)
    }
  }

  "Given words" should {
    "return the first word" in {
      parser.parse(parser.word, "First Second").get should be("First")
    }
  }

  "Given stack amount" should {
    "parse stack amount as integer" in {
      parser.parse(parser.chips, "(45334 in chips)").get should be(45334)
    }

    "return a new player with name adevlupec and 53368 stack " in {
      parser.parse(parser.player, "Seat 1: adevlupec (53368 in chips)").get should be(Player("adevlupec", 53368))
    }
  }

  "Given lines that contain player name" should {
    "parse player name in table intro" in {
      parser.parse(parser.name, "Seat 1: adevlupec (53368 in chips)").get should be("adevlupec")
    }
    "parse player name action" in {
      parser.parse(parser.name, "adevlupec: checks").get should be("adevlupec")
    }
  }

  "Given player and action" should {
    "parse the call action and the amount" in {
      parser.parse(parser.action, "Dette32: calls 100").get should be(Action(Verb.CALL, Some(100)))
    }
    "parse the check action and no amount" in {
      parser.parse(parser.action, "Dette32: checks").get should be(Action(Verb.CHECK, None))
    }
    "parse the fold action and no amount" in {
      parser.parse(parser.action, "Dette32: folds").get should be(Action(Verb.FOLD, None))
    }
    "parse the raise action and the amount" in {
      parser.parse(parser.action, "Dette32: raises 50").get should be(Action(Verb.RAISE, Some(50)))
    }
  }
}