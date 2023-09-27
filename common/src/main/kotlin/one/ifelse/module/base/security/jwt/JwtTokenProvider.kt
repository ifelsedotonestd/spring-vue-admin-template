package one.ifelse.module.base.security.jwt

interface JwtTokenProvider {
    fun generateToken(username: String, password: String, rememberMe: Boolean): JwtToken

    fun renewToken(token: String): JwtToken

    fun authorizeToken(token: String)

    /**
     * Privately use to distinguish between oauth2 token and self-issued jwt token
     *
     * @param token
     * @return
     */
    fun isSelfIssuer(token: String): Boolean

    fun revokeToken(token: String)
}
