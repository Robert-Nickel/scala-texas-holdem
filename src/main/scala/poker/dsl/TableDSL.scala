package poker.dsl

import poker.model.{Player, Table}

object TableDSL {

  /**
   * This exists to simplify (read) access to the table
   */
  implicit class TableDSL(table: Table) {

    def getCurrentPlayer: Player = {
      table.players(table.currentPlayer)
    }

    def getHighestOverallBet: Int = {
      table.players.map(player => player.currentBet).max
    }

    def isOnlyOnePlayerInRound: Boolean = {
      table.players.count(p => p.isInRound) == 1
    }

    def isSB(player: Player): Boolean = table.players(1) == player

    def isBB(player: Player): Boolean = table.players(2) == player

    def isPreFlop: Boolean = table.currentBettingRound == 0

    def shouldPlayNextRound: Boolean = {
      table.players.count(p => p.isInGame()) > 1
    }

    def shouldPlayNextBettingRound: Boolean = {
      table.currentBettingRound < 4 && !table.isOnlyOnePlayerInRound
    }

    def shouldPlayNextMove: Boolean = {
      val maxCurrentBet = table.players.map(p => p.currentBet).max
      table.players.exists(player => player.currentBet != maxCurrentBet && player.isInRound)
    }

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
        s"${" " * 16 * table.currentPlayer}" + "_" * 8
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
