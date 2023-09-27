package one.ifelse.module.base.configurer

import one.ifelse.module.base.helper.ReservedAuthority
import one.ifelse.module.base.security.RestAuthenticationEntryPoint
import one.ifelse.module.base.security.jwt.JwtSecurityAdapter
import one.ifelse.module.base.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class DefaultWebSecurityConfigurer(
    @Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver,
    @Value("\${app.common.settings.cors-allowed-hosts:*}") private val corsAllowedHosts: String,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @Bean
    @Throws(Exception::class)
    fun web(http: HttpSecurity): SecurityFilterChain {
        http.cors().and().csrf().disable()
            .headers()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
            .permissionsPolicy()
            .policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
            .and()
            .frameOptions()
            .sameOrigin()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)


        http.authorizeHttpRequests {
            it.requestMatchers(*IGNORED_API.map { url -> AntPathRequestMatcher(url) }.toTypedArray()).permitAll()
                .requestMatchers("/actuator").hasAuthority(ReservedAuthority.ADMIN.code)
                .anyRequest().authenticated()
        }

        val authenticationEntryPoint = RestAuthenticationEntryPoint(resolver)
        http.formLogin().disable()
            .logout().disable()
            .httpBasic().disable()
            .apply(JwtSecurityAdapter(jwtTokenProvider, authenticationEntryPoint))
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)

        return http.build()
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.addAllowedOriginPattern(corsAllowedHosts)
        config.addAllowedMethod("*")
        config.addAllowedHeader("*")
        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    companion object {
        private val IGNORED_API = arrayOf(
            "/webjars/**", "/error/**",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/pub/**",
            "/**/pub/**"
        )
    }
}
