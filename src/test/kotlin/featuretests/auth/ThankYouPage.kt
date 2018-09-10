package featuretests.auth

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

@PageUrl("/thank-you")
class ThankYouPage : FluentPage() {

    @FindBy(css = """[data-qa="header"]""")
    private lateinit var header: FluentWebElement

    fun headerText() = header.text()!!

}
