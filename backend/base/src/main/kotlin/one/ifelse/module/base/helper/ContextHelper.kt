package one.ifelse.module.base.helper

import one.ifelse.module.base.security.users.SecuredUser
import one.ifelse.module.base.security.users.SecuredUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class ContextHelper {

    @Autowired
    fun setApplicationContext(applicationContext: ApplicationContext) {
        ContextHelper.applicationContext = applicationContext
    }

    companion object {
        private lateinit var applicationContext: ApplicationContext
        fun <T> getBean(clazz: Class<T>): T {
            return applicationContext.getBean(clazz)
        }

        fun currentLoginUser(): Optional<SecuredUser> {
            val authentication = Optional.ofNullable(SecurityContextHolder.getContext())
                .map { obj: SecurityContext -> obj.authentication }
                .orElse(null)
            if (Objects.isNull(authentication)) {
                return Optional.empty()
            }
            return if (authentication is AnonymousAuthenticationToken) {
                Optional.of<SecuredUser>(SecuredUser.ANONYMOUS_USER)
            } else Optional.of(authentication)
                .map { obj: Authentication? -> obj!!.principal }
                .map { obj: Any? -> SecuredUserDetails::class.java.cast(obj) }
                .map(SecuredUserDetails::user)
        }

        fun currentLoginUserId(): Long = currentLoginUser().map(SecuredUser::id).orElse(ReservedUser.ADMIN.id)
    }
}