package poker.dsl

import poker.model.Table

object TablePrintingDSL {
  implicit class TablePrintingDSL(table: Table) {
    def getPrintableTable: String = {
      def getCurrentBets = {
        table.players.flatMap(player => {
          val spacesAfterCurrentBet = 16 - player.currentBet.toString.length
          s"${player.currentBet}" + " " * spacesAfterCurrentBet
        }).mkString
      }

      def getHoleCards = {
        table.players.map(player => {
          s"${player.getHoleCardsString()}" + " " * 8
        }).mkString
      }

      def getNames = {
        table.players.head.name + " (D) " + " " * 8 +
          table.players.tail.map(player => {
            s"${player.name} " + " " * 12
          }).mkString
      }

      def getStacks = {
        table.players.map(player => {
          val spacesAfterStack = 16 - player.stack.toString.length
          s"${player.stack}" + " " * spacesAfterStack
        }).mkString
      }

      def getCurrentPlayerUnderline = {
        s"${" " * 16 * table.currentPlayer}"+"_"*8
      }

      def getPot = {
        " " * (39 - (table.pot.toString.length / 2)) + s"Pot: ${table.pot}"
      }

      def getBettingLine = {
        "_" * 88
      }

      "\n" +
        getPot + "\n\n" +
        getCurrentBets + "\n" +
        getBettingLine + "\n" +
        getHoleCards + "\n" +
        getNames + "\n" +
        getStacks + "\n" +
        getCurrentPlayerUnderline + "\n"
    }
  }
}
