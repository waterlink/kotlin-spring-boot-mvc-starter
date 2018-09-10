package app.auth.user

import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import helpers.component1
import org.mockito.ArgumentMatchers
import org.springframework.security.crypto.password.PasswordEncoder

val passwordEncoder = mock<PasswordEncoder> {
    on { encode(ArgumentMatchers.anyString()) } doAnswer { (rawPassword) ->
        "<encoded>$rawPassword</encoded>"
    }
}