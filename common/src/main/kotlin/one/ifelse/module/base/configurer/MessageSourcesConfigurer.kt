package one.ifelse.module.base.configurer

import one.ifelse.module.base.annotation.MessageResourceClaim
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration
class MessageSourcesConfigurer(private val instructors: List<MessageResourceClaim>) {

    @Bean
    fun messageSource(): MessageSource {
        val sourcePaths = instructors.flatMap { it.messagesSources().toList() }.toTypedArray()
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasenames(*sourcePaths)
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name())
        return messageSource
    }
}
