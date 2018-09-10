package app.email

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import helpers.component1
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.mail.javamail.JavaMailSender
import java.io.ByteArrayOutputStream
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class RealEmailServiceTest {
    private var lastMessageSent: MimeMessage? = null
    private val savingMailSender = mock<JavaMailSender> {
        val stubMessage = MimeMessage(mock<MimeMessage>())

        on { send(any<MimeMessage>()) } doAnswer { (message) ->
            lastMessageSent = message as? MimeMessage
            Unit
        }

        on { createMimeMessage() } doReturn stubMessage
    }

    private val emailService = RealEmailService(savingMailSender)

    @Test
    fun `send - sends email with text only`() {
        val message = EmailMessage(
                from = "hello@example.org",
                to = "kate@example.org",
                subject = "Hi from Example Org",
                htmlBody = null,
                textBody = "O, hi there, Kate!"
        )

        emailService.send(message)

        assertThat(lastMessageSent?.from).isEqualTo(arrayOf(InternetAddress(message.from)))
        assertThat(lastMessageSent?.allRecipients).isEqualTo(arrayOf(InternetAddress(message.to)))
        assertThat(lastMessageSent?.subject).isEqualTo(message.subject)
        assertThat(lastMessageSent?.content).isEqualTo(message.textBody)
    }

    @Test
    fun `send - sends email with text and html`() {
        val message = EmailMessage(
                from = "hello@example.org",
                to = "kate@example.org",
                subject = "Hi from Example Org",
                htmlBody = "<p>O, hi there, <i>Kate</i>!</p>",
                textBody = "O, hi there, Kate!"
        )

        emailService.send(message)

        assertThat(lastMessageSent?.from).isEqualTo(arrayOf(InternetAddress(message.from)))
        assertThat(lastMessageSent?.allRecipients).isEqualTo(arrayOf(InternetAddress(message.to)))
        assertThat(lastMessageSent?.subject).isEqualTo(message.subject)

        val content = lastMessageSent?.content as MimeMultipart
        val stream = ByteArrayOutputStream()
        content.writeTo(stream)
        val result = stream.toString("utf-8")

        assertThat(result).contains(message.htmlBody)
        assertThat(result).contains(message.textBody)
    }

}