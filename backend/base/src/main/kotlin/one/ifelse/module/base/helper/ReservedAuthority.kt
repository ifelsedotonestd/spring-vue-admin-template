package one.ifelse.module.base.helper

enum class ReservedAuthority(val code: String, val desc: String) {
    ADMIN("base.admin", "Admin Privilege"),
    USER("base.user", "User Privilege"),
    DEPUTY("base.deputy", "Deputy Privilege");
}