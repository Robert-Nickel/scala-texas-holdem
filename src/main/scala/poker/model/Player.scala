package main.scala.poker.model

case class Player(name: String, stack: Int, holeCards: (Option[Card], Option[Card])) {
  override def toString: String = s"Player: $name, Stack: $stack, Cards: ${holeCards._1.getOrElse("None")} ${holeCards._2.getOrElse("None")}"

  def getHoleCardsString(): String = {
    if(name.equals("You")) {
      s"[${holeCards._1.getOrElse("None")}][${holeCards._2.getOrElse("None")}]"
    } else {
      "[xx][xx]"
    }
  }
}
