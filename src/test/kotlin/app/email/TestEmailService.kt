package app.email

import org.springframework.stereotype.Service

@Service
class TestEmailService : EmailService {
    var lastEmail: EmailMessage? = null

    override fun send(message: EmailMessage) {
        lastEmail = message
    }
}
