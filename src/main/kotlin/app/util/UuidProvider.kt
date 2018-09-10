package app.util

import org.springframework.stereotype.Service
import java.util.*

@Service
class UuidProvider {
    fun generateUuid() = UUID.randomUUID().toString()
}
