package app.auth.user

import helpers.today
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

internal fun User.Companion.create(name: String = "irrelevant",
                                   password: String = "irrelevant",
                                   email: String = "irrelevant",
                                   confirmed: Boolean = true,
                                   confirmationCode: String = "irrelevant",
                                   createdAt: LocalDateTime = today,
                                   updatedAt: LocalDateTime = today) =
        User(
                name = name,
                password = password,
                email = email,
                confirmed = confirmed,
                confirmationCode = confirmationCode,
                createdAt = createdAt,
                updatedAt = updatedAt
        )

internal val defaultAuthorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

internal fun User.asSpringSecurityUser(authorities: List<GrantedAuthority> = defaultAuthorities) =
        org.springframework.security.core.userdetails.User(
                email, password, authorities
        )

internal fun User.asSpringSecurityToken(authorities: List<GrantedAuthority> = defaultAuthorities) =
        UsernamePasswordAuthenticationToken(
                email, password, authorities
        )

@Suppress("ClassName")
internal object standardUsers {
    val kate = User.create(
            name = "Kate",
            email = "kate@example.org",
            password = "katewelcome",
            confirmationCode = "kate-confirmation-code"
    )

    val john = User.create(
            name = "John",
            email = "john@example.org",
            password = "welcomejohn",
            confirmationCode = "john-confirmation-code"
    )

    val nonConfirmedUser = User.create(
            confirmed = false
    )

    val all = listOf(kate, john)
    val allIncludingNonConfirmed = listOf(kate, john, nonConfirmedUser)
}
