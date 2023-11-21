package one.ifelse.module.base.service.user

import one.ifelse.module.base.security.users.SecuredUser

data class DomainSecuredUser(
    val id: Long,
    val username: String,
    val password: String,
    val preferredUsername: String,
    val enabled: Boolean,
    val locked: Boolean,
    val expired: Boolean,
    val authorityNames: Set<String> = mutableSetOf()
) : SecuredUser {
    override fun id(): Long {
        return this.id
    }

    override fun username(): String {
        return this.username
    }

    override fun preferredUsername(): String {
        return this.preferredUsername
    }

    override fun password(): String {
        return this.password
    }

    override fun enabled(): Boolean {
        return this.enabled
    }

    override fun locked(): Boolean {
        return this.locked
    }

    override fun expired(): Boolean {
        return this.expired
    }

    override fun authorityNames(): Set<String> {
        return this.authorityNames
    }

}
