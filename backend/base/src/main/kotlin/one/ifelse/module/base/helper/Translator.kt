package one.ifelse.module.base.helper

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
class Translator {

    @Autowired
    fun setMessageSource(messageSource: MessageSource) {
        Companion.messageSource = messageSource
    }

    companion object {
        private lateinit var messageSource: MessageSource

        private val log = LoggerFactory.getLogger(this::class.java)

        fun eval(message: String, vararg args: Any?): String {
            val locale = LocaleContextHolder.getLocale()
            return try {
                messageSource.getMessage(message, args, locale)
            } catch (ignoredException: NoSuchMessageException) {
                log.warn("No message found for key: {} ", message)
                message
            }
        }
    }
}