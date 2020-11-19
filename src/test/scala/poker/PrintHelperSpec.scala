package poker

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PrintHelperSpec extends AnyWordSpec with Matchers {

  "PrintHelper" when {
    "given a integer" should {
      val currentPlayerUnderscore = PrintHelper.getCurrentPlayerUnderscore(3)
      "return string with the right indentation to underline the current player" in {
        currentPlayerUnderscore should be(" " * 3 * 16 + "_" * 8)
      }
    }
  }
}
