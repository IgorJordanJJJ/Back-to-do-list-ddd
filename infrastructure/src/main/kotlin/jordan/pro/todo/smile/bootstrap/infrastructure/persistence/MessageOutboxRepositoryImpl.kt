package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.MessageOutboxRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.MessageOutbox
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.MessageId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.MessageOutboxEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.MessageOutboxJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Repository
@Transactional
open class MessageOutboxRepositoryImpl(
    private val messageOutboxJpaRepository: MessageOutboxJpaRepository,
    private val messageOutboxEntityMapper: MessageOutboxEntityMapper
) : MessageOutboxRepository {

    @Transactional(readOnly = true)
    override fun findById(id: MessageId): MessageOutbox? {
        return messageOutboxJpaRepository.findById(id.value)
            .map { messageOutboxEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByEventId(eventId: UUID): MessageOutbox? {
        return messageOutboxJpaRepository.findByEventId(eventId)
            ?.let { messageOutboxEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findPending(limit: Int): List<MessageOutbox> {
        return messageOutboxJpaRepository.findByStatusOrderByCreatedAt("PENDING", limit)
            .map { messageOutboxEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByEventType(eventType: String): List<MessageOutbox> {
        return messageOutboxJpaRepository.findByEventType(eventType)
            .map { messageOutboxEntityMapper.toDomain(it) }
    }

    override fun save(message: MessageOutbox): MessageOutbox {
        val entity = messageOutboxEntityMapper.toEntity(message)
        val savedEntity = messageOutboxJpaRepository.save(entity)
        return messageOutboxEntityMapper.toDomain(savedEntity)
    }

    override fun delete(id: MessageId) {
        messageOutboxJpaRepository.deleteById(id.value)
    }

    override fun deleteProcessedOlderThan(days: Int): Int {
        val cutoffDate = LocalDateTime.now().minusDays(days.toLong())
        return messageOutboxJpaRepository.deleteByStatusAndProcessedAtBefore("PROCESSED", cutoffDate)
    }

    @Transactional(readOnly = true)
    override fun findByStatusAndProcessedAtBefore(status: String, cutoffDate: LocalDateTime): List<MessageOutbox> {
        return messageOutboxJpaRepository.findByStatusAndProcessedAtBefore(status, cutoffDate)
            .map { messageOutboxEntityMapper.toDomain(it) }
    }
}