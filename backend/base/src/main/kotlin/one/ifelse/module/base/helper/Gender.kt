package one.ifelse.module.base.helper

enum class Gender(val code: Int) {
    MALE(1), FEMALE(2), UNKNOWN(0);

    companion object {
        private val valuesAsMap = entries.associateBy { it.code }
        fun of(code: Int?): Gender {
            return valuesAsMap.getOrDefault(code, UNKNOWN)
        }
    }
}