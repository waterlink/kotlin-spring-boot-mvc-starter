package helpers

import org.springframework.jdbc.core.JdbcTemplate

fun JdbcTemplate.resetSerial(to: Int = 1) {
    query("select setval('serial', $to, false)") {}
}