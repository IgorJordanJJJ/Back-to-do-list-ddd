package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Notification
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.NotificationType
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.NotificationId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.NotificationEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class NotificationEntityMapper(
    private val userJpaRepository: UserJpaRepository
) {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(notification: Notification): NotificationEntity {
        val userEntity = userJpaRepository.findById(notification.userId.value)
            .orElseThrow { IllegalStateException("Не найден пользователь с ID ${notification.userId}") }

        return NotificationEntity(
            id = notification.id.value,
            user = userEntity,
            type = notification.type.name,
            message = notification.message,
            relatedEntityId = notification.relatedEntityId,
            isRead = notification.isRead,
            createdAt = notification.createdAt
        )
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: NotificationEntity): Notification {
        return Notification.reconstitute(
            id = NotificationId.from(entity.id),
            userId = UserId.from(entity.user.id),
            type = NotificationType.valueOf(entity.type),
            message = entity.message,
            relatedEntityId = entity.relatedEntityId,
            isRead = entity.isRead,
            createdAt = entity.createdAt
        )
    }
}