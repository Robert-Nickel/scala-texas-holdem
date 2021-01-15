package poker.model

case class Card(value: Char, symbol: Char):
  override def toString: String = s"[$value$symbol]"