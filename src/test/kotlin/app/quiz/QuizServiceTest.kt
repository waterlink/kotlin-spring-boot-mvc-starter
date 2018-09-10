package app.quiz

import app.auth.CurrentUser
import app.quiz.images.ImageRepository
import app.quiz.images.ImageUploadException
import app.quiz.standardQuizzes.quizOne
import app.quiz.standardQuizzes.quizTwo
import app.auth.user.standardUsers.kate
import app.util.TimeProvider
import com.nhaarman.mockito_kotlin.*
import helpers.today
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class QuizServiceTest {

    private val quizRepository = mock<QuizRepository>()
    private val imageRepository = mock<ImageRepository>()

    private val timeProvider = mock<TimeProvider> {
        on { now() } doReturn today
    }

    private val quizService = QuizService(quizRepository, imageRepository, timeProvider)

    @Test
    fun `createQuiz - saves an image and creates the quiz`() {
        // ARRANGE
        val image = "an image".toByteArray()
        val request = CreateQuizRequest(
                userId = 24,
                title = "a title",
                image = image,
                description = "a description",
                durationInMinutes = 7,
                cta = "a cta"
        )

        val imageUrl = "http://images.example.org/12/3456789.png"
        given(imageRepository.upload(image)).willReturn(imageUrl)

        // ACT
        quizService.createQuiz(request)

        // ASSERT
        verify(imageRepository).upload(image)
        verify(quizRepository).create(Quiz(
                userId = request.userId,
                title = request.title,
                imageUrl = imageUrl,
                description = request.description,
                durationInMinutes = request.durationInMinutes,
                cta = request.cta,
                createdAt = today,
                updatedAt = today
        ))
    }

    @Test
    fun `createQuiz - propagates image upload failure`() {
        // ARRANGE
        val image = "an image".toByteArray()
        val request = CreateQuizRequest(
                userId = 42,
                title = "a title",
                image = image,
                description = "a description",
                durationInMinutes = 7,
                cta = "a cta"
        )

        val uploadError = RuntimeException("image service is down")
        given(imageRepository.upload(image)).willThrow(uploadError)

        // ACT
        assertThatThrownBy { quizService.createQuiz(request) }
                .isInstanceOf(ImageUploadException::class.java)
                .hasCause(uploadError)

        // ASSERT
        verify(imageRepository).upload(image)
        verifyZeroInteractions(quizRepository)
    }

    @Test
    fun `getMostRecentQuizzesForUser - fetches quizzes for that user`() =
            listOf(42L, 37L).forEach { userId ->
                // ARRANGE
                val currentUser = CurrentUser(id = userId, name = "irrelevant")
                given(quizRepository.findMostRecentByUserId(userId))
                        .willReturn(listOf(quizOne, quizTwo))

                // ACT
                val quizzes = quizService.getMostRecentQuizzesForUser(currentUser)

                // ASSERT
                assertThat(quizzes).isEqualTo(listOf(quizOne, quizTwo))
            }

    @Test
    fun `getQuizForEditing - finds and returns the quiz`() {
        // ARRANGE
        val quizId = 31L
        val currentUser = CurrentUser(id = 18, name = kate.name)
        val quiz = quizTwo.copy(id = quizId, userId = currentUser.id)
        given(quizRepository.findByIdAndUserId(id = quizId, userId = currentUser.id))
                .willReturn(quiz)

        // ACT
        val actualQuiz = quizService.getQuizForEditing(id = quizId, currentUser = currentUser)

        // ASSERT
        assertThat(actualQuiz).isEqualTo(quiz)
    }

    @Test
    fun `getQuizForEditing - fails when quiz was not foudn`() {
        // ARRANGE
        val quizId = 31L
        val currentUser = CurrentUser(id = 18, name = kate.name)
        given(quizRepository.findByIdAndUserId(id = quizId, userId = currentUser.id))
                .willReturn(null)

        // ACT
        val result = assertThatThrownBy {
            quizService.getQuizForEditing(id = quizId, currentUser = currentUser)
        }

        // ASSERT
        result.isInstanceOf(QuizNotFoundException::class.java)
    }


}