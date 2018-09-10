package app.email

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class EmailTemplate(private val templateEngine: TemplateEngine,
                    private val emailService: EmailService) {

    fun send(from: String,
             to: String,
             subject: String,
             html: String,
             text: String,
             context: Map<String, Any>) {

        val locale = LocaleContextHolder.getLocale()
        val ctx = Context(locale, context)

        val textBody = templateEngine.process(text, ctx)
        val htmlBody = templateEngine.process(html, ctx)

        emailService.send(EmailMessage(from, to, subject, htmlBody, textBody))
    }

}
