package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.DomainEventEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DomainEventEntityMapper(
    private val objectMapper: ObjectMapper,
    private val userJpaRepository: UserJpaRepository
) {
    /**
     * Преобразование из доменного события в JPA сущность
     */
    fun toEntity(event: DomainEvent, entityId: String, entityType: String, userId: UUID?): DomainEventEntity {
        val userEntity = userId?.let {
            userJpaRepository.findById(it).orElse(null)
        }

        return DomainEventEntity(
            eventId = event.eventId,
            eventType = event.javaClass.simpleName,
            entityId = entityId,
            entityType = entityType,
            user = userEntity,
            occurredOn = event.occurredOn,
            payload = objectMapper.writeValueAsString(event)
        )
    }

    /**
     * Десериализация события из JPA сущности
     */
    fun fromEntity(entity: DomainEventEntity): DomainEvent? {
        return try {
            val eventClassName = "jordan.pro.todo.smile.bootstrap.domain.core.event.${entity.eventType}"
            val eventClass = Class.forName(eventClassName) as Class<out DomainEvent>
            objectMapper.readValue(entity.payload, eventClass)
        } catch (e: Exception) {
            null
        }
    }
}