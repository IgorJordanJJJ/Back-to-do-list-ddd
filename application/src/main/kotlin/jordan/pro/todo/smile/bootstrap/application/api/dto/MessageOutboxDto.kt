package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO для сообщения в очереди
 */
data class MessageOutboxDto(
    val id: String,
    val eventId: UUID,
    val eventType: String,
    val payload: String,
    val routingKey: String,
    val status: String,
    val createdAt: LocalDateTime,
    val processedAt: LocalDateTime?,
    val retryCount: Int
)