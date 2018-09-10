package app.auth.login

import app.auth.signup.ForceLoginService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

// This controller exists only for tests purposes and is not running in production
@Controller
class ForceLoginController(private val forceLoginService: ForceLoginService) {

    @GetMapping("/login/force", params = ["username"])
    fun forceLogin(@RequestParam username: String): String {
        forceLoginService.loginUserAfterConfirmation(username)
        return "redirect:/dashboard"
    }

}