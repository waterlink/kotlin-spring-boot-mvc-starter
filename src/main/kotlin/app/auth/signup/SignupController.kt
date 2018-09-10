package app.auth.signup

import app.auth.AuthService
import app.auth.user.User
import app.auth.user.UserExistsAlreadyException
import app.util.TimeProvider
import app.util.getBaseUrlFrom
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import javax.servlet.http.HttpServletRequest
import javax.validation.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.reflect.KClass

@Controller
class SignupController(private val authService: AuthService,
                       private val timeProvider: TimeProvider) {

    @GetMapping("/signup")
    fun signupForm(model: Model): String {
        model.addAttribute("form", SignupRequest())
        return "auth/signup.html"
    }

    @PostMapping("/signup")
    fun signup(@Valid @ModelAttribute("form") form: SignupRequest,
               bindingResult: BindingResult,
               model: Model,
               httpRequest: HttpServletRequest): String {

        if (bindingResult.hasErrors()) {
            return "auth/signup.html"
        }

        val user = User(
                email = form.username,
                name = form.name,
                password = form.pass,
                createdAt = timeProvider.now(),
                updatedAt = timeProvider.now()
        )
        val baseUrl = getBaseUrlFrom(httpRequest)

        try {
            authService.signupUser(user, baseUrl)
        } catch (e: UserExistsAlreadyException) {
            model.addAttribute("error", "signup.user_already_taken")
            return "auth/signup.html"
        }

        return "redirect:/thank-you"
    }

    @GetMapping("/thank-you")
    fun thankYou() = "auth/thank_you.html"

}

@PasswordsMatch
data class SignupRequest(@field:NotBlank(message = "{validation.not_blank}")
                         @field:Email(message = "{validation.valid_email}")
                         val username: String = "",

                         @field:NotBlank(message = "{validation.not_blank}")
                         val name: String = "",

                         @field:Size(min = 10, message = "{validation.password_min_size}")
                         val pass: String = "",

                         val confirm: String = "")

class PasswordsMatchValidator : ConstraintValidator<PasswordsMatch, SignupRequest> {
    private lateinit var message: String

    override fun initialize(constraintAnnotation: PasswordsMatch) {
        message = constraintAnnotation.message
    }

    override fun isValid(value: SignupRequest, context: ConstraintValidatorContext): Boolean {
        if (value.pass == value.confirm) return true

        context.disableDefaultConstraintViolation()

        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("pass")
                .addConstraintViolation()

        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("confirm")
                .addConstraintViolation()

        return false
    }
}

@MustBeDocumented
@Constraint(validatedBy = [PasswordsMatchValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PasswordsMatch(
        val message: String = "{validation.passwords_match}",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
