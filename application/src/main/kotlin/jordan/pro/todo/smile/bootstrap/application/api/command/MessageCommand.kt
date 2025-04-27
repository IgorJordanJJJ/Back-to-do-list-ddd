package jordan.pro.todo.smile.bootstrap.application.api.command

import java.util.UUID

/**
 * Команда: Добавить сообщение в очередь
 */
data class EnqueueMessageCommand(
    val eventId: UUID,
    val eventType: String,
    val payload: String,
    val routingKey: String
)

/**
 * Команда: Отметить сообщение как обработанное
 */
data class MarkMessageAsProcessedCommand(
    val messageId: String
)

/**
 * Команда: Отметить сообщение как неудачное
 */
data class MarkMessageAsFailedCommand(
    val messageId: String
)