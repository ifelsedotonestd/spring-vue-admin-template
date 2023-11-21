package one.ifelse.module.base.service.token

import java.io.Serializable
import java.util.*

data class JwtRefreshTokenDto internal constructor(
    var id: String,
    var accessTokenId: String,
    var issuedAt: Date,
    var expiredAt: Date,
    var token: String
) : Serializable {
    class JwtRefreshTokenDtoBuilder internal constructor() {
        private lateinit var id: String
        private lateinit var accessTokenId: String
        private lateinit var issuedAt: Date
        private lateinit var expiredAt: Date
        private lateinit var token: String
        fun id(id: String): JwtRefreshTokenDtoBuilder {
            this.id = id
            return this
        }

        fun accessTokenId(accessTokenId: String): JwtRefreshTokenDtoBuilder {
            this.accessTokenId = accessTokenId
            return this
        }

        fun issuedAt(issuedAt: Date): JwtRefreshTokenDtoBuilder {
            this.issuedAt = issuedAt
            return this
        }

        fun expiredAt(expiredAt: Date): JwtRefreshTokenDtoBuilder {
            this.expiredAt = expiredAt
            return this
        }

        fun token(token: String): JwtRefreshTokenDtoBuilder {
            this.token = token
            return this
        }

        fun build(): JwtRefreshTokenDto {
            return JwtRefreshTokenDto(id, accessTokenId, issuedAt, expiredAt, token)
        }
    }

    companion object {
        fun builder(): JwtRefreshTokenDtoBuilder {
            return JwtRefreshTokenDtoBuilder()
        }
    }
}
