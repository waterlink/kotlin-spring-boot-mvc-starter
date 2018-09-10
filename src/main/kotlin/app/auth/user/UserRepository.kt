package app.auth.user

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate,
                     private val passwordEncoder: PasswordEncoder) {

    companion object {
        val usersByUsernameQuery = """
            select email as username, password, confirmed as enabled
            from users where email = ?
        """.trimIndent()

        val authoritiesByUsernameQuery = """
            select (select ?) as username, (select 'user') as role
        """.trimIndent()
    }

    fun create(user: User) {
        jdbcTemplate.update("""
            insert into
              users(
                email,
                password,
                name,
                confirmed,
                confirmation_code,
                created_at,
                updated_at
              )
              values(?, ?, ?, ?, ?, ?, ?)
        """.trimIndent(),
                user.email,
                passwordEncoder.encode(user.password),
                user.name,
                user.confirmed,
                user.confirmationCode,
                user.createdAt,
                user.updatedAt
        )
    }

    fun findAll(): List<User> =
            jdbcTemplate.query("""
                select * from users
            """.trimIndent(), rowMapper)

    fun findByEmail(email: String): User? =
            jdbcTemplate.query("""
                select * from users where email = ?
            """.trimIndent(), rowMapper, email)
                    .firstOrNull()

    fun findByConfirmationCode(confirmationCode: String): User? =
            jdbcTemplate.query("""
                select * from users where confirmation_code = ?
            """.trimIndent(), rowMapper, confirmationCode)
                    .firstOrNull()

    fun confirm(user: User) {
        jdbcTemplate.update("""
            update users set confirmed = true where id = ?
        """.trimIndent(), user.id)
    }

    fun updateConfirmationCode(user: User) {
        jdbcTemplate.update("""
            update users set confirmation_code = ? where id = ?
        """.trimIndent(), user.confirmationCode, user.id)
    }

    private val rowMapper: RowMapper<User> = RowMapper { rs, _ ->
        User(
                id = rs.getLong("id"),
                email = rs.getString("email"),
                password = rs.getString("password"),
                isPasswordEncoded = true,
                name = rs.getString("name"),
                confirmed = rs.getBoolean("confirmed"),
                confirmationCode = rs.getString("confirmation_code"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }
}
