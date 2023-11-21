package one.ifelse.module.base.service.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByPreferredUsername(preferredUsername: String): Optional<User>
    fun findByUsername(username: String): Optional<User>
}