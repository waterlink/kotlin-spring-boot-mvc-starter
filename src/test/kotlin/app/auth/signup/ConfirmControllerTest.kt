package app.auth.signup

import app.auth.AuthService
import app.auth.SecurityContextWrapper
import app.auth.user.UserNotFoundException
import app.auth.user.asSpringSecurityToken
import app.auth.user.asSpringSecurityUser
import app.auth.user.standardUsers
import app.auth.user.standardUsers.kate
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import helpers.MockMvcTest
import org.junit.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class ConfirmControllerTest : MockMvcTest {

    private val userService = mock<AuthService>()

    private val userDetailsService = mock<UserDetailsService> {
        standardUsers.all.forEach { user ->
            on { loadUserByUsername(user.email) } doReturn user.asSpringSecurityUser()
        }
    }

    private val securityContextWrapper = mock<SecurityContextWrapper>()
    private val forceLoginService = ForceLoginService(userDetailsService, securityContextWrapper)

    override fun controller() = ConfirmController(userService, forceLoginService)

    @Test
    fun `confirm - confirms the account`() =
            listOf("code-1", "code-2").forEach { code ->
                // ARRANGE
                given(userService.confirmAndGetUser(code)).willReturn(kate)

                // ACT
                mockMvc().perform(get("/confirm/$code"))

                // ASSERT
                verify(userService).confirmAndGetUser(code)
            }

    @Test
    fun `confirm - logs in after confirming`() = standardUsers.all.forEach { user ->
        // ARRANGE
        val code = "valid-code"
        given(userService.confirmAndGetUser(code)).willReturn(user)

        // ACT
        mockMvc().perform(get("/confirm/$code"))

        // ASSERT
        verify(securityContextWrapper).authentication = user.asSpringSecurityToken()
    }

    @Test
    fun `confirm - logs in after confirming with different granted authorities`() {
        // ARRANGE
        val code = "valid-code"
        given(userService.confirmAndGetUser(code)).willReturn(kate)

        val otherAuthorities = listOf(SimpleGrantedAuthority("ROLE_OTHER"))
        given(userDetailsService.loadUserByUsername(kate.email)).willReturn(
                kate.asSpringSecurityUser(authorities = otherAuthorities))

        // ACT
        mockMvc().perform(get("/confirm/$code"))

        // ASSERT
        verify(securityContextWrapper).authentication =
                kate.asSpringSecurityToken(authorities = otherAuthorities)
    }

    @Test
    fun `confirm - redirects to dashboard after login`() {
        // ARRANGE
        val code = "good-code"
        given(userService.confirmAndGetUser(code)).willReturn(kate)

        // ACT
        val result = mockMvc().perform(get("/confirm/$code"))

        // ASSERT
        result.andExpect(status().isFound)
                .andExpect(redirectedUrl("/dashboard"))
    }

    @Test
    fun `confirm - informs user about invalid confirmation link`() {
        // ARRANGE
        val code = "invalid-code"
        given(userService.confirmAndGetUser(code)).willThrow(InvalidCodeException())

        // ACT
        val result = mockMvc().perform(get("/confirm/$code"))

        // ASSERT
        result.andExpect(view().name("auth/invalid_confirmation_link.html"))
    }

    @Test
    fun `confirm - redirects to login when confirmation link was already used`() {
        // ARRANGE
        val code = "already-used-code"
        given(userService.confirmAndGetUser(code)).willThrow(CodeUsedAlreadyException())

        // ACT
        val result = mockMvc().perform(get("/confirm/$code"))

        // ASSERT
        result.andExpect(status().isFound)
                .andExpect(redirectedUrl("/login?error=confirmation_link_was_already_used"))
    }

    @Test
    fun `resendConfirmation - re-sends confirmation and redirects to thank you page`() =
            standardUsers.all.forEach { user ->
                // ACT
                val result = mockMvc().perform(post("/some/context/path/resend-confirmation")
                        .contextPath("/some/context/path")
                        .param("username", user.email))

                // ASSERT
                result.andExpect(status().isFound)
                        .andExpect(redirectedUrl("/some/context/path/thank-you"))
                verify(userService).resendConfirmation(
                        user.email,
                        "http://localhost/some/context/path"
                )
            }

    @Test
    fun `resendConfirmation - is not found when user not found`() {
        // ARRANGE
        val email = "missing@example.org"
        given(userService.resendConfirmation(email, "http://localhost"))
                .willThrow(UserNotFoundException::class.java)

        // ACT
        val result = mockMvc().perform(post("/resend-confirmation")
                .param("username", email))

        // ASSERT
        result.andExpect(status().isNotFound)
    }

}