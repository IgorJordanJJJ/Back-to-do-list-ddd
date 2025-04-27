package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.DomainEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface DomainEventJpaRepository : JpaRepository<DomainEventEntity, UUID> {

    fun findByEntityIdAndEntityType(entityId: String, entityType: String): List<DomainEventEntity>

    fun findByUserId(userId: UUID): List<DomainEventEntity>

    fun findByOccurredOnBetween(startTime: LocalDateTime, endTime: LocalDateTime): List<DomainEventEntity>

    fun findByEventType(eventType: String): List<DomainEventEntity>
}