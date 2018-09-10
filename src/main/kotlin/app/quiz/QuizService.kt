package app.quiz

import app.auth.CurrentUser
import app.quiz.images.ImageRepository
import app.quiz.images.ImageUploadException
import app.util.TimeProvider
import org.springframework.stereotype.Service

@Service
class QuizService(private val quizRepository: QuizRepository,
                  private val imageRepository: ImageRepository,
                  private val timeProvider: TimeProvider) {

    fun createQuiz(request: CreateQuizRequest) {
        val imageUrl = uploadImage(request.image)

        val quiz = Quiz(
                userId = request.userId,
                title = request.title,
                imageUrl = imageUrl,
                description = request.description,
                durationInMinutes = request.durationInMinutes,
                cta = request.cta,
                createdAt = timeProvider.now(),
                updatedAt = timeProvider.now()
        )

        quizRepository.create(quiz)
    }

    private fun uploadImage(image: ByteArray): String {
        try {
            return imageRepository.upload(image)
        } catch (e: Exception) {
            throw ImageUploadException(e)
        }
    }

    fun getMostRecentQuizzesForUser(currentUser: CurrentUser): List<Quiz> {
        return quizRepository.findMostRecentByUserId(currentUser.id)
    }

    fun getQuizForEditing(id: Long, currentUser: CurrentUser): Quiz {
        return quizRepository.findByIdAndUserId(id = id, userId = currentUser.id)
                ?: throw QuizNotFoundException()
    }
}
