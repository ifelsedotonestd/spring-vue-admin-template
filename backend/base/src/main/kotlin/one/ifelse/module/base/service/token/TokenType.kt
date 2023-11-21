package one.ifelse.module.base.service.token

import com.fasterxml.jackson.annotation.JsonValue

enum class TokenType(@get:JsonValue val code: String, val desc: String) {
    ACCESS_TOKEN("access", ""),
    REFRESH_TOKEN("refresh", "");

    companion object {

        private val valueAsMap = TokenType.values().associateBy { it.code }
        fun of(code: String): TokenType {
            return valueAsMap.getValue(code)
        }
    }
}