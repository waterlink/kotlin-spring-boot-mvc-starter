package app.quiz

data class CreateQuizRequest(
        val userId: Long,
        val title: String,
        val image: ByteArray,
        val description: String,
        val durationInMinutes: Int,
        val cta: String
)
