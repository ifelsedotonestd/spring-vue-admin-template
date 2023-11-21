package one.ifelse.module.base

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import one.ifelse.module.base.annotation.AuthorityResourceClaim
import one.ifelse.module.base.annotation.MessageResourceClaim
import one.ifelse.module.base.helper.ReservedAuthority
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@ComponentScan
@Configuration
@PropertySource(value = ["classpath:base-00.properties"])
@EnableCaching
@EntityScan(basePackages = ["one.ifelse.module.base.service.**"])
@EnableJpaRepositories(basePackages = ["one.ifelse.module.base.service.**"])
class BaseModuleConfiguration(
    private val objectMapper: ObjectMapper
) : AuthorityResourceClaim, MessageResourceClaim {
    override fun privilege(): Map<String, String> {
        return ReservedAuthority.entries.associate { it.code to it.desc }
    }

    @Bean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val converter = MappingJackson2HttpMessageConverter()
        objectMapper.registerModule(
            SimpleModule().addDeserializer(
                String::class.java,
                StringTrimmerDeserializer()
            )
        )
        converter.objectMapper = objectMapper
        return converter
    }

    internal class StringTrimmerDeserializer : JsonDeserializer<String>() {
        override fun deserialize(parser: JsonParser?, context: DeserializationContext?): String? {
            return StringDeserializer.instance.deserialize(parser, context)?.trim()
        }
    }

    override fun messagesSources(): Array<String> {
        return arrayOf("classpath:common-messages")
    }

}