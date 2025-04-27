package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "notifications")
data class NotificationEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "type", nullable = false, length = 50)
    val type: String,

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    val message: String,

    @Column(name = "related_entity_id")
    val relatedEntityId: String?,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotificationEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "NotificationEntity(id=$id, userId=${user.id}, type='$type', isRead=$isRead, createdAt=$createdAt)"
    }
}