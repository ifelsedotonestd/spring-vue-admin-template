package one.ifelse.module.base.security.users

import org.springframework.security.core.userdetails.UserDetailsService

interface DomainUserDetailsService : UserDetailsService {
    fun loadUserByUserId(userId: Long): SecuredUserDetails
    fun loadUserByPreferredUsername(preferredUsername: String): SecuredUserDetails
}
