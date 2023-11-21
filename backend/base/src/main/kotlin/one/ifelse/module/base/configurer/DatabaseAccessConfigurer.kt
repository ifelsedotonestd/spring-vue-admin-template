package one.ifelse.module.base.configurer

import one.ifelse.module.base.helper.ContextHelper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
internal class DatabaseAccessConfigurer {
    @Bean
    fun auditorProvider(): AuditorAware<Long> {
        return DomainAuditorAware()
    }

    internal class DomainAuditorAware : AuditorAware<Long> {
        override fun getCurrentAuditor(): Optional<Long> = Optional.of(ContextHelper.currentLoginUserId())
    }
}
