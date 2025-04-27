package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "message_outbox")
data class MessageOutboxEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "event_id", nullable = false)
    val eventId: UUID,

    @Column(name = "event_type", nullable = false, length = 100)
    val eventType: String,

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(name = "routing_key", nullable = false)
    val routingKey: String,

    @Column(name = "status", nullable = false)
    var status: String = "PENDING",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "processed_at")
    var processedAt: LocalDateTime?,

    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageOutboxEntity) return false
        if (id != null && other.id != null) return id == other.id
        return eventId == other.eventId
    }

    override fun hashCode(): Int {
        return eventId.hashCode()
    }

    override fun toString(): String {
        return "MessageOutboxEntity(id=$id, eventId=$eventId, eventType='$eventType', status='$status', retryCount=$retryCount)"
    }
}