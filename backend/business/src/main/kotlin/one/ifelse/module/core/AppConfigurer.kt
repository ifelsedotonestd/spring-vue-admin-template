package one.ifelse.module.core

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(basePackages = ["one.ifelse.module.core.**"])
@EnableJpaRepositories(basePackages = ["one.ifelse.module.core.**"])
class AppConfigurer