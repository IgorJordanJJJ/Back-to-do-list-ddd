package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper
import com.fasterxml.jackson.databind.ObjectMapper
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.MessageOutbox
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.MessageId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.MessageOutboxEntity
import org.springframework.stereotype.Component

@Component
class MessageOutboxEntityMapper(
    private val objectMapper: ObjectMapper
) {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(message: MessageOutbox): MessageOutboxEntity {
        return MessageOutboxEntity(
            id = message.id.value,
            eventId = message.eventId,
            eventType = message.eventType,
            payload = message.payload,
            routingKey = message.routingKey,
            status = message.status,
            createdAt = message.createdAt,
            processedAt = message.processedAt,
            retryCount = message.retryCount
        )
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: MessageOutboxEntity): MessageOutbox {
        return MessageOutbox.reconstitute(
            id = MessageId.from(entity.id!!),
            eventId = entity.eventId,
            eventType = entity.eventType,
            payload = entity.payload,
            routingKey = entity.routingKey,
            status = entity.status,
            createdAt = entity.createdAt,
            processedAt = entity.processedAt,
            retryCount = entity.retryCount
        )
    }
}