package one.ifelse.module.base.security.jwt

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JwtToken(
    val type: Type = Type.BEARER,
    val accessToken: String,
    val accessTokenExpiredAt: Long,
    val refreshToken: String?,
    val refreshTokenExpiredAt: Long?,
) {

    override fun toString(): String {
        val var10000 = type
        return "JwtAccessToken(type=$var10000, accessToken=$accessToken, accessTokenExpiredAt=$accessTokenExpiredAt, refreshToken=$refreshToken, refreshTokenExpiredAt=$refreshTokenExpiredAt)"
    }

    enum class Type(@get:JsonValue val value: String) {
        BEARER("bearer")

    }

    class JwtAccessTokenBuilder {
        private var type: Type = Type.BEARER
        private lateinit var accessToken: String
        private var accessTokenExpiredAt: Long = 0
        private var refreshToken: String? = null
        private var refreshTokenExpiredAt: Long? = null

        fun type(type: Type): JwtAccessTokenBuilder {
            this.type = type
            return this
        }

        fun accessToken(accessToken: String): JwtAccessTokenBuilder {
            this.accessToken = accessToken
            return this
        }

        fun accessTokenExpiredAt(accessTokenExpiredAt: Long): JwtAccessTokenBuilder {
            this.accessTokenExpiredAt = accessTokenExpiredAt
            return this
        }

        fun refreshToken(refreshToken: String?): JwtAccessTokenBuilder {
            this.refreshToken = refreshToken
            return this
        }

        fun refreshTokenExpiredAt(refreshTokenExpiredAt: Long?): JwtAccessTokenBuilder {
            this.refreshTokenExpiredAt = refreshTokenExpiredAt
            return this
        }

        fun build(): JwtToken {
            return JwtToken(type, accessToken, accessTokenExpiredAt, refreshToken, refreshTokenExpiredAt)
        }
    }

    companion object {
        fun builder(): JwtAccessTokenBuilder {
            return JwtAccessTokenBuilder()
        }
    }
}
