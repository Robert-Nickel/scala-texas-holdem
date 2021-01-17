package poker.model

import poker.evaluator.Evaluator.evalHoleCards
import poker.evaluator.FastPostFlopEvaluator.getPostFlopEvaluation
import poker.bb


object Bot :
    def act(player: Player, highestOverallBet: Int, board: List[Card] = List()): Player = 
        if board.isEmpty then
            val handValue = evalHoleCards(player.holeCards)
            actPreflop(player, handValue, highestOverallBet)
        else
            val flopValue = getPostFlopEvaluation(board :+ player.holeCards.get._1 :+ player.holeCards.get._2)
            actPostFlop(player, flopValue, highestOverallBet)

    def actPostFlop(player: Player, handValue: Int, highestOverallBet: Int): Player = 
        handValue match {
        case handValue if handValue > 24_000 => {
            player.allIn(highestOverallBet)
        }
        case handValue if handValue > 12_000 => {
            val tryRaise = player.raise(highestOverallBet * 3, highestOverallBet)
            if (tryRaise.isFailure) {
            player.fold()
            } else {
            tryRaise.get
            }
        }

        case handValue if handValue > 8_000 =>
            val tryCall = player.call(highestOverallBet)
            if (tryCall.isSuccess) tryCall.get else player.fold()
        case _ => if(highestOverallBet == 0)
            then
                val tryCheck = player.check(highestOverallBet)
                if(tryCheck.isFailure)
                    then player.fold()
                    else tryCheck.get
            else player.fold()
        }

    def actPreflop(player: Player, handValue: Int, highestOverallBet: Int): Player = 
        handValue match {
        case handValue if handValue > 30 =>
            val tryRaise = player.raise(5 * bb, highestOverallBet)
            if tryRaise.isFailure then
                val tryCall = player.call(highestOverallBet)
                if tryCall.isSuccess then tryCall.get else player.fold()
            else
            tryRaise.get
        case x if x > 20 =>
            val tryRaise = player.raise(3 * bb, highestOverallBet)
            if tryRaise.isFailure then
                val tryCall = player.call(highestOverallBet)
                if tryCall.isSuccess then tryCall.get else player.fold()
            else tryRaise.get
        case _ if player.stack < 10 * bb =>
            player.raise(player.stack, highestOverallBet).get
        // TODO: What if no prior bet has been made? Technically its a check then.
        case _ if highestOverallBet <= 3 * bb => {
            if player.call(highestOverallBet).isFailure then player.fold()
            else player.call(highestOverallBet).get
        }
        case _ => player.fold()
        }