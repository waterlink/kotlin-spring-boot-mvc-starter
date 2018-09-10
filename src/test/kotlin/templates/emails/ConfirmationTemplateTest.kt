package templates.emails

import app.email.EmailMessage
import com.nhaarman.mockito_kotlin.verify
import helpers.EmailTest
import org.junit.Test

class ConfirmationTemplateTest : EmailTest() {

    @Test
    fun `renders a confirmation email given name and link`() {
        emailTemplate.send(
                from = "from@address.example.org",
                to = "kate@example.org",
                subject = "Please confirm your account",
                html = "emails/confirmation.html",
                text = "emails/confirmation.txt",
                context = mapOf<String, Any>(
                        "name" to "Kate",
                        "confirmUrl" to "https://address.example.org/confirm/valid-code-for-kate"
                )
        )

        verify(emailService).send(EmailMessage(
                from = "from@address.example.org",
                to = "kate@example.org",
                subject = "Please confirm your account",
                htmlBody = """
                    <!DOCTYPE html>
                    <html>
                    <body>
                    <p>Hey, <span>Kate</span>!</p>

                    <p>To complete your signup, please click on the following link:</p>

                    <p><a href="https://address.example.org/confirm/valid-code-for-kate">Confirm my account</a></p>

                    <p>Or copy and paste the following link into your browser:
                        <span>https://address.example.org/confirm/valid-code-for-kate</span></p>

                    <p>Thank you,<br>
                        example.org</p>
                    </body>
                    </html>
                """.trimIndent(),
                textBody = """
                    Hey, Kate!

                    To complete your signup, please verify your account by copying and pasting the following link into your browser:

                    https://address.example.org/confirm/valid-code-for-kate

                    Thank you,
                    example.org
                """.trimIndent()
        ))

    }
}
