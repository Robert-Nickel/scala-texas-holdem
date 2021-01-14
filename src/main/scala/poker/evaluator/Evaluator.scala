
package poker.evaluator

import java.nio.{ByteBuffer, ByteOrder}
import org.apache.commons.io.IOUtils

import poker.model.Card
import scala.collection.immutable.HashMap
import poker.{bb, cardValues}

object Evaluator {
  val inputStream = getClass.getResourceAsStream("/HandRanks.dat")
  val handRanks  = IOUtils.toByteArray(inputStream)
  inputStream.close()
  val cardIntMap = HashMap(
    "2c" -> 1,
    "2d" -> 2,
    "2h" -> 3,
    "2s" -> 4,
    "3c" -> 5,
    "3d" -> 6,
    "3h" -> 7,
    "3s" -> 8,
    "4c" -> 9,
    "4d" -> 10,
    "4h" -> 11,
    "4s" -> 12,
    "5c" -> 13,
    "5d" -> 14,
    "5h" -> 15,
    "5s" -> 16,
    "6c" -> 17,
    "6d" -> 18,
    "6h" -> 19,
    "6s" -> 20,
    "7c" -> 21,
    "7d" -> 22,
    "7h" -> 23,
    "7s" -> 24,
    "8c" -> 25,
    "8d" -> 26,
    "8h" -> 27,
    "8s" -> 28,
    "9c" -> 29,
    "9d" -> 30,
    "9h" -> 31,
    "9s" -> 32,
    "tc" -> 33,
    "td" -> 34,
    "th" -> 35,
    "ts" -> 36,
    "jc" -> 37,
    "jd" -> 38,
    "jh" -> 39,
    "js" -> 40,
    "qc" -> 41,
    "qd" -> 42,
    "qh" -> 43,
    "qs" -> 44,
    "kc" -> 45,
    "kd" -> 46,
    "kh" -> 47,
    "ks" -> 48,
    "ac" -> 49,
    "ad" -> 50,
    "ah" -> 51,
    "as" -> 52
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
  
  def evalCard(value: Int) = {
    val offset = value * 4
    ByteBuffer.wrap(handRanks, offset, handRanks.length - offset).order(ByteOrder.LITTLE_ENDIAN).getInt
  }

  def eval(cards: List[Card]): Evaluation = {
    val intCards = cards.map(card => cardIntMap(card.value.toLower.toString + symbolsMap(card.symbol)))
    val first = intCards(0)
    val second = intCards(1)
    val third = intCards(2)
    val fourth = intCards(3)
    val fifth = intCards(4)
    val sixth = if intCards.length >=6 then intCards(5) else 0
    val seventh = if intCards.length >=7 then intCards(6) else 0

    val p = 53

    val firstEval = evalCard(p + first)
    val secondEval = evalCard(firstEval + second)
    val thirdEval = evalCard(secondEval + third)
    val fourthEval = evalCard(thirdEval + fourth)
    val fifthEval = evalCard(fourthEval + fifth)
    val sixthEval = if sixth != 0 then evalCard(fifthEval + sixth) else 0
    val seventhEval = if seventh != 0 then evalCard(sixthEval + seventh) else 0

    val intermediateEval = if seventhEval != 0 then seventhEval else if sixthEval != 0 then sixthEval else fifthEval
    val finalEval = if (sixthEval == 0 || seventhEval == 0) then evalCard(intermediateEval) else intermediateEval

    val handAddress = finalEval >> 12;
    Evaluation(handAddress, finalEval & 0x00000fff, finalEval, handTypes(handAddress))
  }

  def evalHoleCards(holeCards: Option[(Card, Card)]): Int = 
    if holeCards.isDefined then
      val card1 = holeCards.get._1
      val card2 = holeCards.get._2
      val sum = cardValues(card1.value).max + cardValues(card2.value).max
      val suitedValue = if (card1.symbol == card2.symbol) 6 else 0
      val delta = (cardValues(card1.value).max - cardValues(card2.value).max).abs
      val connectorValue = Set(8 - delta * 2, 0).max
      sum + suitedValue + connectorValue
    else 0
}