package app.auth.login

import helpers.MockMvcTest
import org.junit.Test
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.web.WebAttributes
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class LoginControllerTest : MockMvcTest {

    override fun controller() = LoginController()

    @Test
    fun `loginFailure - renders login page with failure when bad credentials`() {
        // ACT
        val result = mockMvc().perform(post("/login")
                .param("error", "")
                .requestAttr(WebAttributes.AUTHENTICATION_EXCEPTION, BadCredentialsException("")))

        // ASSERT
        result.andExpect(view().name("auth/login.html"))
                .andExpect(model().attribute("error", "login.bad_credentials"))
                .andExpect(model().attribute("nonConfirmedUser", false))
    }

    @Test
    fun `loginFailure - renders login page with failure when non-confirmed user logs in`() {
        // ACT
        val result = mockMvc().perform(post("/login")
                .param("error", "")
                .requestAttr(WebAttributes.AUTHENTICATION_EXCEPTION, DisabledException("")))

        // ASSERT
        result.andExpect(view().name("auth/login.html"))
                .andExpect(model().attribute("error", "login.non_confirmed_user"))
                .andExpect(model().attribute("nonConfirmedUser", true))
    }

    @Test
    fun `loginFailure - renders login page with failure when confirmation link was already used`() {
        // ACT
        val result = mockMvc().perform(get("/login")
                .param("error", "confirmation_link_was_already_used"))

        // ASSERT
        result.andExpect(view().name("auth/login.html"))
                .andExpect(model().attribute("error", "login.confirmation_link_was_already_used"))
                .andExpect(model().attribute("nonConfirmedUser", false))
    }

    @Test
    fun `signOutSuccess - renders login page with a message`() {
        // ACT
        val result = mockMvc().perform(get("/login")
                .param("logout", ""))

        // ASSERT
        result.andExpect(view().name("auth/login.html"))
                .andExpect(model().attribute("signOutInfo", "login.successful_sign_out"))
                .andExpect(model().attribute("nonConfirmedUser", false))
    }
}