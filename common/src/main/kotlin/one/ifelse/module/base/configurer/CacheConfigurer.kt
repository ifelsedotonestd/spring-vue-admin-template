package one.ifelse.module.base.configurer

import one.ifelse.module.base.annotation.CacheResourceClaim
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class CacheConfigurer(
    private val cacheConfigs: List<CacheResourceClaim>
) {

    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder ->
            cacheConfigs.map { it.cacheNames() }.flatMap { it.asSequence() }.forEach {
                builder
                    .withCacheConfiguration(
                        it.key,
                        RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofMinutes(it.value))
                    )
            }
        }
    }
}