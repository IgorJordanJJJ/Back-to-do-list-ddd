package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.NotificationRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Notification
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.NotificationType
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.NotificationId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.NotificationEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.NotificationJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional
open class NotificationRepositoryImpl(
    private val notificationJpaRepository: NotificationJpaRepository,
    private val notificationEntityMapper: NotificationEntityMapper
) : NotificationRepository {

    @Transactional(readOnly = true)
    override fun findById(id: NotificationId): Notification? {
        return notificationJpaRepository.findById(id.value)
            .map { notificationEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByUserId(userId: UserId): List<Notification> {
        return notificationJpaRepository.findByUserIdOrderByCreatedAtDesc(userId.value)
            .map { notificationEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findUnreadByUserId(userId: UserId): List<Notification> {
        return notificationJpaRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId.value)
            .map { notificationEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByUserIdAndType(userId: UserId, type: NotificationType): List<Notification> {
        return notificationJpaRepository.findByUserIdAndType(userId.value, type.name)
            .map { notificationEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByUserIdAndCreatedAfter(userId: UserId, createdAfter: LocalDateTime): List<Notification> {
        return notificationJpaRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId.value, createdAfter)
            .map { notificationEntityMapper.toDomain(it) }
    }

    override fun save(notification: Notification): Notification {
        val entity = notificationEntityMapper.toEntity(notification)
        val savedEntity = notificationJpaRepository.save(entity)
        return notificationEntityMapper.toDomain(savedEntity)
    }

    override fun delete(id: NotificationId) {
        notificationJpaRepository.deleteById(id.value)
    }

    override fun markAllAsRead(userId: UserId) {
        notificationJpaRepository.updateAllUnreadToRead(userId.value)
    }
}