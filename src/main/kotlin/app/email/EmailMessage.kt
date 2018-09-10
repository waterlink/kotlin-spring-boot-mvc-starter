package app.email

data class EmailMessage(val from: String,
                        val to: String,
                        val subject: String,
                        val htmlBody: String?,
                        val textBody: String)
