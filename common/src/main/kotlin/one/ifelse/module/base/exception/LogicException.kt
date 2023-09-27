package one.ifelse.module.base.exception

import one.ifelse.module.base.helper.Translator.Companion.eval

abstract class LogicException : RuntimeException {
    protected constructor(message: String, vararg args: Any?) : super(eval(message, args))
    protected constructor(cause: Throwable?, message: String, vararg args: Any?) : super(eval(message, args), cause)
}
