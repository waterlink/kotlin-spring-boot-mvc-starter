package app.quiz.images

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.io.File
import java.io.InputStream
import javax.servlet.http.HttpServletResponse

@Profile("dev", "test")
@Controller
class ImageController {

    @GetMapping("/uploads/{prefix}/{image}")
    fun downloadImage(@PathVariable prefix: String,
                      @PathVariable image: String,
                      response: HttpServletResponse) {

        val input: InputStream = File("./uploads/$prefix/$image").inputStream()

        response.addHeader(CONTENT_DISPOSITION, "attachment;filename=$image")
        response.contentType = "image/*"

        IOUtils.copy(input, response.outputStream)
        response.flushBuffer()
    }

}