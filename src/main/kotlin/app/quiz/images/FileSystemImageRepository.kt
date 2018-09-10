package app.quiz.images

import app.util.UuidProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Profile("dev", "test")
@Service
class FileSystemImageRepository(
        private val uuidProvider: UuidProvider) : ImageRepository {

    override fun upload(image: ByteArray): String {
        val uuid = uuidProvider.generateUuid()

        val prefix = uuid.substring(0..1)
        val context = "uploads/$prefix"

        val name = uuid.substring(2)
        val extension = "png"

        Files.createDirectories(Paths.get("./$context"))

        val fileName = "$name.$extension"
        val path = "./$context/$fileName"
        File(path).writeBytes(image)

        return "/$context/$fileName"
    }

}