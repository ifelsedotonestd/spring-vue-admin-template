package one.ifelse.module.base.service.user.exception

import one.ifelse.module.base.exception.CustomizedAuthenticationException
import one.ifelse.module.base.helper.Translator.Companion.eval

class UserNotExistedException : CustomizedAuthenticationException(eval("app.user.exception.not-found"))