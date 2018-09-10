package app.auth

import app.auth.config.ConfirmationEmailsConfig
import app.auth.signup.CodeUsedAlreadyException
import app.auth.signup.ConfirmationLink
import app.auth.signup.ConfirmationLinkService
import app.auth.signup.InvalidCodeException
import app.email.EmailTemplate
import app.auth.user.User
import app.auth.user.UserExistsAlreadyException
import app.auth.user.UserNotFoundException
import app.auth.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(private val userRepository: UserRepository,
                  private val emailTemplate: EmailTemplate,
                  private val confirmationLinkService: ConfirmationLinkService,
                  private val confirmationEmailsConfig: ConfirmationEmailsConfig) {

    fun signupUser(user: User, baseUrl: String) {
        if (userRepository.findByEmail(user.email) != null) {
            throw UserExistsAlreadyException()
        }

        val confirmationLink = confirmationLinkService.generate(baseUrl)

        userRepository.create(user.copy(
                confirmationCode = confirmationLink.code
        ))

        sendConfirmationEmail(user, confirmationLink)
    }

    fun getCurrentUser(email: String): CurrentUser {
        val user = userRepository.findByEmail(email)
                ?: throw IllegalStateException("Current logged in user can't be missing!")

        val id = user.id
                ?: throw IllegalStateException("Existing user has to have an id!")

        return CurrentUser(id = id, name = user.name)
    }

    @Transactional
    fun confirmAndGetUser(code: String): User {
        val user = userRepository.findByConfirmationCode(code)
                ?: throw InvalidCodeException()

        if (user.confirmed) {
            throw CodeUsedAlreadyException()
        }

        val confirmedUser = user.copy(confirmed = true)

        userRepository.confirm(confirmedUser)
        return confirmedUser
    }

    @Transactional
    fun resendConfirmation(email: String, baseUrl: String) {
        val user = userRepository.findByEmail(email)
                ?: throw UserNotFoundException()

        val confirmationLink = confirmationLinkService.generate(baseUrl)

        userRepository.updateConfirmationCode(
                user.copy(confirmationCode = confirmationLink.code)
        )

        sendConfirmationEmail(user, confirmationLink)
    }

    fun sendConfirmationEmail(user: User, confirmationLink: ConfirmationLink) {
        emailTemplate.send(
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
}
