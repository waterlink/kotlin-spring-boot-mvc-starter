package featuretests

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

@PageUrl("/dashboard")
class DashboardPage : FluentPage() {

    @FindBy(css = """[data-qa="welcome"]""")
    private lateinit var welcome: FluentWebElement

    @FindBy(css = """[data-qa="sign-out"]""")
    private lateinit var signOut: FluentWebElement

    fun welcomeText() = welcome.text()!!

    fun clickOnSignOut() {
        signOut.click()
    }
}