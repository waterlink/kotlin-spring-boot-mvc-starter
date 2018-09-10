package app.auth.signup

import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Character.MAX_RADIX
import java.math.BigInteger
import java.security.SecureRandom

@Service
class ConfirmationLinkService {
    private val secureRandom = SecureRandom()

    fun generate(baseUrl: String): ConfirmationLink {
        val code = generateCode()

        val href = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment("confirm")
                .pathSegment(code)
                .build()
                .toUriString()

        return ConfirmationLink(code = code, href = href)
    }

    // This is not easy to test. Right now it is tested that it generates
    // unique value every time only.
    private fun generateCode() =
            BigInteger(256, secureRandom).toString(MAX_RADIX)
}
