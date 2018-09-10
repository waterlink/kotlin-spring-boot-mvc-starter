package app.auth.login

import org.springframework.security.authentication.DisabledException
import org.springframework.security.web.WebAttributes
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

@Controller
class LoginController {

    @RequestMapping("/login", params = ["error"])
    fun loginFailure(@RequestParam error: String,
                     request: HttpServletRequest,
                     model: Model): String {

        var concreteError = "login.bad_credentials"

        val exception = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
        val isNotConfirmed = exception is DisabledException
        if (isNotConfirmed) {
            concreteError = "login.non_confirmed_user"
        }

        if (error == "confirmation_link_was_already_used") {
            concreteError = "login.confirmation_link_was_already_used"
        }

        model.addAttribute("nonConfirmedUser", isNotConfirmed)
        model.addAttribute("error", concreteError)

        return "auth/login.html"
    }

    @RequestMapping("/login", params = ["logout"])
    fun signOutSuccess(model: Model): String {
        model.addAttribute("signOutInfo", "login.successful_sign_out")
        model.addAttribute("nonConfirmedUser", false)
        return "auth/login.html"
    }

    @RequestMapping("/login")
    fun login(): String {
        return "auth/login.html"
    }

}
