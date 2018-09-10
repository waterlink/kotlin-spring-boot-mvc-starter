package app.auth

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityContextWrapper {
    var authentication: Authentication
        get() = SecurityContextHolder.getContext().authentication
        set(authentication) {
            SecurityContextHolder.getContext().authentication = authentication
        }
}
