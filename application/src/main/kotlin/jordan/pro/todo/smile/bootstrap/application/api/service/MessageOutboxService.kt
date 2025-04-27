package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.EnqueueMessageCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkMessageAsFailedCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkMessageAsProcessedCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.MessageOutboxDto

/**
 * Сервис для работы с очередью сообщений
 */
interface MessageOutboxService {
    /**
     * Добавить сообщение в очередь
     */
    fun enqueueMessage(command: EnqueueMessageCommand): MessageOutboxDto

    /**
     * Получить сообщения для обработки
     */
    fun getPendingMessages(limit: Int = 10): List<MessageOutboxDto>

    /**
     * Отметить сообщение как обработанное
     */
    fun markAsProcessed(command: MarkMessageAsProcessedCommand): MessageOutboxDto

    /**
     * Отметить сообщение как неудачное
     */
    fun markAsFailed(command: MarkMessageAsFailedCommand): MessageOutboxDto

    /**
     * Удалить старые обработанные сообщения
     */
    fun cleanupProcessedMessages(olderThanDays: Int): Int
}