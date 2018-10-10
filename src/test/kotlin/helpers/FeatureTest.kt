package helpers

import app.Application
import app.auth.AuthService
import app.email.TestEmailService
import app.quiz.QuizService
import featuretests.auth.DashboardPage
import featuretests.auth.ConfirmationPage
import featuretests.auth.LoginPage
import featuretests.auth.SignupPage
import featuretests.auth.ThankYouPage
import featuretests.quiz.NewQuizPage
import featuretests.quiz.QuizEditPage
import featuretests.quiz.QuizListPage
import org.fluentlenium.adapter.junit.FluentTest
import org.fluentlenium.configuration.FluentConfiguration
import org.fluentlenium.core.annotation.Page
import org.fluentlenium.core.hook.wait.Wait
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
@ActiveProfiles("test")
@FluentConfiguration(webDriver = "htmlunit")
@Wait
abstract class FeatureTest : FluentTest() {
    @LocalServerPort
    lateinit var serverPort: String

    override fun getBaseUrl() = "http://localhost:$serverPort"

    @Page
    lateinit var loginPage: LoginPage

    @Page
    lateinit var signupPage: SignupPage

    @Page
    lateinit var thankYouPage: ThankYouPage

    @Page
    lateinit var dashboardPage: DashboardPage

    @Page
    lateinit var confirmationPage: ConfirmationPage

    @Page
    lateinit var quizListPage: QuizListPage

    @Page
    lateinit var newQuizPage: NewQuizPage

    @Page
    lateinit var quizEditPage: QuizEditPage

    @Autowired
    lateinit var authService: AuthService

    @Autowired
    lateinit var emailService: TestEmailService

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Suppress("TestFunctionName")
    protected fun Given(step: Unit) = Unit

    @Suppress("TestFunctionName")
    protected fun When(step: Unit) = Unit

    @Suppress("TestFunctionName")
    protected fun Then(step: Unit) = Unit

    @Suppress("TestFunctionName")
    protected fun And(step: Unit) = Unit

    @Before
    fun clearDatabase() {
        jdbcTemplate.update("delete from quizzes")
        jdbcTemplate.update("delete from users")
        jdbcTemplate.resetSerial()
    }
}