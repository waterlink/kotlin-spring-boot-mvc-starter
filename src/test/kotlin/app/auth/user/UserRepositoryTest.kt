package app.auth.user

import app.auth.user.standardUsers.john
import app.auth.user.standardUsers.kate
import app.auth.user.standardUsers.nonConfirmedUser
import helpers.RepositoryTest
import helpers.resetSerial
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.dao.DuplicateKeyException

class UserRepositoryTest : RepositoryTest() {

    private lateinit var userRepository: UserRepository

    @Before
    fun `before each`() {
        jdbcTemplate.update("delete from quizzes cascade")
        jdbcTemplate.update("delete from users cascade")
        jdbcTemplate.resetSerial()

        userRepository = UserRepository(jdbcTemplate, passwordEncoder)
    }

    @Test
    fun `findAll - returns empty when there are no users`() {
        // ACT
        val users = userRepository.findAll()

        // ASSERT
        assertThat(users).isEmpty()
    }

    @Test
    fun `create - creates a user and findAll finds it`() {
        // ACT
        userRepository.create(kate)

        // ASSERT
        val users = userRepository.findAll()
        assertThat(users).isEqualTo(listOf(
                kate.copy(id = 1).withEncodedPassword()
        ))
    }

    @Test
    fun `create - creates multiple users`() {
        // ACT
        userRepository.create(john)
        userRepository.create(kate)

        // ASSERT
        val users = userRepository.findAll().sortedBy { it.id }
        assertThat(users).isEqualTo(listOf(
                john.copy(id = 1).withEncodedPassword(),
                kate.copy(id = 2).withEncodedPassword()
        ))
    }

    // ASSERT
    @Test(expected = DuplicateKeyException::class)
    fun `create - no duplicate emails`() {
        // ARRANGE
        userRepository.create(john.copy(email = "duplicate@example.org"))

        // ACT
        userRepository.create(kate.copy(email = "duplicate@example.org"))
    }

    // ASSERT
    @Test(expected = DuplicateKeyException::class)
    fun `create - no duplicate confirmation codes`() {
        // ARRANGE
        userRepository.create(john.copy(confirmationCode = "duplicate"))

        // ACT
        userRepository.create(kate.copy(confirmationCode = "duplicate"))
    }

    @Test
    fun `confirm - updates confirmed to true`() {
        // ARRANGE
        userRepository.create(john.copy(confirmed = false))
        userRepository.create(kate.copy(confirmed = false))

        // ACT
        userRepository.confirm(john.copy(id = 1))

        // ASSERT
        val users = userRepository.findAll().sortedBy { it.id }
        assertThat(users).isEqualTo(listOf(
                john.copy(id = 1, confirmed = true).withEncodedPassword(),
                kate.copy(id = 2, confirmed = false).withEncodedPassword()
        ))
    }

    @Test
    fun `confirm - updates other user's confirmed to true`() {
        // ARRANGE
        userRepository.create(john.copy(confirmed = false))
        userRepository.create(kate.copy(confirmed = false))

        // ACT
        userRepository.confirm(kate.copy(id = 2))

        // ASSERT
        val users = userRepository.findAll().sortedBy { it.id }
        assertThat(users).isEqualTo(listOf(
                john.copy(id = 1, confirmed = false).withEncodedPassword(),
                kate.copy(id = 2, confirmed = true).withEncodedPassword()
        ))
    }

    @Test
    fun `updateConfirmationCode - updates confirmation code`() {
        // ARRANGE
        userRepository.create(john.copy(confirmationCode = "john-old-code"))
        userRepository.create(kate.copy(confirmationCode = "kate-old-code"))

        listOf("new-code", "other-code").forEach { newCode ->
            // ACT
            userRepository.updateConfirmationCode(john.copy(id = 1, confirmationCode = newCode))

            // ASSERT
            val users = userRepository.findAll().sortedBy { it.id }
            assertThat(users).isEqualTo(listOf(
                    john.copy(id = 1, confirmationCode = newCode).withEncodedPassword(),
                    kate.copy(id = 2, confirmationCode = "kate-old-code").withEncodedPassword()
            ))
        }
    }

    @Test
    fun `updateConfirmationCode - updates confirmation code for other user`() {
        // ARRANGE
        userRepository.create(john.copy(confirmationCode = "john-old-code"))
        userRepository.create(kate.copy(confirmationCode = "kate-old-code"))
        val newCode = "new-code"

        // ACT
        userRepository.updateConfirmationCode(kate.copy(id = 2, confirmationCode = newCode))

        // ASSERT
        val users = userRepository.findAll().sortedBy { it.id }
        assertThat(users).isEqualTo(listOf(
                john.copy(id = 1, confirmationCode = "john-old-code").withEncodedPassword(),
                kate.copy(id = 2, confirmationCode = newCode).withEncodedPassword()
        ))
    }


    @Test
    fun `findByConfirmationCode - finds by confirmation code`() {
        // ARRANGE
        userRepository.create(john)
        userRepository.create(kate)

        // ACT & ASSERT
        assertThat(userRepository.findByConfirmationCode(john.confirmationCode!!))
                .isEqualTo(john.copy(id = 1).withEncodedPassword())

        assertThat(userRepository.findByConfirmationCode(kate.confirmationCode!!))
                .isEqualTo(kate.copy(id = 2).withEncodedPassword())
    }

    @Test
    fun `findByConfirmationCode - returns null when code is invalid`() {
        // ARRANGE
        val code = "invalid-code"

        // ACT
        val actual = userRepository.findByConfirmationCode(code)

        // ASSERT
        assertThat(actual).isNull()
    }

    @Test
    fun `findByEmail - finds user by email`() {
        // ARRANGE
        userRepository.create(john)
        userRepository.create(kate)

        // ACT
        val user = userRepository.findByEmail(john.email)

        // ASSERT
        assertThat(user).isEqualTo(john.copy(id = 1).withEncodedPassword())
    }

    @Test
    fun `findByEmail - finds user by different email`() {
        // ARRANGE
        userRepository.create(john)
        userRepository.create(kate)

        // ACT
        val user = userRepository.findByEmail(kate.email)

        // ASSERT
        assertThat(user).isEqualTo(kate.copy(id = 2).withEncodedPassword())
    }

    @Test
    fun `findByEmail - returns null when can't find the user`() {
        // ARRANGE
        userRepository.create(john)
        userRepository.create(kate)

        // ACT
        val user = userRepository.findByEmail("missing@example.org")

        // ASSERT
        assertThat(user).isNull()
    }

    @Test
    fun `usersByUsernameQuery - finds single user by username - for spring security integration`() {
        // ARRANGE
        userRepository.create(john)
        userRepository.create(kate)
        userRepository.create(nonConfirmedUser)

        standardUsers.allIncludingNonConfirmed.forEach { user ->
            // ACT
            val users = jdbcTemplate.queryForList(UserRepository.usersByUsernameQuery, user.email)

            // ASSERT
            assertThat(users).hasSize(1)
            assertThat(users[0]).isEqualTo(mapOf(
                    "username" to user.email,
                    "password" to user.password.encoded(),
                    "enabled" to user.confirmed
            ))
        }
    }

    @Test
    fun `authoritiesByUsernameQuery - always returns 'user' authority for now - for spring security integration`() {
        // ARRANGE
        userRepository.create(john)
        userRepository.create(kate)

        standardUsers.all.forEach {
            // ACT
            val authorities = jdbcTemplate.queryForList(UserRepository.authoritiesByUsernameQuery, it.email)

            // ASSERT
            assertThat(authorities).hasSize(1)
            assertThat(authorities[0]["username"]).isEqualTo(it.email)
            assertThat(authorities[0]["role"]).isEqualTo("user")
        }
    }

    @Test
    fun `passwordEncoder's encode - encodes password simplistically`() {
        listOf("password", "different").forEach {
            // ACT
            val encoded = passwordEncoder.encode(it)

            // ASSERT
            assertThat(encoded).isEqualTo("<encoded>$it</encoded>")
        }
    }

    private fun User.withEncodedPassword() = copy(
            password = password.encoded(),
            isPasswordEncoded = true
    )

    private fun String.encoded() =
            passwordEncoder.encode(this)

}
