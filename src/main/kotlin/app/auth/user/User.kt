package app.auth.user

import java.time.LocalDateTime

data class User(val id: Long? = null,
                val confirmed: Boolean = false,
                val confirmationCode: String? = null,
                val email: String,
                val password: String,
                val isPasswordEncoded: Boolean = false,
                val name: String,
                val createdAt: LocalDateTime,
                val updatedAt: LocalDateTime) {

    companion object

}
