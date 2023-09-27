package one.ifelse.module.base.security.jwt

import one.ifelse.module.base.domain.token.AccessToken
import one.ifelse.module.base.domain.token.RefreshToken
import java.util.*

interface JwtTokenStore {
    /**
     * This method used as an indicator to generate token in transaction (for internal use)
     *
     * @param callback function execute real call
     * @return [JwtToken]
     */
    fun generateTokenInTransaction(callback: one.ifelse.module.base.annotation.Executor<JwtToken>): JwtToken
    fun saveAccessToken(id: String, userId: Long, expiration: Date): AccessToken
    fun saveRefreshToken(id: String, accessTokenId: String, userId: Long, expiration: Date): RefreshToken
    fun getUserIdByRefreshTokenId(refreshTokenId: String): Long?
    fun removeTokensByAccessTokenId(accessTokenId: String)
    fun isAccessTokenExisted(accessTokenId: String): Boolean
    fun isRefreshTokenExisted(refreshTokenId: String): Boolean
}
