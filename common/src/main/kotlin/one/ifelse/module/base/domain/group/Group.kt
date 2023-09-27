package one.ifelse.module.base.domain.group

import jakarta.persistence.*
import one.ifelse.module.base.domain.user.User
import one.ifelse.module.base.helper.AuditableEntity
import one.ifelse.module.base.helper.Status
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

    @Basic
    @Column(name = "status")
    var statusValue: Int?,

    @Transient
    var status: Status?,

    @ManyToMany
    @JoinTable(
        name = "group_members",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    private var users: Set<User>? = LinkedHashSet()

) : AuditableEntity() {
    @PostLoad
    fun fillTransient() {
        this.status = this.statusValue?.let { Status.of(it) }
    }

    @PrePersist
    fun fillPersistent() {
        this.statusValue = this.status?.code
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
        val that = o as Group
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
