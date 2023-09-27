package one.ifelse.module.base.helper

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@MappedSuperclass
abstract class AuditableEntity(
    @Column(name = "created_by")
    @CreatedBy
    protected var createdBy: Long? = null,

    @Column(name = "created_at")
    @CreatedDate
    protected var createdAt: Instant? = null,

    @Column(name = "updated_by")
    @LastModifiedBy
    protected var updateBy: Long? = null,

    @Column(name = "updated_at")
    @LastModifiedDate
    protected var updatedAt: Instant? = null
)