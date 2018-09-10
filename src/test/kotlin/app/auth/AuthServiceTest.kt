package app.auth

import app.auth.config.ConfirmationEmailsConfig
import app.auth.signup.CodeUsedAlreadyException
import app.auth.signup.ConfirmationLink
import app.auth.signup.ConfirmationLinkService
import app.auth.signup.InvalidCodeException
import app.email.EmailTemplate
import app.auth.user.UserExistsAlreadyException
import app.auth.user.UserNotFoundException
import app.auth.user.UserRepository
import app.auth.user.standardUsers
import app.auth.user.standardUsers.john
import app.auth.user.standardUsers.kate
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class AuthServiceTest {

    private val userRepository = mock<UserRepository>()
    private val confirmationLinkService = mock<ConfirmationLinkService>()
    private val emailTemplate = mock<EmailTemplate>()

    private val confirmationEmailsConfig = ConfirmationEmailsConfig().apply {
        from = "Alex <alex@example.org>"
    }

    private val userService = AuthService(
            userRepository,
            emailTemplate,
            confirmationLinkService,
            confirmationEmailsConfig
    )

    private val baseUrl = "http://localhost:8080"

    @Test
    fun `signupUser - creates a user account with confirmation code`() =
            standardUsers.all.forEach { user ->
                // ARRANGE
                given(confirmationLinkService.generate(baseUrl)).willReturn(ConfirmationLink(
                        href = "some-href",
                        code = "confirmation-code"
                ))

                // ACT
                userService.signupUser(user, baseUrl)

                // ASSERT
                verify(userRepository).create(user.copy(confirmationCode = "confirmation-code"))
            }

    @Test
    fun `signupUser - sends a confirmation email`() = standardUsers.all.forEach { user ->
        // ARRANGE
        val confirmationLink = ConfirmationLink(
                href = "https://example.org/confirmation-link-for-${user.name}",
                code = "confirmation-link-for-${user.name}"
        )
        given(confirmationLinkService.generate(baseUrl)).willReturn(confirmationLink)

        // ACT
        userService.signupUser(user, baseUrl)

        // ASSERT
        verify(emailTemplate).send(
                from = confirmationEmailsConfig.from,
                to = "${user.name} <${user.email}>",
                subject = "Please confirm your account",
                html = "emails/confirmation.html",
                text = "emails/confirmation.txt",
                context = mapOf(
                        "name" to user.name,
                        "confirmUrl" to confirmationLink.href
                )
        )
    }

    // ASSERT
    @Test(expected = UserExistsAlreadyException::class)
    fun `signupUser - fails when user already exists`() {
        // ARRANGE
        given(userRepository.findByEmail(kate.email)).willReturn(kate)

        // ACT
        userService.signupUser(kate, baseUrl)
    }

    @Test
    fun `getCurrentUser - finds current user by email`() =
            standardUsers.all.forEachIndexed { index, user ->
                // ARRANGE
                val id = 42L + index
                given(userRepository.findByEmail(user.email))
                        .willReturn(user.copy(id = id))

                // ACT
                val currentUser = userService.getCurrentUser(user.email)

                // ASSERT
                assertThat(currentUser).isEqualTo(
                        CurrentUser(id = id, name = user.name)
                )
            }

    @Test
    fun `getCurrentUser - fails when user not found`() {
        // ARRANGE
        given(userRepository.findByEmail("missing@example.org"))
                .willReturn(null)

        // ACT
        val result = assertThatThrownBy {
            userService.getCurrentUser("missing@example.org")
        }

        // ASSERT
        result.isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Current logged in user can't be missing!")
    }

    @Test
    fun `getCurrentUser - fails when found user does not have an id`() {
        // ARRANGE
        given(userRepository.findByEmail("kate@example.org"))
                .willReturn(kate)

        // ACT
        val result = assertThatThrownBy {
            userService.getCurrentUser("kate@example.org")
        }

        // ASSERT
        result.isInstanceOf(IllegalStateException::class.java)
                .hasMessageContaining("Existing user has to have an id!")
    }

    @Test
    fun `confirmAndGetUser - confirms by valid code`() = standardUsers.all.forEach { user ->
        // ARRANGE
        val code = "valid-code"
        val unconfirmedUser = user.copy(confirmed = false)
        given(userRepository.findByConfirmationCode(code)).willReturn(unconfirmedUser)

        // ACT
        val foundUser = userService.confirmAndGetUser(code)

        // ASSERT
        val expectedConfirmedUser = user.copy(confirmed = true)
        assertThat(foundUser).isEqualTo(expectedConfirmedUser)
        verify(userRepository).confirm(expectedConfirmedUser)
    }

    @Test
    fun `confirmAndGetUser - confirms by other valid code`() {
        // ARRANGE
        val code = "other-valid-code"
        val unconfirmedUser = john.copy(confirmed = false)
        given(userRepository.findByConfirmationCode(code)).willReturn(unconfirmedUser)

        // ACT
        userService.confirmAndGetUser(code)

        // ASSERT
        verify(userRepository).confirm(john.copy(confirmed = true))
    }

    // ASSERT
    @Test(expected = InvalidCodeException::class)
    fun `confirmAndGetUser - fails when code is invalid`() {
        // ARRANGE
        val code = "invalid-code"
        given(userRepository.findByConfirmationCode(code)).willReturn(null)

        // ACT
        userService.confirmAndGetUser(code)
    }

    // ASSERT
    @Test(expected = CodeUsedAlreadyException::class)
    fun `confirmAndGetUser - fails when code was already used`() {
        // ARRANGE
        val code = "already-used-code"
        given(userRepository.findByConfirmationCode(code))
                .willReturn(kate.copy(confirmed = true))

        // ACT
        userService.confirmAndGetUser(code)
    }

    @Test
    fun `resendConfirmation - re-sends confirmation email`() = standardUsers.all.forEach { user ->
        // ARRANGE
        val confirmationLink = ConfirmationLink(
                href = "https://example.org/confirmation-link-for-${user.name}",
                code = "confirmation-link-for-${user.name}"
        )
        given(confirmationLinkService.generate(baseUrl)).willReturn(confirmationLink)
        given(userRepository.findByEmail(user.email)).willReturn(user)

        // ACT
        userService.resendConfirmation(user.email, baseUrl)

        // ASSERT
        verify(emailTemplate).send(
                from = confirmationEmailsConfig.from,
                to = "${user.name} <${user.email}>",
                subject = "Please confirm your account",
                html = "emails/confirmation.html",
                text = "emails/confirmation.txt",
                context = mapOf(
                        "name" to user.name,
                        "confirmUrl" to confirmationLink.href
                )
        )
    }

    @Test
    fun `resendConfirmation - updates user confirmation code`() =
            standardUsers.all.forEach { user ->
                // ARRANGE
                given(confirmationLinkService.generate(baseUrl)).willReturn(ConfirmationLink(
                        href = "some-href",
                        code = "new-confirmation-code"
                ))
                given(userRepository.findByEmail(user.email)).willReturn(user)

                // ACT
                userService.resendConfirmation(user.email, baseUrl)

                // ASSERT
                verify(userRepository).updateConfirmationCode(
                        user.copy(confirmationCode = "new-confirmation-code")
                )
            }

    // ASSERT
    @Test(expected = UserNotFoundException::class)
    fun `resendConfirmation - fails when user not found`() {
        // ARRANGE
        val email = "missing@example.org"
        given(confirmationLinkService.generate(baseUrl)).willReturn(ConfirmationLink(
                href = "some-href",
                code = "new-confirmation-code"
        ))
        given(userRepository.findByEmail(email)).willReturn(null)

        // ACT
        userService.resendConfirmation(email, baseUrl)
    }

}