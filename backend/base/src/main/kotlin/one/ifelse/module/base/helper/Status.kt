package one.ifelse.module.base.helper

import com.fasterxml.jackson.annotation.JsonValue

enum class Status(@get:JsonValue val code: Int) {
    ACTIVE(1), INACTIVATE(0), DELETED(9);

    companion object {
        private val valuesAsMap = entries.associateBy { it.code }
        fun of(code: Int?): Status {
            return valuesAsMap.getOrDefault(code, INACTIVATE)
        }
    }
}