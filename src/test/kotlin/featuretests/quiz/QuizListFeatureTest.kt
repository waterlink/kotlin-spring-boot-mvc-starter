package featuretests.quiz

import app.quiz.Quiz
import app.quiz.standardQuizzes.quizOne
import app.quiz.standardQuizzes.quizTwo
import featuretests.auth.LoginFeatureTestHelper
import featuretests.auth.UserEntry
import helpers.FeatureTest
import helpers.today
import helpers.yesterday
import org.fluentlenium.assertj.FluentLeniumAssertions.assertThat
import org.junit.Test

class QuizListFeatureTest : FeatureTest(), QuizListFeatureTestHelper {
    @Test
    fun `lists quizzes in the most-recent-first order`() {
        Given(`there are following signed-up users`(UserEntry(user = "kate@example.org")))
        And(`I am logged in`(with = "kate@example.org"))
        And(`I have created the following quizzes`(
                quizOne.copy(createdAt = yesterday),
                quizTwo.copy(createdAt = today)
        ))

        When(`I go to quiz list page`())

        Then(`I see quizzes in the following order`(
                quizTwo.copy(createdAt = today),
                quizOne.copy(createdAt = yesterday)
        ))
    }
}

interface QuizListFeatureTestHelper : LoginFeatureTestHelper, CreateQuizFeatureTestHelper {
    fun `I see quizzes in the following order`(vararg quizzes: Quiz) {
        assertThat(quizListPage.quizTitles()).isEqualTo(
                quizzes.map { it.title }
        )
    }
}
