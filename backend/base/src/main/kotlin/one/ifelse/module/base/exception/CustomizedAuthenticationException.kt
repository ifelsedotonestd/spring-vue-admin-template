package one.ifelse.module.base.exception

import org.springframework.security.core.AuthenticationException

open class CustomizedAuthenticationException(override val message: String) : AuthenticationException(message)