package one.ifelse.module.base.service.user

import one.ifelse.module.base.security.users.DomainUserDetailsService
import one.ifelse.module.base.security.users.SecuredUser
import one.ifelse.module.base.security.users.SecuredUserDetails
import one.ifelse.module.base.service.user.exception.UserNotExistedException
import one.ifelse.module.base.service.user.exception.UserNotVerifyException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class JpaDomainUserDetailsService(private val userRepository: UserRepository) : DomainUserDetailsService {
    override fun loadUserByUserId(userId: Long): SecuredUserDetails {
        val user = userRepository.findById(userId).orElseThrow { UserNotExistedException() }
        return convertUserEntityToSecureUserDetails(user)
    }

    private fun convertUserEntityToSecureUserDetails(user: User): SecuredUserDetails {
        postCheck(user)
        val securedUser: SecuredUser = DomainSecuredUser(
            id = user.id!!,
            username = user.username!!,
            preferredUsername = user.preferredUsername!!,
            enabled = user.enabled!!,
            locked = user.locked!!,
            expired = user.expired!!,
            password = user.password!!,
            authorityNames = user.authorities?.map { it.code!! }!!.toMutableSet()
        )
        return SecuredUserDetails.instance(securedUser)
    }

    private fun postCheck(user: User) {
        if (user.emailVerified == false) {
            throw UserNotVerifyException()
        }
    }

    override fun loadUserByPreferredUsername(preferredUsername: String): SecuredUserDetails {
        val user = userRepository.findByPreferredUsername(preferredUsername).orElseThrow { UserNotExistedException() }
        return convertUserEntityToSecureUserDetails(user)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username).orElseThrow { UserNotExistedException() }
        return convertUserEntityToSecureUserDetails(user)
    }
}