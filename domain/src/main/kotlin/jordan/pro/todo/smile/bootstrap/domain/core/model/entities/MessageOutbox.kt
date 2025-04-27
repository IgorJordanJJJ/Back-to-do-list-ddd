package jordan.pro.todo.smile.bootstrap.domain.core.model.entities

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.MessageId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Сущность: Исходящее сообщение для асинхронной обработки
 */
class MessageOutbox private constructor(
    val id: MessageId,
    val eventId: UUID,
    val eventType: String,
    val payload: String,
    val routingKey: String,
    var status: String,
    val createdAt: LocalDateTime,
    var processedAt: LocalDateTime?,
    var retryCount: Int
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_PROCESSED = "PROCESSED"
        const val STATUS_FAILED = "FAILED"

        fun create(eventId: UUID, eventType: String, payload: String, routingKey: String): MessageOutbox {
            return MessageOutbox(
                id = MessageId.create(),
                eventId = eventId,
                eventType = eventType,
                payload = payload,
                routingKey = routingKey,
                status = STATUS_PENDING,
                createdAt = LocalDateTime.now(),
                processedAt = null,
                retryCount = 0
            )
        }

        fun reconstitute(
            id: MessageId,
            eventId: UUID,
            eventType: String,
            payload: String,
            routingKey: String,
            status: String,
            createdAt: LocalDateTime,
            processedAt: LocalDateTime?,
            retryCount: Int
        ): MessageOutbox {
            return MessageOutbox(
                id, eventId, eventType, payload, routingKey,
                status, createdAt, processedAt, retryCount
            )
        }
    }

    fun markAsProcessed() {
        this.status = STATUS_PROCESSED
        this.processedAt = LocalDateTime.now()
    }

    fun markAsFailed() {
        this.status = STATUS_FAILED
        this.retryCount += 1
    }

    fun isPending(): Boolean {
        return status == STATUS_PENDING
    }
}
