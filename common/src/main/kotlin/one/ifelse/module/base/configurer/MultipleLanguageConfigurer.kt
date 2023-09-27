package one.ifelse.module.base.configurer

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Configuration
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

@Configuration
internal class MultipleLanguageConfigurer(
    private val messageSource: MessageSource,
    @Value("\${app.common.settings.locales:vi,en}") private val locales: Array<String>,
) : AcceptHeaderLocaleResolver(), WebMvcConfigurer {
    override fun resolveLocale(request: HttpServletRequest): Locale {
        val headerLang: String = request.getHeader("Accept-Language")
        return if (headerLang.isEmpty()) Locale.getDefault() else Locale.lookup(
            Locale.LanguageRange.parse(
                headerLang
            ), locales.map { Locale(it) }
        )
    }


    override fun getValidator(): Validator? {
        val validator = LocalValidatorFactoryBean()
        validator.setValidationMessageSource(messageSource)
        return validator
    }
}
