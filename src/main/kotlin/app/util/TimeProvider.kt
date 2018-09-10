package app.util

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TimeProvider {
    fun now() = LocalDateTime.now()
}
