package app.quiz

import java.time.LocalDateTime

data class Quiz(val id: Long? = null,
                val userId: Long,
                val title: String,
                val imageUrl: String,
                val description: String,
                val durationInMinutes: Int,
                val cta: String,
                val createdAt: LocalDateTime,
                val updatedAt: LocalDateTime) {

    companion object

}
