package app.quiz

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class QuizRepository(private val jdbcTemplate: JdbcTemplate) {
    fun create(quiz: Quiz) {
        jdbcTemplate.update("""
            insert into
              quizzes (
                user_id,
                title,
                image_url,
                description,
                duration_in_minutes,
                cta,
                created_at,
                updated_at
              )
              values (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent(),
                quiz.userId,
                quiz.title,
                quiz.imageUrl,
                quiz.description,
                quiz.durationInMinutes,
                quiz.cta,
                quiz.createdAt,
                quiz.updatedAt
        )
    }

    fun findAll(): List<Quiz> {
        return jdbcTemplate.query("""
            select * from quizzes
        """.trimIndent(), toQuiz)
    }

    fun findMostRecentByUserId(userId: Long): List<Quiz> {
        return jdbcTemplate.query("""
            select * from quizzes where user_id = ? order by created_at desc
        """.trimIndent(), toQuiz, userId)
    }

    fun findByIdAndUserId(id: Long, userId: Long): Quiz? {
        return jdbcTemplate.query("""
            select * from quizzes where id = ? and user_id = ?
        """.trimIndent(), toQuiz, id, userId)
                .firstOrNull()
    }

    private val toQuiz = RowMapper { rs, _ ->
        Quiz(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                title = rs.getString("title"),
                imageUrl = rs.getString("image_url"),
                description = rs.getString("description"),
                durationInMinutes = rs.getInt("duration_in_minutes"),
                cta = rs.getString("cta"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }
}
