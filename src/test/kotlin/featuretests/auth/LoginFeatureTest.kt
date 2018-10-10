package featuretests.auth

import app.auth.AuthService
import app.auth.user.User
import helpers.FeatureTest
import helpers.today
import org.fluentlenium.assertj.FluentLeniumAssertions.assertThat
import org.fluentlenium.core.FluentControl
import org.junit.Before
import org.junit.Test
import org.springframework.web.util.UriComponentsBuilder

class LoginFeatureTest : FeatureTest(), LoginFeatureTestHelper {

    @Before
    fun `before each`() {
        Given(`there are following signed-up users`(
                UserEntry(user = "kate@example.org", pass = "welcomekate", name = "Kate"),
                UserEntry(user = "john@example.org", pass = "johnwelcome", name = "John")
        ))
    }

    @Test
    fun `login is required`() {
        When(`I go to dashboard page`())
        Then(`I see the login page`())
    }

    @Test
    fun `correct login`() {
        Given(`I am on the login page`())
        When(`I log in with`(user = "kate@example.org", pass = "welcomekate"))
        Then(`I see the dashboard`(with = "Welcome, Kate"))
    }

    @Test
    fun `correct login for other user`() {
        Given(`I am on the login page`())
        When(`I log in with`(user = "john@example.org", pass = "johnwelcome"))
        Then(`I see the dashboard`(with = "Welcome, John"))
    }

    @Test
    fun `non-existing user`() {
        Given(`I am on the login page`())
        When(`I log in with`(user = "sali@example.org"))
        Then(`I see that my credentials are invalid`())
        And(`the user field filled`(with = "sali@example.org"))
        And(`the password field is empty`())
        And(`I can try to login again`())
    }

    @Test
    fun `existing user with invalid password`() {
        Given(`I am on the login page`())
        When(`I log in with`(user = "kate@example.org"))
        Then(`I see that my credentials are invalid`())
        And(`the user field filled`(with = "kate@example.org"))
        And(`the password field is empty`())
        And(`I can try to login again`())
    }

    private fun `I can try to login again`() = `correct login`()

}

interface LoginFeatureTestHelper : FluentControl {
    val authService: AuthService
    val dashboardPage: DashboardPage
    val loginPage: LoginPage

    fun `there are following signed-up users`(vararg entries: UserEntry) {
        entries.forEach {
            authService.signupUser(User(
                    email = it.user,
                    password = it.pass,
                    name = it.name,
                    confirmed = true,
                    createdAt = today,
                    updatedAt = today
            ), baseUrl)
        }
    }

    fun `I am logged in`(with: String) {
        val uri = UriComponentsBuilder
                .fromPath("/login/force")
                .queryParam("username", with)
                .toUriString()

        goTo(uri)

        `I see the dashboard`(with = "Welcome")
    }

    fun `I go to dashboard page`() {
        goTo(dashboardPage)
    }

    fun `I am on the dashboard page`() = `I go to dashboard page`()

    fun `I see the login page`(with: String? = null) {
        assertThat(loginPage).isAt

        if (with != null) {
            assertThat(loginPage.signOutText()).contains(with)
        }
    }

    fun `I log in with`(user: String, pass: String = "irrelevant") {
        loginPage.login(user, pass)
    }

    fun `I am on the login page`() {
        goTo(loginPage)
    }

    fun `I see the dashboard`(with: String? = null) {
        assertThat(dashboardPage).isAt

        if (with != null) {
            assertThat(dashboardPage.welcomeText()).contains(with)
        }
    }

    fun `I see that my credentials are invalid`() {
        assertThat(loginPage.errorText()).contains("Bad credentials")
    }

    fun `the user field filled`(with: String) {
        assertThat(loginPage.userInputValue()).isEqualTo(with)
    }

    fun `the password field is empty`() {
        assertThat(loginPage.passwordInputValue()).isEqualTo("")
    }
}

data class UserEntry(val user: String = "irrelevant@example.org",
                     val pass: String = "irrelevant",
                     val name: String = "irrelevant")
