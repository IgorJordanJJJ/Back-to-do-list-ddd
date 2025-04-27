package jordan.pro.todo.smile.bootstrap.domain.core.model.entities

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.NotificationType
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.NotificationId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Сущность: Уведомление для пользователя
 */
class Notification private constructor(
    val id: NotificationId,
    val userId: UserId,
    val type: NotificationType,
    val message: String,
    val relatedEntityId: String?,
    var isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(
            userId: UserId,
            type: NotificationType,
            message: String,
            relatedEntityId: String? = null
        ): Notification {
            return Notification(
                id = NotificationId.create(),
                userId = userId,
                type = type,
                message = message,
                relatedEntityId = relatedEntityId,
                isRead = false,
                createdAt = LocalDateTime.now()
            )
        }

        fun reconstitute(
            id: NotificationId,
            userId: UserId,
            type: NotificationType,
            message: String,
            relatedEntityId: String?,
            isRead: Boolean,
            createdAt: LocalDateTime
        ): Notification {
            return Notification(id, userId, type, message, relatedEntityId, isRead, createdAt)
        }
    }

    fun markAsRead() {
        isRead = true
    }

    fun markAsUnread() {
        isRead = false
    }
}