package one.ifelse.module.base.configurer

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
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

    @Bean
    fun hibernate6Module(): Hibernate6Module {
        return Hibernate6Module().configure(
            Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS,
            true
        )
    }
}
