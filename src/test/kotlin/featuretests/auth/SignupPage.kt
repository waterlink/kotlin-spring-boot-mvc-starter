package featuretests.auth

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

@PageUrl("/signup")
class SignupPage : FluentPage() {

    @FindBy(css = """[data-qa="login"]""")
    private lateinit var login: FluentWebElement

    @FindBy(css = """[data-qa="user-input"]""")
    private lateinit var userInput: FluentWebElement

    @FindBy(css = """[data-qa="user-validation"]""")
    private lateinit var userValidation: FluentWebElement

    @FindBy(css = """[data-qa="name-input"]""")
    private lateinit var nameInput: FluentWebElement

    @FindBy(css = """[data-qa="name-validation"]""")
    private lateinit var nameValidation: FluentWebElement

    @FindBy(css = """[data-qa="pass-input"]""")
    private lateinit var passInput: FluentWebElement

    @FindBy(css = """[data-qa="pass-validation"]""")
    private lateinit var passValidation: FluentWebElement

    @FindBy(css = """[data-qa="confirm-input"]""")
    private lateinit var confirmInput: FluentWebElement

    @FindBy(css = """[data-qa="confirm-validation"]""")
    private lateinit var confirmValidation: FluentWebElement

    @FindBy(css = """[data-qa="submit-button"]""")
    private lateinit var submitButton: FluentWebElement

    @FindBy(css = """[data-qa="error"]""")
    private lateinit var error: FluentWebElement

    fun clickOnLogin() {
        login.click()
    }

    fun signup(user: String, name: String, pass: String, confirm: String) {
        userInput.fill().with(user)
        nameInput.fill().with(name)
        passInput.fill().with(pass)
        confirmInput.fill().with(confirm)
        submitButton.click()
    }

    fun errorText() = error.text()!!
    fun userValue() = userInput.value()!!
    fun nameValue() = nameInput.value()!!
    fun passValue() = passInput.value()!!
    fun confirmValue() = confirmInput.value()!!

    fun userValidationText() = userValidation.text()!!
    fun nameValidationText() = nameValidation.text()!!
    fun passValidationText() = passValidation.text()!!
    fun confirmValidationText() = confirmValidation.text()!!

}