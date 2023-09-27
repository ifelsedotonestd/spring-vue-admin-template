package one.ifelse.module.base.domain.token

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
@Table(
    name = "access_tokens",
    indexes = [
        Index(name = "access_tokens_idx_user_id", columnList = "user_id"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class AccessToken(

    @Id
    @Column(name = "id", nullable = false)
    var id: @Size(max = 72) String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, insertable = false)
    private val user: User? = null,

    @Column(name = "user_id")
    var userId: @NotNull Long? = null,

    @Column(name = "expired_at")
    var expiredAt: Instant? = null,

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
        val that = o as AccessToken
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
