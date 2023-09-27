package one.ifelse.module.base.security

import one.ifelse.module.base.security.jwt.DefaultJwtAuthenticationFilter
import one.ifelse.module.base.security.jwt.JwtTokenProvider
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class DefaultSecurityProvider(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationEntryPoint: RestAuthenticationEntryPoint
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain?, HttpSecurity>() {
    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(
            DefaultJwtAuthenticationFilter(jwtTokenProvider, authenticationEntryPoint),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }
}
