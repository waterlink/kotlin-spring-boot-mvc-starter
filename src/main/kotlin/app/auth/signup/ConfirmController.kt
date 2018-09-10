package app.auth.signup

import app.auth.AuthService
import app.auth.user.UserNotFoundException
import app.util.getBaseUrlFrom
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
class ConfirmController(private val authService: AuthService,
                        private val forceLoginService: ForceLoginService) {

    @GetMapping("/confirm/{code}")
    fun confirm(@PathVariable code: String): String {
        return try {
            val user = authService.confirmAndGetUser(code)
            forceLoginService.loginUserAfterConfirmation(user.email)
            "redirect:/dashboard"
        } catch (e: InvalidCodeException) {
            "auth/invalid_confirmation_link.html"
        } catch (e: CodeUsedAlreadyException) {
            "redirect:/login?error=confirmation_link_was_already_used"
        }
    }

    @PostMapping("/resend-confirmation")
    fun resendConfirmation(@RequestParam username: String,
                           httpRequest: HttpServletRequest): String {
        val baseUrl = getBaseUrlFrom(httpRequest)
        authService.resendConfirmation(username, baseUrl)
        return "redirect:/thank-you"
    }

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFoundException() = Unit
}
