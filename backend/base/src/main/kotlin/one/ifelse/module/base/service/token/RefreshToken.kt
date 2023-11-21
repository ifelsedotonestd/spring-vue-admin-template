package one.ifelse.module.base.service.token

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.Hibernate
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "refresh_tokens",
    indexes = [
        Index(name = "refresh_tokens_idx_access_token_id", columnList = "access_token_id"),
        Index(name = "refresh_tokens_idx_user_id", columnList = "user_id"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class RefreshToken(
    @Id
    @Column(name = "id", nullable = false)
    var id: @Size(max = 72) String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "access_token_id", nullable = false, insertable = false, updatable = false)
    private val accessToken: AccessToken? = null,

    @Column(name = "access_token_id")
    var accessTokenId: @NotNull String? = null,

    @Column(name = "user_id", nullable = false)
    var userId: @NotNull Long? = null,

    @Column(name = "expired_at", columnDefinition = "timestamp")
    var expiredAt: Instant? = null,

    @Column(name = "created_by")
    @CreatedBy
    var createdBy: Long? = null,

    @Column(name = "created_at", columnDefinition = "timestamp")
    @CreatedDate
    var createdAt: Instant? = null,
) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
        val that = o as RefreshToken
        return id != null && id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
