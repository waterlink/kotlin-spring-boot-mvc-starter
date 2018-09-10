package app.auth.signup

import app.auth.AuthService
import app.auth.user.User
import app.auth.user.UserExistsAlreadyException
import app.auth.user.standardUsers
import app.auth.user.standardUsers.kate
import app.util.TimeProvider
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import helpers.MockMvcTest
import helpers.today
import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.util.LinkedMultiValueMap

class SignupControllerTest : MockMvcTest {

    private val userService = mock<AuthService>()

    private val timeProvider = mock<TimeProvider> {
        on { now() } doReturn today
    }

    override fun controller() = SignupController(userService, timeProvider)

    @Test
    fun `signup - redirects to thank you page`() {
        // ACT
        val result = mockMvc().perform(post("/signup")
                .params(signupRequestFor(kate)))

        // ASSERT
        result.andExpect(status().isFound)
                .andExpect(redirectedUrl("/thank-you"))
    }

    @Test
    fun `signup - creates an account`() {
        standardUsers.all.forEach { user ->
            // ACT
            mockMvc().perform(post("/signup")
                    .params(signupRequestFor(user)))

            // ASSERT
            val expectedUser = expectedCreatedUserFor(user)
            verify(userService).signupUser(expectedUser, "http://localhost")
        }
    }

    @Test
    fun `signup - is aware of context path when getting the base url`() {
        // ACT
        mockMvc().perform(post("/some/context/path/signup")
                .contextPath("/some/context/path")
                .params(signupRequestFor(kate)))

        // ASSERT
        val expectedUser = expectedCreatedUserFor(kate)
        verify(userService).signupUser(expectedUser, "http://localhost/some/context/path")
    }

    @Test
    fun `signup - fails when user already exists`() {
        // ARRANGE
        val baseUrl = "http://localhost"
        val expectedUser = expectedCreatedUserFor(kate)
        given(userService.signupUser(expectedUser, baseUrl))
                .willThrow(UserExistsAlreadyException())

        // ACT
        val result = mockMvc().perform(post("/signup")
                .params(signupRequestFor(kate)))

        // ASSERT
        result.andExpect(view().name("auth/signup.html"))
                .andExpect(model().attributeHasNoErrors("form"))
                .andExpect(model().attribute("error", "signup.user_already_taken"))
    }

    @Test
    fun `signup - renders validation failure when email is invalid`() {
        // ACT
        val result = mockMvc().perform(post("/signup")
                .params(signupRequestFor(User(
                        email = "invalid",
                        name = "irrelevant",
                        password = "irrelevant",
                        createdAt = today,
                        updatedAt = today
                ))))

        // ASSERT
        result.andExpect(view().name("auth/signup.html"))
                .andExpect(model().attributeHasFieldErrors("form", "username"))
    }

    @Test
    fun `signup - renders validation failure when email is empty`() {
        // ACT
        val result = mockMvc().perform(post("/signup")
                .params(signupRequestFor(User(
                        email = "",
                        name = "irrelevant",
                        password = "irrelevant",
                        createdAt = today,
                        updatedAt = today
                ))))

        // ASSERT
        result.andExpect(view().name("auth/signup.html"))
                .andExpect(model().attributeHasFieldErrors("form", "username"))
    }

    @Test
    fun `signup - renders validation failure when name is empty`() {
        // ACT
        val result = mockMvc().perform(post("/signup")
                .params(signupRequestFor(User(
                        email = "irrelevant@example.org",
                        name = "",
                        password = "irrelevant",
                        createdAt = today,
                        updatedAt = today
                ))))

        // ASSERT
        result.andExpect(view().name("auth/signup.html"))
                .andExpect(model().attributeHasFieldErrors("form", "name"))
    }

    @Test
    fun `signup - renders validation failure when password is too short`() {
        // ACT
        val result = mockMvc().perform(post("/signup")
                .params(signupRequestFor(User(
                        email = "irrelevant@example.org",
                        name = "irrelevant",
                        password = "short",
                        createdAt = today,
                        updatedAt = today
                ))))

        // ASSERT
        result.andExpect(view().name("auth/signup.html"))
                .andExpect(model().attributeHasFieldErrors("form", "pass"))
    }

    @Test
    fun `signup - renders validation failure when password do not match`() {
        // ACT
        val result = mockMvc().perform(post("/signup")
                .param("username", "irrelevant@example.org")
                .param("name", "irrelevant")
                .param("pass", "goodpassword")
                .param("confirm", "doesnotmatch"))

        // ASSERT
        result.andExpect(view().name("auth/signup.html"))
                .andExpect(model().attributeHasFieldErrors("form", "pass"))
                .andExpect(model().attributeHasFieldErrors("form", "confirm"))
    }

    private fun signupRequestFor(user: User) =
            LinkedMultiValueMap<String, String>().apply {
                add("username", user.email)
                add("name", user.name)
                add("pass", user.password)
                add("confirm", user.password)
            }

    private fun expectedCreatedUserFor(user: User) =
            User(
                    email = user.email,
                    password = user.password,
                    name = user.name,
                    createdAt = today,
                    updatedAt = today
            )
}