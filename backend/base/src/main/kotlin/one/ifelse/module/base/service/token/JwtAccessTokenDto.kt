package one.ifelse.module.base.service.token

import java.util.*

data class JwtAccessTokenDto internal constructor(
    var id: String,
    var issuedAt: Date,
    var expiredAt: Date,
    var token: String
) {
    class JwtAccessTokenDtoBuilder internal constructor() {
        private lateinit var id: String
        private lateinit var issuedAt: Date
        private lateinit var expiredAt: Date
        private lateinit var token: String
        fun id(id: String): JwtAccessTokenDtoBuilder {
            this.id = id
            return this
        }

        fun issuedAt(issuedAt: Date): JwtAccessTokenDtoBuilder {
            this.issuedAt = issuedAt
            return this
        }

        fun expiredAt(expiredAt: Date): JwtAccessTokenDtoBuilder {
            this.expiredAt = expiredAt
            return this
        }

        fun token(token: String): JwtAccessTokenDtoBuilder {
            this.token = token
            return this
        }

        fun build(): JwtAccessTokenDto {
            return JwtAccessTokenDto(id, issuedAt, expiredAt, token)
        }
    }

    companion object {
        fun builder(): JwtAccessTokenDtoBuilder {
            return JwtAccessTokenDtoBuilder()
        }
    }
}
