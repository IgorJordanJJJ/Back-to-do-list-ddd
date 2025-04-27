package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.MessageOutbox
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.MessageId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Репозиторий для работы с исходящими сообщениями
 */
interface MessageOutboxRepository {
    /**
     * Найти сообщение по ID
     */
    fun findById(id: MessageId): MessageOutbox?

    /**
     * Найти сообщение по ID события
     */
    fun findByEventId(eventId: UUID): MessageOutbox?

    /**
     * Найти все сообщения в статусе ожидания
     */
    fun findPending(limit: Int = 10): List<MessageOutbox>

    /**
     * Найти сообщения по типу события
     */
    fun findByEventType(eventType: String): List<MessageOutbox>

    /**
     * Сохранить сообщение
     */
    fun save(message: MessageOutbox): MessageOutbox

    /**
     * Удалить сообщение
     */
    fun delete(id: MessageId)

    /**
     * Удалить обработанные сообщения старше указанного времени
     * Возвращает количество удаленных записей
     */
    fun deleteProcessedOlderThan(days: Int): Int

    /**
     * Найти сообщения по статусу и времени обработки
     */
    fun findByStatusAndProcessedAtBefore(status: String, cutoffDate: LocalDateTime): List<MessageOutbox>
}