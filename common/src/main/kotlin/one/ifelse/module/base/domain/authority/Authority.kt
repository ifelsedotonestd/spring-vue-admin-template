package one.ifelse.module.base.domain.authority

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import one.ifelse.module.base.domain.user.User
import org.hibernate.Hibernate
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@Table(name = "authorities")
@EntityListeners(AuditingEntityListener::class)
data class Authority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "code", nullable = false, length = 100)
    var code: @Size(max = 100) @NotNull String? = null,

    @Lob
    @Column(name = "description")
    var desc: String? = null,

    @ManyToMany
    @JoinTable(
        name = "user_authorities",
        joinColumns = [JoinColumn(name = "authority_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var users: Set<User> = LinkedHashSet(),


    @Column(name = "created_by")
    @CreatedBy
    var createdBy: Long? = null,

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: Instant? = null,
) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
        val that = o as Authority
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "Authority(id=$id, code=$code, desc=$desc, users=$users, createdBy=$createdBy, createdAt=$createdAt)"
    }

}
