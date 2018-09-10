package app.quiz

import app.auth.AuthService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal
import javax.validation.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import kotlin.reflect.KClass

@Controller
class QuizController(private val quizService: QuizService,
                     private val authService: AuthService) {

    @GetMapping("/quizzes/new")
    fun newQuiz(model: Model): String {
        model.addAttribute("form", QuizForm())
        return "quizzes/new.html"
    }

    @GetMapping("/quizzes")
    fun listQuizzes(model: Model, principal: Principal): String {
        val currentUser = authService.getCurrentUser(principal.name)

        val quizzes = quizService.getMostRecentQuizzesForUser(currentUser)

        model.addAttribute("quizzes", quizzes)
        return "quizzes/list.html"
    }

    @PostMapping("/quizzes")
    fun createQuiz(@Valid @ModelAttribute("form") form: QuizForm,
                   bindingResult: BindingResult,
                   principal: Principal): String {

        if (bindingResult.hasErrors()) {
            return "quizzes/new.html"
        }

        val currentUser = authService.getCurrentUser(principal.name)

        quizService.createQuiz(CreateQuizRequest(
                userId = currentUser.id,
                title = form.title,
                image = form.image!!.bytes,
                description = form.description,
                durationInMinutes = form.duration!!,
                cta = form.cta
        ))

        return "redirect:/quizzes"
    }

    @GetMapping("/quizzes/{id}/edit")
    fun editQuiz(@PathVariable id: Long,
                 principal: Principal,
                 model: Model): String {
        val currentUser = authService.getCurrentUser(principal.name)

        val quiz = quizService.getQuizForEditing(id, currentUser)

        model.addAttribute("form", EditQuizForm(
                id = quiz.id,
                title = quiz.title,
                currentImageUrl = quiz.imageUrl,
                description = quiz.description,
                duration = quiz.durationInMinutes,
                cta = quiz.cta
        ))

        return "quizzes/edit.html"
    }

    @ExceptionHandler(QuizNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleQuizNotFound() = Unit

}

data class QuizForm(
        @field:NotBlank(message = "{validation.not_blank}")
        val title: String = "",

        @field:NotBlank(message = "{validation.not_blank}")
        val description: String = "",

        @field:Min(value = 1, message = "{validation.not_blank}")
        val duration: Int? = -1,

        @field:NotBlank(message = "{validation.not_blank}")
        val cta: String = "",

        @field:UploadNotBlank
        var image: MultipartFile? = null
)

data class EditQuizForm(
        val id: Long? = null,
        val title: String = "",
        val description: String = "",
        val duration: Int? = -1,
        val cta: String = "",
        val currentImageUrl: String = "",
        var image: MultipartFile? = null
)

class UploadNotBlankValidator : ConstraintValidator<UploadNotBlank, MultipartFile?> {
    override fun isValid(value: MultipartFile?, context: ConstraintValidatorContext?): Boolean {
        return value != null && !value.isEmpty
    }
}

@MustBeDocumented
@Constraint(validatedBy = [UploadNotBlankValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UploadNotBlank(
        val message: String = "{validation.not_blank}",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)
