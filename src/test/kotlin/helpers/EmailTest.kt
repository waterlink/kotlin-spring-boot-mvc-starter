package helpers

import app.email.EmailService
import app.email.EmailTemplate
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@SpringBootTest(classes = [EmailTemplate::class, ThymeleafAutoConfiguration::class])
abstract class EmailTest {

    @Autowired
    protected lateinit var emailTemplate: EmailTemplate

    @MockBean
    protected lateinit var emailService: EmailService

}