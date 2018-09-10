package app.auth.signup

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ConfirmationLinkServiceTest {

    private val service = ConfirmationLinkService()

    @Test
    fun `generate - generates unique links`() {
        // ARRANGE
        val baseUrl = "http://localhost"

        // ACT
        val one = service.generate(baseUrl)
        val two = service.generate(baseUrl)

        // ASSERT
        assertThat(one.code).isNotEqualTo(two.code)
        assertThat(one.href).contains(one.code)
        assertThat(two.href).contains(two.code)
    }

    // ARRANGE
    private val baseUrls = listOf("http://localhost:8080", "https://example.org/context")

    @Test
    fun `generate - generates link prefixed with confirm path`() = baseUrls.forEach { baseUrl ->
        // ACT
        val link = service.generate(baseUrl)

        // ASSERT
        assertThat(link.href).startsWith("$baseUrl/confirm/")
    }
}