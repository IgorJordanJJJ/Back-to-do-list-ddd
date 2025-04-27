package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.MessageOutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface MessageOutboxJpaRepository : JpaRepository<MessageOutboxEntity, Long> {
    /**
     * Найти сообщение по UUID события
     */
    fun findByEventId(eventId: UUID): MessageOutboxEntity?

    /**
     * Найти сообщения с указанным статусом, отсортированные по дате создания
     */
    @Query(value = "SELECT m FROM MessageOutboxEntity m WHERE m.status = :status ORDER BY m.createdAt ASC LIMIT :limit")
    fun findByStatusOrderByCreatedAt(@Param("status") status: String, @Param("limit") limit: Int): List<MessageOutboxEntity>

    /**
     * Найти сообщения по типу события
     */
    fun findByEventType(eventType: String): List<MessageOutboxEntity>

    /**
     * Удалить обработанные сообщения, которые старше указанной даты
     * Возвращает количество удаленных записей
     */
    @Modifying
    @Query("DELETE FROM MessageOutboxEntity m WHERE m.status = :status AND m.processedAt < :cutoffDate")
    fun deleteByStatusAndProcessedAtBefore(@Param("status") status: String, @Param("cutoffDate") cutoffDate: LocalDateTime): Int

    /**
     * Найти сообщения по статусу и дате обработки
     */
    fun findByStatusAndProcessedAtBefore(status: String, cutoffDate: LocalDateTime): List<MessageOutboxEntity>
}