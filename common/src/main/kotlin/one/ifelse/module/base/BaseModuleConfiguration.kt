package one.ifelse.module.base

import one.ifelse.module.base.annotation.AuthorityResourceClaim
import one.ifelse.module.base.helper.ReservedAuthority
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@ComponentScan
@Configuration
@PropertySource(value = ["classpath:base-00.properties"])
@EnableCaching
class BaseModuleConfiguration : AuthorityResourceClaim {
    override fun privilege(): Map<String, String> {
        return ReservedAuthority.values().associate { it.code to it.desc }
    }

}