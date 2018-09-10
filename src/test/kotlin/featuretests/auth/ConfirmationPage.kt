package featuretests.auth

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

class ConfirmationPage : FluentPage() {

    @FindBy(css = """[data-qa="error"]""")
    private lateinit var error: FluentWebElement

    fun errorText() = error.text()!!

}
