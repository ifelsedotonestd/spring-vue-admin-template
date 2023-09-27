package one.ifelse.module.base.domain.user.exception

import one.ifelse.module.base.exception.LogicException
import one.ifelse.module.base.helper.Translator.Companion.eval

class UserNotVerifyException : LogicException(eval(""))