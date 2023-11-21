package one.ifelse.module.base.exception

import one.ifelse.module.base.helper.Translator.Companion.eval

open class JwtTokenException : CustomizedAuthenticationException(eval("app.auth.exception.token-not-valid"))
