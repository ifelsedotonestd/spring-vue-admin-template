package one.ifelse.module.base.configurer

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * From Jhipster auto-generated configuration
 */
@Configuration
internal class JacksonConfigurer {
    /**
     * Support for Java date and time API.
     *
     * @return the corresponding Jackson module.
     */
    @Bean
    fun javaTimeModule(): JavaTimeModule {
        return JavaTimeModule()
    }

    @Bean
    fun jdk8TimeModule(): Jdk8Module {
        return Jdk8Module()
    }
}
