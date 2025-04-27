package jordan.pro.todo.smile.bootstrap.domain.api.service

import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.MessageOutbox

/**
 * Доменный сервис для работы с очередью сообщений
 */
interface MessageOutboxService {
    /**
     * Добавить сообщение в очередь
     */
    fun enqueue(event: DomainEvent, routingKey: String): MessageOutbox

    /**
     * Обработать сообщения в очереди
     */
    fun processPendingMessages(batchSize: Int)

    /**
     * Пометить сообщение как обработанное
     */
    fun markAsProcessed(messageId: String)

    /**
     * Отметить сообщение как неудачное с увеличением счетчика попыток
     */
    fun markAsFailed(messageId: String)

    /**
     * Очистить старые обработанные сообщения
     */
    fun cleanupProcessedMessages(olderThanDays: Int)
}