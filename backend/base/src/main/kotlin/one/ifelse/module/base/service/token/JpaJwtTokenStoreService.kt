package one.ifelse.module.base.service.token

import one.ifelse.module.base.annotation.Executor
import one.ifelse.module.base.security.jwt.JwtToken
import one.ifelse.module.base.security.jwt.JwtTokenStore
import one.ifelse.module.base.service.token.exception.RevokedJwtTokenException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class JpaJwtTokenStoreService(
    private val accessTokenRepo: AccessTokenRepository,
    private val refreshTokenRepo: RefreshTokenRepository
) : JwtTokenStore {

    @Transactional(propagation = Propagation.REQUIRED)
    override fun generateTokenInTransaction(callback: Executor<JwtToken>): JwtToken = callback.run()

    override fun saveAccessToken(id: String, userId: Long, expiration: Date): AccessToken =
        accessTokenRepo.save(
            AccessToken(
                id = id,
                userId = userId,
                expiredAt = expiration.toInstant()
            )
        )

    override fun saveRefreshToken(id: String, accessTokenId: String, userId: Long, expiration: Date) =
        refreshTokenRepo.save(
            RefreshToken(
                id = id,
                accessTokenId = accessTokenId,
                userId = userId,
                expiredAt = expiration.toInstant()
            )
        )

    override fun getUserIdByRefreshTokenId(refreshTokenId: String): Long? =
        refreshTokenRepo.findById(refreshTokenId).map { it.userId }.orElseThrow { RevokedJwtTokenException() }


    override fun removeTokensByAccessTokenId(accessTokenId: String) {
        refreshTokenRepo.deleteRefreshTokenByAccessTokenId(accessTokenId)
        accessTokenRepo.deleteById(accessTokenId)
    }

    override fun isAccessTokenExisted(accessTokenId: String): Boolean = accessTokenRepo.existsById(accessTokenId)

    override fun isRefreshTokenExisted(refreshTokenId: String): Boolean = refreshTokenRepo.existsById(refreshTokenId)
}