package app.auth.dashboard

import app.auth.AuthService
import app.auth.CurrentUser
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import helpers.MockMvcTest
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import java.security.Principal

class DashboardControllerTest : MockMvcTest {

    private val principal = mock<Principal>()
    private val authService = mock<AuthService>()

    override fun controller() = DashboardController(authService)

    @Test
    fun `dashboard - renders current user name`() {
        // ARRANGE
        given(principal.name).willReturn("kate@example.org")
        given(authService.getCurrentUser(email = "kate@example.org"))
                .willReturn(CurrentUser(id = 42, name = "Kate"))

        // ACT
        val result = mockMvc().perform(get("/dashboard").principal(principal))

        // ASSERT
        result.andExpect(model().attribute("currentUser",
                hasProperty<String>("name", equalTo("Kate"))))
    }

    @Test
    fun `dashboard - renders current user name for different user`() {
        // ARRANGE
        given(principal.name).willReturn("john@example.org")
        given(authService.getCurrentUser(email = "john@example.org"))
                .willReturn(CurrentUser(id = 54, name = "John"))

        // ACT
        val result = mockMvc().perform(get("/dashboard").principal(principal))

        // ASSERT
        result.andExpect(model().attribute("currentUser",
                hasProperty<String>("name", equalTo("John"))))
    }
}

