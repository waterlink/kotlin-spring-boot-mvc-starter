package app.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.auth.confirmation-emails")
class ConfirmationEmailsConfig {
    lateinit var from: String
}
