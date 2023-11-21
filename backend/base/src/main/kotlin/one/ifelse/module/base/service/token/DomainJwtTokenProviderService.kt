package one.ifelse.module.base.service.token

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.f4b6a3.ulid.Ulid
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import one.ifelse.module.base.exception.JwtTokenException
import one.ifelse.module.base.exception.ShouldNeverOccurException
import one.ifelse.module.base.helper.ContextHelper
import one.ifelse.module.base.security.jwt.JwtToken
import one.ifelse.module.base.security.jwt.JwtTokenProvider
import one.ifelse.module.base.security.jwt.JwtTokenStore
import one.ifelse.module.base.security.users.DomainUserDetailsService
import one.ifelse.module.base.security.users.SecuredUser
import one.ifelse.module.base.security.users.SecuredUserDetails
import one.ifelse.module.base.service.token.exception.InvalidAudienceTokenException
import one.ifelse.module.base.service.token.exception.RevokedJwtTokenException
import one.ifelse.module.base.service.token.exception.UnknownIssuerTokenException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsChecker
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


@Component
class DomainJwtTokenProviderService(
    protected val authenticationManagerBuilder: AuthenticationManagerBuilder,
    protected val userDetailsService: DomainUserDetailsService,
    protected val tokenStore: JwtTokenStore,
    @Value("\${app.common.settings.jwt.issuer}") private val issuer: String,
    @Value("\${app.common.settings.jwt.access-token-expiration-in-minutes}") private val accessTokenExpirationInMinutes: Long,
    @Value("\${app.common.settings.jwt.refresh-token-expiration-in-minutes}") private val refreshTokenExpirationInMinutes: Long,
    @Value("\${app.common.settings.jwt.remember-me-expiration-in-minutes}") private val rememberMeExpirationInMinutes: Long,
    @Value("\${app.common.settings.jwt.secret-key}") private val jwtSecretKey: String,
) : JwtTokenProvider {

    private lateinit var jwtParser: JwtParser

    private lateinit var secretKey: Key

    @PostConstruct
    fun afterInit() {
        val keyBytes: ByteArray = Decoders.BASE64.decode(jwtSecretKey)
        secretKey = Keys.hmacShaKeyFor(keyBytes)
        jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build()
    }

    override fun generateToken(username: String, password: String, rememberMe: Boolean): JwtToken {
        val authenticationToken: Authentication =
            UsernamePasswordAuthenticationToken.unauthenticated(username, password)
        val authentication: Authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication
        return tokenStore.generateTokenInTransaction { createToken(rememberMe) }
    }

    private fun createToken(rememberMe: Boolean): JwtToken {
        val currentUser: SecuredUser = ContextHelper.currentLoginUser()
            .orElseThrow { ShouldNeverOccurException() }
        val accessToken: JwtAccessTokenDto = createAccessToken(currentUser, rememberMe)
        tokenStore.saveAccessToken(accessToken.id, currentUser.id(), accessToken.expiredAt)
        val refreshToken: JwtRefreshTokenDto = createRefreshToken(accessToken.id, accessToken.issuedAt, rememberMe)
        tokenStore.saveRefreshToken(
            refreshToken.id,
            refreshToken.accessTokenId,
            currentUser.id(),
            refreshToken.expiredAt
        )
        val converter: Calendar = Calendar.getInstance()
        converter.setTime(accessToken.expiredAt)
        val accessTokenExpiredAtInSeconds: Long = converter.getTimeInMillis() / 1000
        converter.setTime(refreshToken.expiredAt)
        val refreshTokenExpiredAtInSeconds: Long = converter.getTimeInMillis() / 1000
        return JwtToken.builder()
            .accessToken(accessToken.token)
            .refreshToken(refreshToken.token)
            .accessTokenExpiredAt(accessTokenExpiredAtInSeconds)
            .refreshTokenExpiredAt(refreshTokenExpiredAtInSeconds)
            .build()
    }

    private fun getExpirationDate(issuedAt: Date, defaultExpiration: Long, rememberMe: Boolean): Date {
        val expiration = if (rememberMe) defaultExpiration + rememberMeExpirationInMinutes else defaultExpiration
        return Date(issuedAt.time + expiration * 1000 * 60)
    }

    private fun createAccessToken(user: SecuredUser, rememberMe: Boolean): JwtAccessTokenDto {
        val id = Ulid.fast().toString()
        val issuedAt = Date()
        val expirationDate = getExpirationDate(issuedAt, accessTokenExpirationInMinutes, rememberMe)
        val accessToken: String = Jwts.builder()
            .setIssuer(issuer)
            .setId(id)
            .setSubject(user.preferredUsername())
            .setIssuedAt(issuedAt)
            .setNotBefore(issuedAt)
            .setExpiration(expirationDate)
            .claim(JwtClaim.AUTHORITY, user.authorityNames())
            .claim(JwtClaim.TYPE, TokenType.ACCESS_TOKEN)
            .signWith(secretKey)
            .compact()
        return JwtAccessTokenDto.builder()
            .id(id)
            .token(accessToken)
            .issuedAt(issuedAt)
            .expiredAt(expirationDate)
            .build()
    }

    private fun createRefreshToken(accessTokenId: String, issuedAt: Date, rememberMe: Boolean): JwtRefreshTokenDto {
        val refreshTokenId = Ulid.fast().toString()
        val expirationDate = getExpirationDate(issuedAt, refreshTokenExpirationInMinutes, rememberMe)
        val refreshToken: String = Jwts.builder()
            .setIssuer(issuer)
            .setId(refreshTokenId)
            .setSubject(accessTokenId)
            .setIssuedAt(issuedAt)
            .setNotBefore(issuedAt)
            .setExpiration(expirationDate)
            .claim(JwtClaim.REMEMBER_ME, rememberMe)
            .claim(JwtClaim.TYPE, TokenType.REFRESH_TOKEN)
            .signWith(secretKey)
            .compact()
        return JwtRefreshTokenDto.builder()
            .id(refreshTokenId)
            .accessTokenId(accessTokenId)
            .token(refreshToken)
            .issuedAt(issuedAt)
            .expiredAt(expirationDate)
            .build()
    }

    override fun renewToken(token: String): JwtToken {
        val claims: Claims = getClaims(jwtParser, token)
        if (claims.issuer != issuer) {
            throw UnknownIssuerTokenException()
        }
        if (claims[JwtClaim.TYPE] != TokenType.REFRESH_TOKEN.code) {
            throw InvalidAudienceTokenException()
        }
        val userId: Long? = tokenStore.getUserIdByRefreshTokenId(claims.id)
        tokenStore.removeTokensByAccessTokenId(claims.subject)
        setAuthenticationAfterSuccess(userId!!)
        return createToken(isPreviousRefreshTokenRememberMe(claims))
    }

    private fun isPreviousRefreshTokenRememberMe(claims: Claims): Boolean {
        return claims.get(JwtClaim.REMEMBER_ME, Boolean::class.java)
    }

    override fun authorizeToken(token: String) {
        val claims: Claims = getClaims(jwtParser, token)
        if (claims[JwtClaim.TYPE] != TokenType.ACCESS_TOKEN.code) {
            throw InvalidAudienceTokenException()
        }
        if (!tokenStore.isAccessTokenExisted(claims.id)) {
            throw RevokedJwtTokenException()
        }
        setAuthenticationAfterSuccess(claims.subject)
    }

    private fun setAuthenticationAfterSuccess(userRef: Any) {
        val userDetails: UserDetails = buildPrincipalForRefreshTokenFromUser(userRef)
        val authentication: Authentication =
            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        val context: SecurityContext = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
    }

    private fun buildPrincipalForRefreshTokenFromUser(userRef: Any): UserDetails {
        val userDetails: SecuredUserDetails = when (userRef) {
            Long -> userDetailsService.loadUserByUserId(userRef as Long)
            Int -> userDetailsService.loadUserByUserId(userRef as Long)
            else -> userDetailsService.loadUserByPreferredUsername(userRef as String)
        }
        postCheckUserStatus.check(userDetails)
        return userDetails
    }

    override fun isSelfIssuer(token: String): Boolean {
        return try {
            val claims = getClaimsWithoutKey(token)
            claims[Claims.ISSUER] == issuer
        } catch (e: Exception) {
            log.error("Cannot parse payload in token", e)
            false
        }
    }

    override fun revokeToken(token: String) {
        val claims: Claims = getClaims(jwtParser, token)
        tokenStore.removeTokensByAccessTokenId(claims.id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)

        private val JACKSON_MAPPER: ObjectMapper = objectMapper()

        private val postCheckUserStatus: UserDetailsChecker = AccountStatusUserDetailsChecker()

        private fun objectMapper(): ObjectMapper {
            val objectMapper = ObjectMapper()
            objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS)
            return objectMapper
        }

        private fun getClaims(jwtParser: JwtParser, jwt: String): Claims {
            return try {
                jwtParser.parseClaimsJws(jwt).body
            } catch (e: ExpiredJwtException) {
                log.trace("Invalid JWT jwt", e)
                throw JwtTokenException()
            } catch (e: MalformedJwtException) {
                log.trace("Invalid JWT jwt", e)
                throw JwtTokenException()
            } catch (e: UnsupportedJwtException) {
                log.trace("Invalid JWT jwt", e)
                throw JwtTokenException()
            } catch (e: SignatureException) {
                log.trace("Invalid JWT jwt", e)
                throw JwtTokenException()
            } catch (e: PrematureJwtException) {
                log.trace("Invalid JWT jwt", e)
                throw JwtTokenException()
            } catch (e: IllegalArgumentException) {
                log.trace("Invalid JWT jwt", e)
                throw JwtTokenException()
            }
        }

        private fun isJwtTokenValid(jwtParser: JwtParser, jwt: String): Boolean {
            return try {
                jwtParser.parseClaimsJws(jwt)
                true
            } catch (e: ExpiredJwtException) {
                false
            } catch (e: MalformedJwtException) {
                false
            } catch (e: UnsupportedJwtException) {
                false
            } catch (e: SignatureException) {
                false
            } catch (e: PrematureJwtException) {
                false
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        private fun getClaimsWithoutKey(jwt: String): Map<String, Any> {
            return try {
                val decoder = Base64.getUrlDecoder()
                val payload =
                    decoder.decode(jwt.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                JACKSON_MAPPER.readValue(payload, object : TypeReference<Map<String, Any>>() {})
            } catch (e: Exception) {
                log.error("Cannot parse payload in token", e)
                throw JwtTokenException()
            }
        }
    }
}
