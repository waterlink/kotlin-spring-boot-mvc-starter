package app.quiz

import helpers.today
import java.time.LocalDateTime

fun Quiz.Companion.create(
        userId: Long = 42,
        title: String = "irrelevant",
        imageUrl: String = "irrelevant",
        description: String = "irrelevant",
        durationInMinutes: Int = 3,
        cta: String = "irrelevant",
        createdAt: LocalDateTime = today,
        updatedAt: LocalDateTime = today
) = Quiz(
        userId = userId,
        title = title,
        imageUrl = imageUrl,
        description = description,
        durationInMinutes = durationInMinutes,
        cta = cta,
        createdAt = createdAt,
        updatedAt = updatedAt
)

@Suppress("ClassName")
object standardQuizzes {

    val quizOne = Quiz.create(
            userId = 1,
            title = "title one",
            imageUrl = "image one",
            description = "description one",
            durationInMinutes = 3,
            cta = "cta one"
    )

    val quizTwo = Quiz.create(
            userId = 2,
            title = "title two",
            imageUrl = "image two",
            description = "description two",
            durationInMinutes = 6,
            cta = "cta two"
    )

    val all = listOf(quizOne, quizTwo)

}
