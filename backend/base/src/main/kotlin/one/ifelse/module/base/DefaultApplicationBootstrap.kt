package one.ifelse.module.base

import com.github.f4b6a3.ulid.Ulid
import jakarta.annotation.PostConstruct
import one.ifelse.module.base.annotation.AuthorityResourceClaim
import one.ifelse.module.base.helper.ReservedAuthority
import one.ifelse.module.base.helper.ReservedUser
import one.ifelse.module.base.helper.Status
import one.ifelse.module.base.service.authorization.Authority
import one.ifelse.module.base.service.authorization.AuthorityRepository
import one.ifelse.module.base.service.group.GroupRepository
import one.ifelse.module.base.service.user.PasswordGenerator
import one.ifelse.module.base.service.user.User
import one.ifelse.module.base.service.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Configuration
class DefaultApplicationBootstrap(

    protected val txManager: PlatformTransactionManager,

    private val passwordEncoder: PasswordEncoder,

    private val userRepo: UserRepository,

    private val authorityRepo: AuthorityRepository,

    private val groupRepo: GroupRepository,

    private val authorities: List<AuthorityResourceClaim>

) {
    @PostConstruct
    fun afterInit() {
        val tx = TransactionTemplate(txManager)
        tx.executeWithoutResult {
            migrateAuthorities()
            migrateUsers()
        }
    }

    private fun migrateAuthorities() {
        val bootstrapAuthorities = authorities.map(AuthorityResourceClaim::privilege).flatMap { it.asSequence() }
        val bootstrapAuthoritiesByCode = bootstrapAuthorities.map { it.key }
        val existed = authorityRepo.findAll()
        val existedByCode = existed.map { it.code }

        val newAuthorities = bootstrapAuthorities.filter { !existedByCode.contains(it.key) }
        addNewAuthorities(newAuthorities)

        val existedByMap = existed.associateBy { it.code }
        val updateAuthorities = bootstrapAuthorities.filter { existedByCode.contains(it.key) }
        updateExistingAuthorities(updateAuthorities, existedByMap)

        val deleteAuthorities = existedByCode.filter { !bootstrapAuthoritiesByCode.contains(it) }.filterNotNull()
        deleteObsoleteAuthorities(deleteAuthorities, existedByMap)
    }

    private fun addNewAuthorities(newAuthorities: List<Map.Entry<String, String>>) {
        authorityRepo.saveAll(newAuthorities.map { Authority(code = it.key, desc = it.value) })
    }

    private fun updateExistingAuthorities(
        updateAuthorities: List<Map.Entry<String, String>>,
        authorities: Map<String?, Authority>
    ) {
        val potentialUpdatedAuthorities = ArrayList<Authority>()
        for (au in updateAuthorities) {
            authorities[au.key]?.let {
                if (it.code != au.key || it.desc != au.value) {
                    it.code = au.key
                    it.desc = au.value
                    potentialUpdatedAuthorities.add(it)
                }

            }
        }
        if (potentialUpdatedAuthorities.size > 0) {
            authorityRepo.saveAll(potentialUpdatedAuthorities)
        }

    }

    private fun deleteObsoleteAuthorities(deleteAuthorities: List<String>, authorities: Map<String?, Authority>) {
        val potentialDeletedAuthorities = ArrayList<Authority>()
        for (code in deleteAuthorities) {
            authorities[code]?.let {
                potentialDeletedAuthorities.add(it)
            }
        }
        if (potentialDeletedAuthorities.size > 0) {
            authorityRepo.deleteAll(potentialDeletedAuthorities)
        }
    }

    private fun migrateUsers() {
        userRepo.findById(ReservedUser.ADMIN.id).ifPresentOrElse(
            {
                log.info("\n\n\uD83D\uDD25 User ${ReservedUser.ADMIN} is existed. Skip create\n")
            }, {
                val password = PasswordGenerator.generateStrongPassword()
                val adminUser = User(
                    id = ReservedUser.ADMIN.id,
                    password = passwordEncoder.encode(password),
                    username = ReservedUser.ADMIN.username,
                    preferredUsername = Ulid.fast().toString(),
                    email = ReservedUser.ADMIN.email,
                    emailVerified = true,
                    locked = false,
                    enabled = true,
                    expired = false,
                    status = Status.ACTIVE
                )
                val adminAuthorities =
                    authorityRepo.findAllByCodeIsIn(ReservedAuthority.entries.map { it.code }).toHashSet()
                adminUser.authorities = adminAuthorities
                userRepo.save(adminUser)
                log.info("\n\n\uD83D\uDD25 User ${ReservedUser.ADMIN} is created with password: $password\n")
            })

        userRepo.findById(ReservedUser.ANONYMOUS.id).ifPresentOrElse(
            {
                log.info("\n\n\uD83D\uDD25 User ${ReservedUser.ANONYMOUS} is existed. Skip create\n")
            },
            {
                userRepo.save(
                    User(
                        id = ReservedUser.ANONYMOUS.id,
                        password = passwordEncoder.encode(PasswordGenerator.generateStrongPassword()),
                        username = ReservedUser.ANONYMOUS.username,
                        preferredUsername = Ulid.fast().toString(),
                        email = ReservedUser.ANONYMOUS.email,
                        emailVerified = true,
                        locked = true,
                        enabled = true,
                        expired = false,
                        status = Status.ACTIVE
                    )
                )
                log.info("\n\n\uD83D\uDD25 User ${ReservedUser.ANONYMOUS} is created.\n")
            }
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}