package one.ifelse.module.base.helper

enum class ReservedUser(
    val id: Long,
    val username: String,
    val email: String,
) {
    ANONYMOUS(0L, "anonymous", "anonymous@localhost"),
    ADMIN(1L, "root", "root@localhost"),
}