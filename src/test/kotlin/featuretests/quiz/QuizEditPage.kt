package featuretests.quiz

import helpers.WaitHelper
import org.fluentlenium.assertj.FluentLeniumAssertions
import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.support.FindBy

@PageUrl("/quizzes/{id}/edit")
class QuizEditPage : FluentPage(), WaitHelper {

    @FindBy(css = """[data-qa="title-input"]""")
    private lateinit var titleInput: FluentWebElement

    fun titleValue() = titleInput.value()!!

    fun questionTitles() = find("""[data-qa="question"]""")
            .map { it.attribute("data-qa-title") }

    fun assertNoTitles() {
        awaitAtMostFor(50) {
            FluentLeniumAssertions.assertThatThrownBy { questionTitles() }
                    .isInstanceOf(TimeoutException::class.java)
                    .hasMessageContaining("""[data-qa="question"]""")
        }
    }

}
