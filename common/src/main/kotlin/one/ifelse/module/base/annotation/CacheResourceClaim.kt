package one.ifelse.module.base.annotation

interface CacheResourceClaim {
    fun cacheNames(): Map<String, Long>
}