package one.ifelse.module.base.exception

import one.ifelse.module.base.helper.Translator.Companion.eval
import org.springframework.security.core.AuthenticationException

open class JwtTokenException : AuthenticationException(eval("app.auth.exception.token-not-valid"))
