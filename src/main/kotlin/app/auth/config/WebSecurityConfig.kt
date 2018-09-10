package app.auth.config

import app.auth.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .mvcMatchers(
                        // Allow static
                        "/css/**",
                        "/js/**",
                        "/img/**",

                        // Allow signup-related pages
                        "/signup",
                        "/thank-you",
                        "/confirm/*",
                        "/resend-confirmation",
                        "/login/**"
                ).permitAll()

                // allow login error page
                .regexMatchers("\\A/login\\?error[^&]+\\Z").permitAll()

                .anyRequest().authenticated()

                .and().formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .failureHandler(
                        SimpleUrlAuthenticationFailureHandler().apply {
                            setUseForward(true)
                            setDefaultFailureUrl("/login?error")
                        }
                )
                .permitAll()

                .and().logout()
                .permitAll()
    }

    @Autowired
    private lateinit var dataSource: DataSource

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(UserRepository.usersByUsernameQuery)
                .authoritiesByUsernameQuery(UserRepository.authoritiesByUsernameQuery)
    }

    @Bean
    fun passwordEncoder(@Value("\${passwordEncoder.strength:13}") strength: Int): PasswordEncoder =
            BCryptPasswordEncoder(strength)

    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

}