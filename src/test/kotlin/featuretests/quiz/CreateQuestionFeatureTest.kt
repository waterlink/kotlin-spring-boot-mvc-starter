package featuretests.quiz

import app.quiz.Quiz
import app.quiz.create
import featuretests.auth.LoginFeatureTestHelper
import featuretests.auth.UserEntry
import helpers.FeatureTest
import org.fluentlenium.assertj.FluentLeniumAssertions.assertThat
import org.junit.Before
import org.junit.Test

class CreateQuestionFeatureTest : FeatureTest(), CreateQuestionFeatureTestHelper {
    @Before
    fun `before each`() {
        Given(`there are following signed-up users`(
                UserEntry(user = "kate@example.org")
        ))
        And(`I am logged in`(with = "kate@example.org"))
        And(`I have created the following quizzes`(
                Quiz.create(title = "My quiz", userId = 1)
        ))
    }

    @Test
    fun `showing the quiz page`() {
        Given(`I am on the quiz list page`())
        When(`I click on the quiz edit button for`(quiz = "My quiz"))
        Then(`I see the quiz edit page with`(title = "My quiz"))
        And(`I see that there are no questions`())
    }
}

interface CreateQuestionFeatureTestHelper : LoginFeatureTestHelper, CreateQuizFeatureTestHelper {
    val quizEditPage: QuizEditPage

    fun `I click on the quiz edit button for`(quiz: String) {
        quizListPage.clickOnEditQuiz(quiz)
    }

    fun `I see the quiz edit page with`(title: String) {
        val quizId = 2
        quizEditPage.isAt(quizId)

        assertThat(quizEditPage.titleValue()).isEqualTo(title)
    }

    fun `I see that there are no questions`() {
        quizEditPage.assertNoTitles()
    }
}