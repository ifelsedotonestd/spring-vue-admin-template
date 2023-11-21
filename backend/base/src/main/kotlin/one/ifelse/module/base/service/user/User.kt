package one.ifelse.module.base.service.user

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import one.ifelse.module.base.helper.*
import one.ifelse.module.base.service.authorization.Authority
import one.ifelse.module.base.service.group.Group
import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*


@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "users_idx_unique_username", columnList = "username", unique = true),
        Index(name = "users_idx_unique_preferred_username", columnList = "preferred_username", unique = true),
        Index(name = "users_idx_unique_email", columnList = "email", unique = true),
        Index(name = "users_idx_gender", columnList = "gender"),
        Index(name = "users_idx_status", columnList = "status"),
        Index(name = "users_idx_enabled", columnList = "enabled"),
        Index(name = "users_idx_locked", columnList = "locked"),
        Index(name = "users_idx_expired", columnList = "expired"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(generator = "user_seq_generator", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(
        name = "user_seq_generator",
        strategy = "one.ifelse.module.base.helper.generator.UseExistingIdOrGeneratedIdGenerator",
        parameters = [
            org.hibernate.annotations.Parameter(name = "sequence_name", value = "users_sequence"),
            org.hibernate.annotations.Parameter(name = "initial_value", value = "1000"),
            org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
        ]
    )
    var id: Long? = null,

    @Size(max = 200)
    @Column(name = "email", length = 200)
    var email: String? = null,

    @Column(name = "email_verified")
    var emailVerified: Boolean? = false,

    @Size(max = 255)
    @Column(name = "username", length = 255, nullable = false)
    var username: String? = null,

    @Size(max = 32)
    @Column(name = "preferred_username", length = 32, nullable = false)
    var preferredUsername: String? = null,

    @Size(max = 255)
    @Column(name = "full_name", length = 255)
    var fullName: String? = null,

    @Column(name = "gender")
    @Convert(converter = GenderConverter::class)
    var gender: Gender? = Gender.UNKNOWN,

    @Size(max = 72)
    @Column(name = "password", length = 72, nullable = false)
    var password: String? = null,

    @Transient
    var rawPassword: String? = null,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean? = true,

    @Column(name = "locked", nullable = false)
    var locked: Boolean? = false,

    @Column(name = "expired", nullable = false)
    var expired: Boolean? = false,

    @Column(name = "status", nullable = false)
    @Convert(converter = StatusConverter::class)
    var status: Status? = Status.INACTIVATE,

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "group_id")]
    )
    var groups: Set<Group>? = LinkedHashSet(),

    @ManyToMany
    @JoinTable(
        name = "user_authorities",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")]
    )
    var authorities: Set<Authority>? = LinkedHashSet()


) : AuditableEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val that = other as User
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "User(id=$id, email=$email, emailVerified=$emailVerified, username=$username, preferredUsername=$preferredUsername, fullName=$fullName, gender=$gender, password=$password, rawPassword=$rawPassword, enabled=$enabled, locked=$locked, expired=$expired, status=$status"
    }
}