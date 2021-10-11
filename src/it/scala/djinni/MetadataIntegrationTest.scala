package djinni

import org.scalatest._
import matchers._
import should.Matchers._

class MetadataIntegrationTest extends IntegrationTest with GivenWhenThen {
  it("should output version information") {
    When("calling `djinni --version`")
    val versionDescription = djinni("--version")
    Then(s"the version description should be returned")
    versionDescription should startWith("djinni generator version")
  }
  it("should output usage help") {
    When("calling `djinni --help`")
    val helpText = djinni("--help")
    Then("the version information followed by the help text should be returned")
    helpText should (startWith("djinni generator version") and include(
      "Usage: djinni [options]"
    ))
  }
}
