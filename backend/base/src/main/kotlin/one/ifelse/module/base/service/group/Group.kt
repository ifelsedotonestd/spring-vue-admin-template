package one.ifelse.module.base.service.group

import jakarta.persistence.*
import one.ifelse.module.base.helper.AuditableEntity
import one.ifelse.module.base.helper.Status
import one.ifelse.module.base.service.user.User
import org.hibernate.Hibernate
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(
    name = "groups",
    indexes = [
        Index(name = "groups_idx_status", columnList = "status"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class Group(
    @Id
    @GeneratedValue(generator = "group_seq_generator", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(
        name = "group_seq_generator",
        strategy = "one.ifelse.module.base.helper.generator.UseExistingIdOrGeneratedIdGenerator",
        parameters = [
            org.hibernate.annotations.Parameter(name = "sequence_name", value = "groups_sequence"),
            org.hibernate.annotations.Parameter(name = "initial_value", value = "1000"),
            org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
        ]
    )
    var id: Long?,

    var name: String?,

    @Column(name = "description")
    var desc: String?,

    @Column(name = "status")
    var status: Status?,

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var users: Set<User>? = LinkedHashSet()

) : AuditableEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val that = other as Group
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
