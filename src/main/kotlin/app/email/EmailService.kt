package app.email

interface EmailService {
    fun send(message: EmailMessage)
}
