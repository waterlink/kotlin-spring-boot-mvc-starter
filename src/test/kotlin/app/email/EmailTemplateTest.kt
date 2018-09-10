package app.email

import com.nhaarman.mockito_kotlin.verify
import helpers.EmailTest
import org.junit.Test

class EmailTemplateTest : EmailTest() {

    @Test
    fun `render - creates an email message from template and model`() {
        emailTemplate.send(
                from = "noreply@example.org",
                to = "john@example.org",
                subject = "Please confirm your account",
                html = "test_emails/testEmail.html",
                text = "test_emails/testEmail.txt",
                context = mapOf<String, Any>(
                        "name" to "John",
                        "confirmUrl" to "https://example.org/confirm/valid-code"
                )
        )

        verify(emailService).send(EmailMessage(
                from = "noreply@example.org",
                to = "john@example.org",
                subject = "Please confirm your account",
                htmlBody = """
                    <!DOCTYPE html>
                    <html>
                    <body>
                    <p>Hey, <span>John</span>!</p>

                    <p>Please click on the following link:</p>

                    <p><a href="https://example.org/confirm/valid-code">Confirm my account</a></p>
                    </body>
                    </html>
                """.trimIndent(),
                textBody = """
                    Hey, John!

                    Please copy and paste the following link into your browser:

                    https://example.org/confirm/valid-code
                """.trimIndent()
        ))
    }
}
