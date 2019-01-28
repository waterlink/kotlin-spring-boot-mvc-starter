package featuretests.auth

import app.email.EmailMessage
import app.email.TestEmailService
import app.auth.user.User
import helpers.FeatureTest
import helpers.today
import org.assertj.core.api.Assertions.fail
import org.fluentlenium.assertj.FluentLeniumAssertions.assertThat
import org.junit.Test

class SignupFeatureTest : FeatureTest(), SignupFeatureTestHelper {

    override lateinit var rememberedConfirmationLink: String

    // Links
    @Test
    fun `login page has a link to the signup page`() {
        Given(`I am on the login page`())
        When(`I click on the create account link`())
        Then(`I see the signup page`())
    }

    @Test
    fun `signup page has a link to the login page`() {
        Given(`I am on the signup page`())
        When(`I click on the login link`())
        Then(`I see the login page`())
    }

    // Happy path :)
    @Test
    fun `creating new account`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(
                user = "sali@example.org", name = "Sali",
                pass = "saliwelcome", confirm = "saliwelcome"))
        Then(`I see the thank you page`())
        And(`I receive the confirmation email`(on = "sali@example.org", withName = "Sali"))
    }

    @Test
    fun `can't login before confirmation`() {
        Given(`I have created an account with`(user = "sali@example.org", pass = "saliwelcome"))
        And(`I am on the login page`())
        When(`I log in with`(user = "sali@example.org", pass = "saliwelcome"))
        Then(`I see the login page`())
        And(`I see that my account is not confirmed yet`())
    }

    @Test
    fun `confirming the account`() {
        Given(`I have created an account with`(user = "sali@example.org", name = "Sali"))
        When(`I click on the link in the confirmation email`())
        Then(`I see the dashboard`(with = "Welcome, Sali"))
    }

    @Test
    fun `can login after confirmation`() {
        Given(`I have created an account with`(user = "sali@example.org", name = "Sali", pass = "welcomesali"))
        And(`I have confirmed the account`())
        And(`I am on the login page`())
        When(`I log in with`(user = "sali@example.org", pass = "welcomesali"))
        Then(`I see the dashboard`(with = "Welcome, Sali"))
    }

    // Meh paths :S
    @Test
    fun `resending the confirmation link`() {
        Given(`I have created an account with`(user = "sali@example.org", name = "Sali", pass = "saliwelcome"))
        And(`I remember the current confirmation link`())
        And(`I am on the login page`())

        When(`I log in with`(user = "sali@example.org", pass = "saliwelcome"))
        And(`I click on the resend confirmation link`())

        Then(`I see the thank you page`())
        And(`I receive the confirmation email`(on = "sali@example.org", withName = "Sali"))
        And(`The confirmation link is new`())
    }

    @Test
    fun `unable to confirm using old link`() {
        Given(`I have created an account with`(user = "sali@example.org", pass = "saliwelcome"))
        And(`I remember the current confirmation link`())
        And(`I have requested new confirmation email`(user = "sali@example.org"))
        When(`I click on the old confirmation link`())
        Then(`I see that the url matches the old confirmation link`())
        And(`I see that the link is invalid`())
    }

    @Test
    fun `confirming the account with new link`() {
        Given(`I have created an account with`(user = "sali@example.org", name = "Sali"))
        And(`I have requested new confirmation email`(user = "sali@example.org"))
        When(`I click on the link in the confirmation email`())
        Then(`I see the dashboard`(with = "Welcome, Sali"))
    }

    @Test
    fun `can log in after confirming with new link`() {
        Given(`I have created an account with`(user = "sali@example.org", name = "Sali", pass = "welcomesali"))
        And(`I have requested new confirmation email`(user = "sali@example.org"))
        And(`I have confirmed the account`())
        And(`I am on the login page`())
        When(`I log in with`(user = "sali@example.org", pass = "welcomesali"))
        Then(`I see the dashboard`(with = "Welcome, Sali"))
    }

    // Sad paths :(
    @Test
    fun `trying to create an account for existing email`() {
        Given(`there are following signed-up users`(
                UserEntry(user = "kate@example.org")))
        And(`I am on the signup page`())
        When(`I sign up with`(user = "kate@example.org", name = "Kate"))
        Then(`I see the signup page`())
        And(`I see that the username is already taken`())
        And(`I see that username and name are still filled in`(user = "kate@example.org", name = "Kate"))
        And(`I see that password and confirm are cleared`())
    }

    @Test
    fun `trying to use confirmation link again to log in`() {
        Given(`I have created an account with`(user = "sali@example.org"))
        And(`I have confirmed the account`())
        When(`I click on the link in the confirmation email`())
        Then(`I see the login page`())
        And(`I see that the link was already used`())
    }

    @Test
    fun `trying to use invalid confirmation link`() {
        When(`I click on the invalid confirmation link`())
        Then(`I see that the link is invalid`())
    }

    @Test
    fun `entering invalid email`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(user = "kate"))
        Then(`I see the signup page`())
        And(`I see that the username is invalid`())
    }

    @Test
    fun `entering empty email`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(user = ""))
        Then(`I see the signup page`())
        And(`I see that the username is empty`())
    }

    @Test
    fun `entering empty name`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(name = ""))
        Then(`I see the signup page`())
        And(`I see that the name is empty`())
    }

    @Test
    fun `password is too short`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(pass = "tooshort", confirm = "tooshort"))
        Then(`I see the signup page`())
        And(`I see that the password is too short`())
    }

    @Test
    fun `entering empty password`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(pass = "", confirm = ""))
        Then(`I see the signup page`())
        And(`I see that the password is too short`())
    }

    @Test
    fun `passwords do not match`() {
        Given(`I am on the signup page`())
        When(`I sign up with`(pass = "heywelcome", confirm = "somethingelse"))
        Then(`I see the signup page`())
        And(`I see that the passwords do not match`())
    }
}

interface SignupFeatureTestHelper : LoginFeatureTestHelper {

    val signupPage: SignupPage
    val thankYouPage: ThankYouPage
    val confirmationPage: ConfirmationPage
    val emailService: TestEmailService

    var rememberedConfirmationLink: String

    fun `I am on the signup page`() {
        goTo(signupPage)
    }

    fun `I have created an account with`(user: String,
                                         pass: String = "irrelevant",
                                         name: String = "irrelevant") {
        authService.signupUser(User(
                email = user,
                password = pass,
                name = name,
                createdAt = today,
                updatedAt = today
        ), baseUrl)
    }

    fun `I have confirmed the account`() {
        val email = parseConfirmationEmail()!!

        authService.confirmAndGetUser(email.code)
    }

    fun `I remember the current confirmation link`() {
        val email = parseConfirmationEmail()!!

        rememberedConfirmationLink = email.link
    }

    fun `I have requested new confirmation email`(user: String) {
        authService.resendConfirmation(user, baseUrl)
    }

    fun `I click on the create account link`() {
        loginPage.clickOnCreateAccount()
    }

    fun `I click on the login link`() {
        signupPage.clickOnLogin()
    }

    fun `I sign up with`(user: String = "irrelevant@example.org",
                         name: String = "irrelevant",
                         pass: String = "irrelevant",
                         confirm: String = "irrelevant") {
        signupPage.signup(
                user = user,
                name = name,
                pass = pass,
                confirm = confirm
        )
    }

    fun `I click on the link in the confirmation email`() {
        val email = parseConfirmationEmail()!!
        goTo(email.link)
    }

    fun `I click on the resend confirmation link`() {
        loginPage.clickOnResendConfirmationLink()
    }

    fun `I click on the old confirmation link`() {
        goTo(rememberedConfirmationLink)
    }

    fun `I click on the invalid confirmation link`() {
        goTo("/confirm/invalid-confirmation-code")
    }

    fun `I see the signup page`() {
        assertThat(signupPage).isAt
    }

    fun `I see the thank you page`() {
        assertThat(thankYouPage).isAt
        assertThat(thankYouPage.headerText()).isEqualTo("Thank you for signing up")
    }

    fun `I receive the confirmation email`(on: String, withName: String) {
        val (email) = parseConfirmationEmail()!!

        assertThat(email.from).isEqualToIgnoringCase("Quizzy Support <noreply@example.org>")
        assertThat(email.to).isEqualToIgnoringCase("$withName <$on>")
        assertThat(email.subject).isEqualToIgnoringCase("Please confirm your account")
    }

    fun `I see that my account is not confirmed yet`() {
        assertThat(loginPage.errorText())
                .contains("Your account is not confirmed yet. " +
                        "Please click on the link in the confirmation email.")
    }

    fun `The confirmation link is new`() {
        val email = parseConfirmationEmail()!!

        assertThat(email.link).isNotEqualToIgnoringCase(rememberedConfirmationLink)
    }

    fun `I see that the url matches the old confirmation link`() {
        assertThat("$baseUrl/${url()}").isEqualTo(rememberedConfirmationLink)
    }

    fun `I see that the link is invalid`() {
        assertThat(confirmationPage.errorText()).contains(
                "The confirmation code is invalid"
        )
    }

    fun `I see that the username is already taken`() {
        assertThat(signupPage.errorText()).contains(
                "This username is already taken"
        )
    }

    fun `I see that username and name are still filled in`(user: String, name: String) {
        assertThat(signupPage.userValue()).isEqualTo(user)
        assertThat(signupPage.nameValue()).isEqualTo(name)
    }

    fun `I see that password and confirm are cleared`() {
        assertThat(signupPage.passValue()).isEmpty()
        assertThat(signupPage.confirmValue()).isEmpty()
    }

    fun `I see that the link was already used`() {
        assertThat(loginPage.errorText()).contains(
                "The confirmation link was already used"
        )
    }

    fun `I see that the username is invalid`() {
        assertThat(signupPage.userValidationText())
                .contains("Username must be a valid email address")
    }

    fun `I see that the username is empty`() {
        assertThat(signupPage.userValidationText())
                .contains("Username can't be empty")
    }

    fun `I see that the name is empty`() {
        assertThat(signupPage.nameValidationText())
                .contains("Name can't be empty")
    }

    fun `I see that the password is too short`() {
        assertThat(signupPage.passValidationText())
                .contains("Passwords must have 10 or more characters")
    }

    fun `I see that the passwords do not match`() {
        assertThat(signupPage.passValidationText())
                .contains("Passwords must be the same")
        assertThat(signupPage.confirmValidationText())
                .contains("Passwords must be the same")
    }

    private fun parseConfirmationEmail(): ParsedConfirmationEmail? {
        val email = emailService.lastEmail
                ?: return fail<Nothing>("There was no confirmation email")

        val link = "http[^ \n]+".toRegex().find(email.textBody)?.value
                ?: return fail<Nothing>("There was no link in the confirmation email")

        val (code) = "http[^ ]+/confirm/([^ \n]+)".toRegex().find(email.textBody)
                ?.destructured
                ?: return fail<Nothing>("There was no code in the confirmation email")

        return ParsedConfirmationEmail(email, link, code)
    }

}

data class ParsedConfirmationEmail(val email: EmailMessage,
                                   val link: String,
                                   val code: String)
