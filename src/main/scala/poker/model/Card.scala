package poker.model

case class Card(value: Char, symbol: Char) {
  override def toString: String = s"[$value$symbol]"

  def toLetterNotation: String = {
    val letter = symbol match {
      case '♥' => 'h'
      case '♦' => 'd'
      case '♣' => 'c'
      case '♠' => 's'
    }
    s"$value$letter"
  }
}
