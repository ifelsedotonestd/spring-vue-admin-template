package one.ifelse.module.base.service.authorization

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AuthorityRepository : CrudRepository<Authority, Long> {
    fun findAllByCodeIsIn(ids: Collection<String>): List<Authority>

    fun findByCode(code: String): Optional<Authority>
}