# A CLI version of Texas Holdem using Scala
 
[![Build Status](https://travis-ci.com/Robert-Nickel/scala-texas-holdem.svg?branch=master)](https://travis-ci.com/Robert-Nickel/scala-texas-holdem)
[![Coverage Status](https://coveralls.io/repos/github/Robert-Nickel/scala-texas-holdem/badge.svg?branch=master)](https://coveralls.io/github/Robert-Nickel/scala-texas-holdem?branch=master)
 
*The coverage relates to the [Scala 2 version](https://github.com/Robert-Nickel/scala-texas-holdem/tree/scala2.13) of the project*

## Technological
A CLI version of Texas Holdem using **Scala 3** for the course "Reactive Programming" at HTWG WS20/21. Many different aspects of the Scala Programming Language and surrounding libraries are covered, therefore some effort is put onto the technology rather than the game itself.

[Basics](https://github.com/Robert-Nickel/scala-texas-holdem/tree/master)
- Introduction to Scala
- More Scala
- Tests
- Functional Style and Monads

[Internal DSLs](https://github.com/Robert-Nickel/scala-texas-holdem/blob/master/src/main/scala/poker/Constants.scala)

[External DSLs](https://github.com/Robert-Nickel/scala-texas-holdem/blob/scala2.13/src/main/scala/poker/dsl/HandHistoryParser.scala)

[Actors](https://github.com/Robert-Nickel/scala-texas-holdem/tree/kafka/src/main/scala/poker/actor)

[Reactive Streams](https://github.com/Robert-Nickel/scala-texas-holdem/blob/kafka/src/main/scala/poker/stream/EquityCalculator.scala)

[Kafka](https://github.com/Robert-Nickel/scala-texas-holdem/tree/kafka)

[Spark](https://github.com/Robert-Nickel/scala-texas-holdem/tree/spark)

[Scala 3](https://github.com/Robert-Nickel/scala-texas-holdem/tree/master)

To evaluate hands, we borrowed the solution from the TwoPlusTwo hand ranks evaluator as described [here](https://web.archive.org/web/20111103160502/http://www.codingthewheel.com/archives/poker-hand-evaluator-roundup#2p2).
To get the implementation of the evaluation done, we looked [here](https://github.com/chenosaurus/poker-evaluator), [here](https://github.com/LativDeveloper/PokerGym) and [here](https://github.com/tommy-a/zetebot/blob/master/src/tools/TwoPlusTwo.java).

## Functional
All typical [cash game rules](https://www.pokerlistings.com/poker-rules-texas-holdem) are covered.  

### Manual
You can use these commands:
```
fold
check
call
raise 42
all-in
```

### Yet missing Features
- [ ] if all-in and highestOverallbet > investment only return partial amount
- [ ] if the next player has folded or is all-in, skip him faster

### Known Issues
- The unicode characters ♥, ♠, ♦, ♣ are not supported well on the native Windows terminals. The IntelliJ terminal renders them well.
- If someone has called all-in, but still has chips behind, the betting round continues forever, because he is not considered "all-in", but everyone else has either folded or is all-in.