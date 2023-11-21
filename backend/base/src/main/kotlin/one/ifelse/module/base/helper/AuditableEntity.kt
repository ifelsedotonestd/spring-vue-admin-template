package one.ifelse.module.base.helper

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@MappedSuperclass
abstract class AuditableEntity(
    @Column(name = "created_by")
    @CreatedBy
    open var createdBy: Long? = null,

    @Column(name = "created_at", columnDefinition = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    open var createdAt: Instant? = null,

    @Column(name = "updated_by")
    @LastModifiedBy
    open var updatedBy: Long? = null,

    @Column(name = "updated_at", columnDefinition = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    open var updatedAt: Instant? = null
)