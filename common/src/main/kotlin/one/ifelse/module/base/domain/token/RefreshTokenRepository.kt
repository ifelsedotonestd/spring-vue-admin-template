package one.ifelse.module.base.domain.token

import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun deleteRefreshTokenByAccessTokenId(accessTokenId: String)
}