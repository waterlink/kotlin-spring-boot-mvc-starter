package app.auth.dashboard

import app.auth.AuthService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class DashboardController(private val authService: AuthService) {

    @GetMapping("/dashboard")
    fun dashboard(model: Model, principal: Principal): String {
        val currentUser = authService.getCurrentUser(principal.name)

        model.addAttribute("currentUser", currentUser)

        return "auth/dashboard.html"
    }

}