package featuretests.quiz

import app.quiz.CreateQuizRequest
import app.quiz.Quiz
import app.quiz.QuizService
import featuretests.auth.LoginFeatureTestHelper
import featuretests.auth.UserEntry
import helpers.FeatureTest
import org.fluentlenium.assertj.FluentLeniumAssertions.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File

class CreateQuizFeatureTest : FeatureTest(), CreateQuizFeatureTestHelper {

    @Before
    fun setUp() {
        Given(`there are following signed-up users`(
                UserEntry(user = "sarah@example.org")
        ))
        And(`I am logged in`(with = "sarah@example.org"))
    }

    @Test
    fun `initiating creation of quiz from the quiz list page`() {
        Given(`I am on the quiz list page`())
        When(`I click on create new quiz button`())
        Then(`I see the new quiz page`())
    }

    @Test
    fun `creating the quiz`() {
        Given(`I am on the new quiz page`())

        When(`I enter the title`("How much do you actually know about extension functions in Kotlin?"))
        And(`I upload an image`("extension_functions_quiz.png"))
        And(`I enter the description`(
                "Answer these 7 questions, and see what great features of " +
                        "extension function in Kotlin you are missing out on!"))
        And(`I select the quiz duration`("3 min."))
        And(`I enter the CTA text`("Take a quiz and learn!"))
        And(`I click on the submit button`())

        Then(`I see the quiz list page`())
        And(`I see the quiz with`(
                title = "How much do you actually know about extension functions in Kotlin?",
                image = "extension_functions_quiz.png",
                description = "Answer these 7 questions, and see what great features of " +
                        "extension function in Kotlin you are missing out on!",
                duration = "It will take you only 3 minutes!",
                cta = "Take a quiz and learn!",
                ctaUrl = "/quizzes/2"
        ))
    }

    // sad paths
    @Test
    fun `empty title`() {
        When(`I create a quiz with`(title = ""))
        Then(`I still see the new quiz page`())
        And(`I see that the title can't be empty`())
    }

    @Test
    fun `empty description`() {
        When(`I create a quiz with`(description = ""))
        Then(`I still see the new quiz page`())
        And(`I see that the description can't be empty`())
    }

    @Test
    fun `empty duration`() {
        When(`I create a quiz with`(duration = null))
        Then(`I still see the new quiz page`())
        And(`I see that the duration can't be empty`())
    }

    @Test
    fun `empty cta`() {
        When(`I create a quiz with`(cta = ""))
        Then(`I still see the new quiz page`())
        And(`I see that the cta can't be empty`())
    }

    @Test
    fun `empty image`() {
        When(`I create a quiz with`(image = null))
        Then(`I still see the new quiz page`())
        And(`I see that the image can't be empty`())
    }

}

interface CreateQuizFeatureTestHelper : LoginFeatureTestHelper {
    val newQuizPage: NewQuizPage
    val quizService: QuizService
    val quizListPage: QuizListPage

    fun `I am on the new quiz page`() {
        goTo(newQuizPage)
    }

    fun `I have created the following quizzes`(vararg quizzes: Quiz) {
        quizzes.forEach {
            quizService.createQuiz(CreateQuizRequest(
                    userId = 1,
                    title = it.title,
                    image = "".toByteArray(),
                    description = it.description,
                    durationInMinutes = it.durationInMinutes,
                    cta = it.cta
            ))
        }
    }

    fun `I go to quiz list page`() {
        goTo(quizListPage)
    }

    fun `I am on the quiz list page`() = `I go to quiz list page`()

    fun `I click on create new quiz button`() {
        quizListPage.clickOnNewQuiz()
    }

    fun `I see the new quiz page`() {
        assertThat(newQuizPage).isAt
    }

    fun `I still see the new quiz page`() {
        assertThat(newQuizPage.afterSubmit).isAt
    }

    fun `I enter the title`(title: String) {
        newQuizPage.enterTitle(title)
    }

    fun `I upload an image`(image: String) {
        newQuizPage.uploadImage(imagePath(image))
    }

    fun `I enter the description`(description: String) {
        newQuizPage.enterDescription(description)
    }

    fun `I select the quiz duration`(duration: String) {
        newQuizPage.selectDuration(duration)
    }

    fun `I enter the CTA text`(cta: String) {
        newQuizPage.enterCtaText(cta)
    }

    fun `I click on the submit button`() {
        newQuizPage.clickOnSubmit()
    }

    fun `I create a quiz with`(title: String = "irrelevant",
                               image: String? = "extension_functions_quiz.png",
                               description: String = "irrelevant",
                               duration: String? = "5 min.",
                               cta: String = "irrelevant") {
        `I am on the new quiz page`()

        `I enter the title`(title)

        if (image != null) {
            `I upload an image`(image)
        }

        `I enter the description`(description)

        if (duration != null) {
            `I select the quiz duration`(duration)
        }

        `I enter the CTA text`(cta)
        `I click on the submit button`()
    }

    fun `I see the quiz list page`() {
        assertThat(quizListPage).isAt
    }

    fun `I see the quiz with`(
            title: String,
            image: String,
            description: String,
            duration: String,
            cta: String,
            ctaUrl: String
    ) {
        val view = quizListPage.findQuizBy(title)

        assertThat(view.copy(image = "N/A")).isEqualTo(QuizView(
                title = title,
                image = "N/A",
                description = description,
                duration = duration,
                cta = cta,
                ctaUrl = "$baseUrl$ctaUrl"
        ))

        assertCorrectImage(
                actualUrl = view.image,
                expectedFile = imagePath(image)
        )
    }

    fun `I see that the title can't be empty`() {
        assertThat(newQuizPage.titleValidationText()).contains("Title can't be empty")
    }

    fun `I see that the description can't be empty`() {
        assertThat(newQuizPage.descriptionValidationText()).contains("Short description can't be empty")
    }

    fun `I see that the duration can't be empty`() {
        assertThat(newQuizPage.durationValidationText()).contains("Duration (in minutes) can't be empty")
    }

    fun `I see that the cta can't be empty`() {
        assertThat(newQuizPage.ctaValidationText()).contains("Call to action (CTA) can't be empty")
    }

    fun `I see that the image can't be empty`() {
        assertThat(newQuizPage.imageValidationText()).contains("Cover image can't be empty")
    }

    private fun assertCorrectImage(actualUrl: String, expectedFile: String) {
        goTo(actualUrl)
        val actualImage = pageSource()
        val expectedImage = File(expectedFile).readText(charset("ISO-8859-1"))
        assertThat(actualImage).isEqualTo(expectedImage)
    }

    private fun imagePath(image: String): String {
        val file = File("src/test/resources/test_images/$image")
        return file.absolutePath
    }
}

data class QuizView(
        val title: String,
        val image: String,
        val description: String,
        val duration: String,
        val cta: String,
        val ctaUrl: String
)
