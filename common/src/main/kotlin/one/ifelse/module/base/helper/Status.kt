package one.ifelse.module.base.helper

enum class Status(val code: Int) {
    ACTIVATED(1), PENDING(0), DELETED(9);

    companion object {
        private val valuesAsMap = Status.values().associateBy { it.code }
        fun of(code: Int): Status {
            return valuesAsMap.getOrDefault(code, PENDING)
        }
    }
}