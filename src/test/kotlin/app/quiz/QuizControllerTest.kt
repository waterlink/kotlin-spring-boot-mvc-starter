package app.quiz

import app.auth.AuthService
import app.auth.CurrentUser
import app.quiz.standardQuizzes.quizOne
import app.quiz.standardQuizzes.quizTwo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import helpers.MockMvcTest
import org.junit.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.security.Principal

class QuizControllerTest : MockMvcTest {
    private val quizService = mock<QuizService>()

    private val currentUser = CurrentUser(id = 29, name = "user@example.org")
    private val authService = mock<AuthService>() {
        on { getCurrentUser("user@example.org") } doReturn currentUser
    }

    private val principal = mock<Principal> {
        on { name } doReturn "user@example.org"
    }

    override fun controller() = QuizController(quizService, authService)

    @Test
    fun `createQuiz - redirects to quiz list when successful`() {
        // ARRANGE
        val uploadedImage = "image here".toByteArray()

        // ACT
        val result = mockMvc().perform(multipart("/quizzes")
                .file("image", uploadedImage)
                .param("title", "Quiz title")
                .param("description", "Quiz description")
                .param("duration", "3")
                .param("cta", "Quiz CTA")
                .principal(principal))

        // ASSERT
        result.andExpect(status().isFound)
                .andExpect(redirectedUrl("/quizzes"))
    }

    @Test
    fun `createQuiz - creates the quiz`() {
        // ARRANGE
        val uploadedImage = "image here".toByteArray()

        // ACT
        mockMvc().perform(multipart("/quizzes")
                .file("image", uploadedImage)
                .param("title", "Quiz title")
                .param("description", "Quiz description")
                .param("duration", "3")
                .param("cta", "Quiz CTA")
                .principal(principal))

        // ASSERT
        verify(quizService).createQuiz(CreateQuizRequest(
                userId = 29,
                title = "Quiz title",
                image = uploadedImage,
                description = "Quiz description",
                durationInMinutes = 3,
                cta = "Quiz CTA"
        ))
    }

    @Test
    fun `createQuiz - validates fields`() {
        // ARRANGE
        val uploadedImage = "image here".toByteArray()

        // ACT
        val result = mockMvc().perform(multipart("/quizzes")
                .file("image", uploadedImage)
                .param("title", "")
                .param("description", "")
                .param("duration", "-1")
                .param("cta", "")
                .principal(principal))

        // ASSERT
        result.andExpect(view().name("quizzes/new.html"))
                .andExpect(model().attributeHasFieldErrors("form", "title"))
                .andExpect(model().attributeHasFieldErrors("form", "description"))
                .andExpect(model().attributeHasFieldErrors("form", "duration"))
                .andExpect(model().attributeHasFieldErrors("form", "cta"))
    }

    @Test
    fun `createQuiz - duration can't be empty`() {
        // ACT
        val result = mockMvc().perform(multipart("/quizzes").principal(principal))

        // ASSERT
        result.andExpect(view().name("quizzes/new.html"))
                .andExpect(model().attributeHasFieldErrors("form", "duration"))
    }


    @Test
    fun `createQuiz - validates image upload when not present`() {
        // ACT
        val result = mockMvc().perform(multipart("/quizzes").principal(principal))

        // ASSERT
        result.andExpect(view().name("quizzes/new.html"))
                .andExpect(model().attributeHasFieldErrors("form", "image"))
    }

    @Test
    fun `createQuiz - validates image upload when empty`() {
        // ARRANGE
        val emptyUpload = mock<MockMultipartFile> {
            on { isEmpty } doReturn true
            on { name } doReturn "image"
        }

        // ACT
        val result = mockMvc().perform(multipart("/quizzes")
                .file(emptyUpload)
                .principal(principal))

        // ASSERT
        result.andExpect(view().name("quizzes/new.html"))
                .andExpect(model().attributeHasFieldErrors("form", "image"))
    }

    @Test
    fun `editQuiz - finds quiz and renders its form`() {
        // ARRANGE
        val quiz = quizOne.copy(id = 42, userId = 29)
        given(quizService.getQuizForEditing(42, currentUser)).willReturn(quiz)

        // ACT
        val result = mockMvc().perform(get("/quizzes/42/edit").principal(principal))

        // ASSERT
        result.andExpect(view().name("quizzes/edit.html"))
                .andExpect(model().attribute("form", EditQuizForm(
                        id = quiz.id,
                        title = quiz.title,
                        description = quiz.description,
                        duration = quiz.durationInMinutes,
                        cta = quiz.cta,
                        currentImageUrl = quiz.imageUrl
                )))
    }

    @Test
    fun `editQuiz - returns not found when quiz is not found`() {
        // ARRANGE
        given(quizService.getQuizForEditing(42, currentUser))
                .willThrow(QuizNotFoundException())

        // ACT
        val result = mockMvc().perform(get("/quizzes/42/edit").principal(principal))

        // ASSERT
        result.andExpect(status().isNotFound)
    }

    @Test
    fun `listQuizzes - lists quizzes`() {
        // ARRANGE
        val quizzes = listOf(quizOne, quizTwo)
        given(quizService.getMostRecentQuizzesForUser(currentUser))
                .willReturn(quizzes)

        // ACT
        val result = mockMvc().perform(get("/quizzes").principal(principal))

        // ASSERT
        result.andExpect(view().name("quizzes/list.html"))
                .andExpect(model().attribute("quizzes", quizzes))
    }

}