package featuretests.auth

import helpers.FeatureTest
import org.junit.Test

class LogoutFeatureTest : FeatureTest(), LogoutFeatureTestHelper {
    @Test
    fun `signing out`() {
        Given(`there are following signed-up users`(
                UserEntry(user = "kate@example.org", name = "Kate")
        ))
        And(`I am on the login page`())

        When(`I log in with`(user = "kate@example.org"))
        And(`I click on the sign out button`())
        Then(`I see the login page`(with = "You have been signed out successfully"))

        When(`I go to dashboard page`())
        Then(`I see the login page`())
    }
}

interface LogoutFeatureTestHelper : LoginFeatureTestHelper {
    fun `I click on the sign out button`() {
        dashboardPage.clickOnSignOut()
    }
}
