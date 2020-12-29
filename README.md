# A CLI version of Texas Holdem using Scala
 
[![Build Status](https://travis-ci.com/Robert-Nickel/scala-texas-holdem.svg?branch=master)](https://travis-ci.com/Robert-Nickel/scala-texas-holdem)
[![Coverage Status](https://coveralls.io/repos/github/Robert-Nickel/scala-texas-holdem/badge.svg?branch=master)](https://coveralls.io/github/Robert-Nickel/scala-texas-holdem?branch=master)
 
A CLI version of Texas Holdem using Scala for the course "Reactive Programming" at HTWG WS20/21.
All typical cash game rules of no limit texas holdem poker should be covered.
One human player can play against 5 bot players by using the following commands:
- [x] fold
- [x] check
- [x] call
- [x] raise 20 (or any other number)
- [x] all-in

Since it is a running project, not all features are implemented yet.
A lot of different aspects of the Scala Programming Language are covered, therefore some focus is put onto the technology rather than the game itself.
To evaluate hands, we borrowed the solution from the TwoPlusTwo hand ranks evaluator as described [here](https://web.archive.org/web/20111103160502/http://www.codingthewheel.com/archives/poker-hand-evaluator-roundup#2p2).
To get the implementation of the evaluation done, we looked [here](https://github.com/chenosaurus/poker-evaluator), [here](https://github.com/LativDeveloper/PokerGym) and [here](https://github.com/tommy-a/zetebot/blob/master/src/tools/TwoPlusTwo.java).

Features to cover in the near future:
- [ ] if all-in and highestOverallbet > investment only return partial amount
- [ ] if the next player has folded or is all-in, skip him sooner
- [ ] if someone has called all-in, but still has chips behind, the betting round continues forever, because he is not considered "all-in", but everyone else has either folded or is all-in.
- [x] Players don't get cards, if they don't have chips
- [x] Players don't act, if they are all-in
- [x] Bots using actors to make smarter decisions
- [x] Adjust game speed so that it is viable for human players to follow up with what happens
