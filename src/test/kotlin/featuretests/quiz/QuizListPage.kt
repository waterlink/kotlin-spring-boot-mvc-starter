package featuretests.quiz

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

@PageUrl("/quizzes")
class QuizListPage : FluentPage() {

    @FindBy(css = """[data-qa="new-quiz"]""")
    private lateinit var newQuiz: FluentWebElement

    fun clickOnNewQuiz() {
        newQuiz.click()
    }

    fun findQuizBy(title: String) =
            el("""[data-qa="quiz"][data-qa-title="$title"]""").run {
                QuizView(
                        title = el("""[data-qa="quiz-title"]""").text(),
                        image = el("""[data-qa="quiz-image"]""").attribute("src"),
                        description = el("""[data-qa="quiz-description"]""").text(),
                        duration = el("""[data-qa="quiz-duration"]""").text(),
                        cta = el("""[data-qa="quiz-cta"]""").text(),
                        ctaUrl = el("""[data-qa="quiz-cta"]""").attribute("href")
                )
            }

    fun quizTitles() = find("""[data-qa="quiz"]""")
            .map { it.attribute("data-qa-title") }

    fun clickOnEditQuiz(title: String) {
        el("""[data-qa="quiz"][data-qa-title="$title"]""")
                .el("""[data-qa="edit-button"]""")
                .click()
    }
}
