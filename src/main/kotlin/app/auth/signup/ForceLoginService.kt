package app.auth.signup

import app.auth.SecurityContextWrapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class ForceLoginService(private val userDetailsService: UserDetailsService,
                        private val securityContextWrapper: SecurityContextWrapper) {

    // use this only when you know what you’re doing
    fun loginUserAfterConfirmation(email: String) {
        val userDetails = userDetailsService.loadUserByUsername(email)
        val authentication = UsernamePasswordAuthenticationToken(
                userDetails.username,
                userDetails.password,
                userDetails.authorities
        )
        securityContextWrapper.authentication = authentication
    }
}