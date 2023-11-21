package one.ifelse.module.base.security.users

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SecuredUserDetails private constructor(private val user: SecuredUser) : UserDetails {
    fun user(): SecuredUser {
        return user
    }

    companion object {
        fun instance(user: SecuredUser): SecuredUserDetails {
            return SecuredUserDetails(user)
        }
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return user.authorityNames()
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .toList()
    }

    override fun getPassword(): String {
        return user.password()
    }

    override fun getUsername(): String {
        return user.username()
    }

    override fun isAccountNonExpired(): Boolean {
        return !user.expired()
    }

    override fun isAccountNonLocked(): Boolean {
        return !user.locked()
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return user.enabled()
    }
}
