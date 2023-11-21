package one.ifelse.module.base.security.users

import one.ifelse.module.base.helper.ReservedUser
import java.io.Serializable

interface SecuredUser : Serializable {
    fun id(): Long
    fun username(): String
    fun preferredUsername(): String
    fun password(): String
    fun enabled(): Boolean
    fun locked(): Boolean
    fun expired(): Boolean
    fun authorityNames(): Set<String>

    companion object {
        val ANONYMOUS_USER: SecuredUser = object : SecuredUser {
            override fun id(): Long {
                return ReservedUser.ANONYMOUS.id
            }

            override fun username(): String {
                return ReservedUser.ANONYMOUS.username
            }

            override fun preferredUsername(): String {
                return ""
            }

            override fun password(): String {
                return ""
            }

            override fun enabled(): Boolean {
                return true
            }

            override fun locked(): Boolean {
                return true
            }

            override fun expired(): Boolean {
                return false
            }

            override fun authorityNames(): Set<String> {
                return setOf()
            }
        }
    }
}
