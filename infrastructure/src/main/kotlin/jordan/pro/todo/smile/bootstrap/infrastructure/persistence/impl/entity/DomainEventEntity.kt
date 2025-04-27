package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "domain_events")
data class DomainEventEntity(
    @Id
    @Column(name = "event_id")
    val eventId: UUID,

    @Column(name = "event_type", nullable = false, length = 100)
    val eventType: String,

    @Column(name = "entity_id", nullable = false, length = 255)
    val entityId: String,

    @Column(name = "entity_type", nullable = false, length = 50)
    val entityType: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity?,

    @Column(name = "occurred_on", nullable = false)
    val occurredOn: LocalDateTime,

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DomainEventEntity) return false
        return eventId == other.eventId
    }

    override fun hashCode(): Int {
        return eventId.hashCode()
    }

    override fun toString(): String {
        return "DomainEventEntity(eventId=$eventId, eventType='$eventType', entityId='$entityId', occurredOn=$occurredOn)"
    }
}
