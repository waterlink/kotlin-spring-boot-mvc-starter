package app.email

import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Profile("cloud")
@Service
class RealEmailService(private val mailSender: JavaMailSender) : EmailService {

    override fun send(message: EmailMessage) {

        mailSender.send(
                mailSender.createMimeMessage(message.htmlBody != null) {
                    setFrom(message.from)
                    setTo(message.to)
                    setSubject(message.subject)

                    if (message.htmlBody != null) {
                        setText(message.textBody, message.htmlBody)
                    } else {
                        setText(message.textBody)
                    }
                }
        )
    }

    private fun JavaMailSender.createMimeMessage(multipart: Boolean, block: MimeMessageHelper.() -> Unit) =
            MimeMessageHelper(createMimeMessage(), multipart)
                    .apply(block)
                    .mimeMessage
}