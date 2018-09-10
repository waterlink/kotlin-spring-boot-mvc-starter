package featuretests.quiz

import org.fluentlenium.core.FluentPage
import org.fluentlenium.core.annotation.Page
import org.fluentlenium.core.annotation.PageUrl
import org.fluentlenium.core.domain.FluentWebElement
import org.openqa.selenium.support.FindBy

@PageUrl("/quizzes/new")
class NewQuizPage : FluentPage() {

    @Page
    lateinit var afterSubmit: NewQuizPageAfterSubmitPage

    @FindBy(css = """[data-qa="title-input"]""")
    private lateinit var titleInput: FluentWebElement

    @FindBy(css = """[data-qa="title-validation"]""")
    private lateinit var titleValidation: FluentWebElement

    @FindBy(css = """[data-qa="image-input"]""")
    private lateinit var imageInput: FluentWebElement

    @FindBy(css = """[data-qa="image-validation"]""")
    private lateinit var imageValidation: FluentWebElement

    @FindBy(css = """[data-qa="description-input"]""")
    private lateinit var descriptionInput: FluentWebElement

    @FindBy(css = """[data-qa="description-validation"]""")
    private lateinit var descriptionValidation: FluentWebElement

    @FindBy(css = """[data-qa="duration-select"]""")
    private lateinit var durationSelect: FluentWebElement

    @FindBy(css = """[data-qa="duration-validation"]""")
    private lateinit var durationValidation: FluentWebElement

    @FindBy(css = """[data-qa="cta-input"]""")
    private lateinit var ctaInput: FluentWebElement

    @FindBy(css = """[data-qa="cta-validation"]""")
    private lateinit var ctaValidation: FluentWebElement

    @FindBy(css = """[data-qa="submit-button"]""")
    private lateinit var submitButton: FluentWebElement

    fun enterTitle(title: String) {
        titleInput.fill().with(title)
    }

    fun uploadImage(imagePath: String) {
        imageInput.fill().with(imagePath)
    }

    fun enterDescription(description: String) {
        descriptionInput.fill().with(description)
    }

    fun selectDuration(duration: String) {
        durationSelect.fillSelect().withText(duration)
    }

    fun enterCtaText(cta: String) {
        ctaInput.fill().with(cta)
    }

    fun clickOnSubmit() {
        submitButton.click()
    }

    fun titleValidationText() = titleValidation.text()!!
    fun imageValidationText() = imageValidation.text()!!
    fun descriptionValidationText() = descriptionValidation.text()!!
    fun durationValidationText() = durationValidation.text()!!
    fun ctaValidationText() = ctaValidation.text()!!
}

@PageUrl("/quizzes")
class NewQuizPageAfterSubmitPage : FluentPage()