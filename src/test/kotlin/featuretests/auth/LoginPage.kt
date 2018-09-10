package featuretests.auth

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

@PageUrl("/login")
class LoginPage : FluentPage() {

    @FindBy(css = """[data-qa="user-input"]""")
    private lateinit var userInput: FluentWebElement

    @FindBy(css = """[data-qa="pass-input"]""")
    private lateinit var passwordInput: FluentWebElement

    @FindBy(css = """[data-qa="submit-button"]""")
    private lateinit var submitButton: FluentWebElement

    @FindBy(css = """[data-qa="error"]""")
    private lateinit var error: FluentWebElement

    @FindBy(css = """[data-qa="sign-out-info"]""")
    private lateinit var signOutInfo: FluentWebElement

    @FindBy(css = """[data-qa="create-account"]""")
    private lateinit var createAccount: FluentWebElement

    @FindBy(css = """[data-qa="resend-confirmation"]""")
    private lateinit var resendConfirmation: FluentWebElement

    fun login(user: String, pass: String) {
        userInput.fill().with(user)
        passwordInput.fill().with(pass)
        submitButton.click()
    }

    fun errorText() = error.text()!!
    fun signOutText() = signOutInfo.text()!!

    fun userInputValue(): String = userInput.value()
    fun passwordInputValue(): String = passwordInput.value()

    fun clickOnCreateAccount() = createAccount.click()!!
    fun clickOnResendConfirmationLink() = resendConfirmation.click()!!
}