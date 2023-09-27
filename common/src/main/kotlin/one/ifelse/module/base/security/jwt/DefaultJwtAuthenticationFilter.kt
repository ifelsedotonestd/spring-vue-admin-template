package one.ifelse.module.base.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import one.ifelse.module.base.helper.RequestedAttribute
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import java.util.*

class DefaultJwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        doFilterInternal(request, response, chain)
    }


    @Throws(ServletException::class, IOException::class)
    private fun doFilterInternal(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        this.logger.debug("Start default app authentication flow")
        val httpServletRequest: HttpServletRequest = request as HttpServletRequest
        val jwt = resolveToken(httpServletRequest)
        if (StringUtils.hasText(jwt) && jwt?.let { jwtTokenProvider.isSelfIssuer(it) } == true) {
            try {
                request.setAttribute(RequestedAttribute.BEARER_TOKEN_VALUE, jwt)

                // Authenticate with default Jwt-Filter
                jwtTokenProvider.authorizeToken(jwt)

                // Hide token to skip authentication in default Oauth2-Filter
                chain.doFilter(HiddenTokenRequestWrapper(request), response)
            } catch (e: AuthenticationException) {
                SecurityContextHolder.clearContext()
                this.logger.trace("Failed to process authentication request", e)
                authenticationEntryPoint.commence(request, response as HttpServletResponse, e)
            }
        } else {
            chain.doFilter(request, response)
        }
        this.logger.debug("Finished default app authentication flow")
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val value: String? = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(value) && value!!.startsWith(BEARER_TOKEN_PREFIX)) value.substring(7) else null
    }

    internal class HiddenTokenRequestWrapper(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {
        override fun getHeader(name: String): String? =
            if (name == AUTHORIZATION_HEADER) null else super.getHeader(name)

        override fun getHeaders(name: String): Enumeration<String>? =
            if (name == AUTHORIZATION_HEADER) null else super.getHeaders(name)

    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_TOKEN_PREFIX = "Bearer "
    }
}
