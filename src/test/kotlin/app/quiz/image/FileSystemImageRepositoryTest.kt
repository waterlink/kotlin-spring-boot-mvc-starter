package app.quiz.image

import app.quiz.images.FileSystemImageRepository
import app.util.UuidProvider
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File

class FileSystemImageRepositoryTest {

    private val uuidProvider = mock<UuidProvider> {
        on { generateUuid() } doReturn "55aa4f35-0920-4285-9d00-43d284085062"
    }

    private val imageRepository = FileSystemImageRepository(uuidProvider)

    @Before
    fun `before each`() {
        File("./uploads").deleteRecursively()
    }

    @Test
    fun `upload - returns url`() {
        // ARRANGE
        val image = "some image".toByteArray()

        // ACT
        val url = imageRepository.upload(image)

        // ASSERT
        assertThat(url).isEqualTo(
                "/uploads/55/aa4f35-0920-4285-9d00-43d284085062.png"
        )
    }

    @Test
    fun `upload - stores the file`() {
        // ARRANGE
        val image = "some image".toByteArray()

        // ACT
        val url = imageRepository.upload(image)

        // ASSERT
        val file = File(".$url")
        assertThat(file.readBytes()).isEqualTo(image)
    }

}