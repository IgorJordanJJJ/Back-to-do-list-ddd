package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Notification
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.NotificationType
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.NotificationId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Репозиторий для работы с уведомлениями
 */
interface NotificationRepository {
    /**
     * Найти уведомление по ID
     */
    fun findById(id: NotificationId): Notification?

    /**
     * Найти уведомления пользователя
     */
    fun findByUserId(userId: UserId): List<Notification>

    /**
     * Найти непрочитанные уведомления пользователя
     */
    fun findUnreadByUserId(userId: UserId): List<Notification>

    /**
     * Найти уведомления пользователя по типу
     */
    fun findByUserIdAndType(userId: UserId, type: NotificationType): List<Notification>

    /**
     * Найти уведомления, созданные после указанной даты
     */
    fun findByUserIdAndCreatedAfter(userId: UserId, createdAfter: LocalDateTime): List<Notification>

    /**
     * Сохранить уведомление
     */
    fun save(notification: Notification): Notification

    /**
     * Удалить уведомление
     */
    fun delete(id: NotificationId)

    /**
     * Отметить все уведомления пользователя как прочитанные
     */
    fun markAllAsRead(userId: UserId)
}