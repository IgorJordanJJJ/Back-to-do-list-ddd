package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.EnqueueMessageCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkMessageAsFailedCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkMessageAsProcessedCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.MessageOutboxDto
import jordan.pro.todo.smile.bootstrap.application.api.service.MessageOutboxService
import jordan.pro.todo.smile.bootstrap.domain.api.repository.MessageOutboxRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.MessageOutbox
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.MessageId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class MessageOutboxServiceImpl(
    private val messageOutboxRepository: MessageOutboxRepository
) : MessageOutboxService {

    @Transactional
    override fun enqueueMessage(command: EnqueueMessageCommand): MessageOutboxDto {
        // Создание нового сообщения
        val message = MessageOutbox.create(
            eventId = command.eventId,
            eventType = command.eventType,
            payload = command.payload,
            routingKey = command.routingKey
        )

        // Сохранение в репозитории
        val savedMessage = messageOutboxRepository.save(message)

        // Возврат DTO
        return toDto(savedMessage)
    }

    @Transactional(readOnly = true)
    override fun getPendingMessages(limit: Int): List<MessageOutboxDto> {
        val messages = messageOutboxRepository.findPending(limit)
        return messages.map { toDto(it) }
    }

    @Transactional
    override fun markAsProcessed(command: MarkMessageAsProcessedCommand): MessageOutboxDto {
        val messageId = MessageId.from(command.messageId)

        // Поиск сообщения
        val message = messageOutboxRepository.findById(messageId)
            ?: throw IllegalArgumentException("Сообщение с ID ${command.messageId} не найдено")

        // Пометка как обработанное
        message.markAsProcessed()

        // Сохранение изменений
        val updatedMessage = messageOutboxRepository.save(message)

        // Возврат DTO
        return toDto(updatedMessage)
    }

    @Transactional
    override fun markAsFailed(command: MarkMessageAsFailedCommand): MessageOutboxDto {
        val messageId = MessageId.from(command.messageId)

        // Поиск сообщения
        val message = messageOutboxRepository.findById(messageId)
            ?: throw IllegalArgumentException("Сообщение с ID ${command.messageId} не найдено")

        // Пометка как неудачное
        message.markAsFailed()

        // Сохранение изменений
        val updatedMessage = messageOutboxRepository.save(message)

        // Возврат DTO
        return toDto(updatedMessage)
    }

    @Transactional
    override fun cleanupProcessedMessages(olderThanDays: Int): Int {
        // Используем метод репозитория для удаления старых обработанных сообщений
        return messageOutboxRepository.deleteProcessedOlderThan(olderThanDays)
    }

    /**
     * Преобразование доменной модели в DTO
     */
    private fun toDto(message: MessageOutbox): MessageOutboxDto {
        return MessageOutboxDto(
            id = message.id.toString(),
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
}