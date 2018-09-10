package app.quiz

import app.quiz.standardQuizzes.quizOne
import app.quiz.standardQuizzes.quizTwo
import app.auth.user.UserRepository
import app.auth.user.passwordEncoder
import app.auth.user.standardUsers.john
import app.auth.user.standardUsers.kate
import helpers.RepositoryTest
import helpers.resetSerial
import helpers.today
import helpers.yesterday
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class QuizRepositoryTest : RepositoryTest() {

    private lateinit var userRepository: UserRepository
    private lateinit var quizRepository: QuizRepository

    private val kateId = 1L
    private val johnId = 2L

    @Before
    fun `before each`() {
        jdbcTemplate.update("delete from quizzes cascade")
        jdbcTemplate.update("delete from users cascade")
        jdbcTemplate.resetSerial()

        userRepository = UserRepository(jdbcTemplate, passwordEncoder)
        quizRepository = QuizRepository(jdbcTemplate)

        userRepository.create(kate)
        userRepository.create(john)
    }

    @Test
    fun `create - creates new quiz`() {
        // ACT
        quizRepository.create(quizOne.copy(userId = 1))

        // ASSERT
        val quizzes = quizRepository.findAll()
        assertThat(quizzes).isEqualTo(listOf(
                quizOne.copy(id = 3, userId = 1)
        ))
    }

    @Test
    fun `create - creates other quiz`() {
        // ACT
        quizRepository.create(quizTwo.copy(userId = 2))

        // ASSERT
        val quizzes = quizRepository.findAll()
        assertThat(quizzes).isEqualTo(listOf(
                quizTwo.copy(id = 3, userId = 2)
        ))
    }

    @Test
    fun `findAll - finds multiple quizzes`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = 2))
        quizRepository.create(quizTwo.copy(userId = 2))

        // ACT
        val quizzes = quizRepository.findAll()

        // ASSERT
        assertThat(quizzes).isEqualTo(listOf(
                quizOne.copy(id = 3, userId = 2),
                quizTwo.copy(id = 4, userId = 2)
        ))
    }

    @Test
    fun `findAll - finds multiple quizzes with different starting serial`() {
        // ARRANGE
        jdbcTemplate.resetSerial(to = 42)
        quizRepository.create(quizTwo.copy(userId = 1))
        quizRepository.create(quizOne.copy(userId = 1))

        // ACT
        val quizzes = quizRepository.findAll()

        // ASSERT
        assertThat(quizzes).isEqualTo(listOf(
                quizTwo.copy(id = 42, userId = 1),
                quizOne.copy(id = 43, userId = 1)
        ))
    }

    @Test
    fun `findMostRecentByUserId - finds no quizzes when user doesn't have any`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = 1))
        quizRepository.create(quizTwo.copy(userId = 2))

        // ACT
        val quizzes = quizRepository.findMostRecentByUserId(42)

        // ASSERT
        assertThat(quizzes).isEmpty()
    }

    @Test
    fun `findMostRecentByUserId - finds a single quiz when there is only one`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = 1))
        quizRepository.create(quizTwo.copy(userId = 2))

        // ACT
        val quizzes = quizRepository.findMostRecentByUserId(1)

        // ASSERT
        assertThat(quizzes).isEqualTo(listOf(
                quizOne.copy(id = 3, userId = 1)
        ))
    }

    @Test
    fun `findMostRecentByUserId - finds multiple quizzes when there are multiple`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = 1, createdAt = yesterday))
        quizRepository.create(quizTwo.copy(userId = 1, createdAt = today))

        // ACT
        val quizzes = quizRepository.findMostRecentByUserId(1)

        // ASSERT
        assertThat(quizzes).isEqualTo(listOf(
                quizTwo.copy(id = 4, userId = 1, createdAt = today),
                quizOne.copy(id = 3, userId = 1, createdAt = yesterday)
        ))
    }

    @Test
    fun `findMostRecentByUserId - finds multiple quizzes when there are multiple when ordered differently`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = 1, createdAt = today))
        quizRepository.create(quizTwo.copy(userId = 1, createdAt = yesterday))

        // ACT
        val quizzes = quizRepository.findMostRecentByUserId(1)

        // ASSERT
        assertThat(quizzes).isEqualTo(listOf(
                quizOne.copy(id = 3, userId = 1, createdAt = today),
                quizTwo.copy(id = 4, userId = 1, createdAt = yesterday)
        ))
    }

    @Test
    fun `findByIdAndUserId - finds quiz by id and userId`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = kateId))
        quizRepository.create(quizTwo.copy(userId = kateId))

        // ACT
        val actualQuiz = quizRepository.findByIdAndUserId(4, kateId)

        // ASSERT
        val expectedQuiz = quizTwo.copy(id = 4, userId = kateId)
        assertThat(actualQuiz).isEqualTo(expectedQuiz)
    }

    @Test
    fun `findByIdAndUserId - finds different quiz by id and userId`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = kateId))
        quizRepository.create(quizTwo.copy(userId = kateId))

        // ACT
        val actualQuiz = quizRepository.findByIdAndUserId(3, kateId)

        // ASSERT
        val expectedQuiz = quizOne.copy(id = 3, userId = kateId)
        assertThat(actualQuiz).isEqualTo(expectedQuiz)
    }

    @Test
    fun `findByIdAndUserId - finds nothing when user does not match`() {
        // ARRANGE
        quizRepository.create(quizOne.copy(userId = kateId))
        quizRepository.create(quizTwo.copy(userId = kateId))

        // ACT
        val actualQuiz = quizRepository.findByIdAndUserId(3, johnId)

        // ASSERT
        assertThat(actualQuiz).isNull()
    }

}
