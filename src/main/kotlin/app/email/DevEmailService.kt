package app.email

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("dev")
@Service
class DevEmailService : EmailService {
    override fun send(message: EmailMessage) {
        println("from: ${message.from}")
        println("to: ${message.to}")
        println("subject: ${message.subject}")
        println(message.textBody)
    }
}