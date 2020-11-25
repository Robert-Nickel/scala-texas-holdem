
package poker.evaluator

import java.nio.{ByteBuffer, ByteOrder}

import poker.model.Card
import scala.collection.immutable.HashMap

// sources: https://github.com/LativDeveloper/PokerGym/blob/b51f440daa3ebe2b3bfe7498e6c28a19920d912e/src/poker/evaluator/HandEvaluator.java
// https://github.com/chenosaurus/poker-evaluator/blob/master/lib/PokerEvaluator.js
object Evaluator {
  val inputStream = getClass.getResourceAsStream("/HandRanks.dat")
  val handRanks = new Array[Byte](inputStream.available)
  val cardIntMap = HashMap(
    "2c" -> 1,
    "2d"-> 2,
    "2h"-> 3,
    "2s"-> 4,
    "3c"-> 5,
    "3d"-> 6,
    "3h"-> 7,
    "3s"-> 8,
    "4c"-> 9,
    "4d"-> 10,
    "4h"-> 11,
    "4s"-> 12,
    "5c"-> 13,
    "5d"-> 14,
    "5h"-> 15,
    "5s"-> 16,
    "6c"-> 17,
    "6d"-> 18,
    "6h"-> 19,
    "6s"-> 20,
    "7c"-> 21,
    "7d"-> 22,
    "7h"-> 23,
    "7s"-> 24,
    "8c"-> 25,
    "8d"-> 26,
    "8h"-> 27,
    "8s"-> 28,
    "9c"-> 29,
    "9d"-> 30,
    "9h"-> 31,
    "9s"-> 32,
    "tc"-> 33,
    "td"-> 34,
    "th"-> 35,
    "ts"-> 36,
    "jc"-> 37,
    "jd"-> 38,
    "jh"-> 39,
    "js"-> 40,
    "qc"-> 41,
    "qd"-> 42,
    "qh"-> 43,
    "qs"-> 44,
    "kc"-> 45,
    "kd"-> 46,
    "kh"-> 47,
    "ks"-> 48,
    "ac"-> 49,
    "ad"-> 50,
    "ah"-> 51,
    "as"-> 52
  )

  val symbolsMap = HashMap(
    '♥' -> 'h',
    '♦' -> 'd',
    '♠' -> 's',
    '♣' -> 'c'
  )

  val handTypes = List(
    "invalid hand",
    "high card",
    "one pair",
    "two pairs",
    "three of a kind",
    "straight",
    "flush",
    "full house",
    "four of a kind",
    "straight flush"
  )

  inputStream.read(handRanks)
  inputStream.close()

  private def evalCard(value: Int) = {
    val offset = value * 4
    ByteBuffer.wrap(handRanks, offset, handRanks.length - offset).order(ByteOrder.LITTLE_ENDIAN).getInt
  }

  def eval(cards: List[Card]):Evaluation  = {
    val intCards = cards.map(card => cardIntMap(card.value.toLower.toString + symbolsMap(card.symbol)))
    var p = 53
    intCards.foreach( card => p = evalCard(p + card))
    val handAddress = p >> 12

    Evaluation( handAddress, p & 0x00000fff, p, handTypes(if(handAddress <= 9) handAddress else 0))
  }

}