package one.ifelse.module.base.domain.user

import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {
    fun findByPreferredUsername(preferredUsername: String): Optional<User>
    fun findByUsername(username: String): Optional<User>
}