package jordan.pro.todo.smile.bootstrap.domain.api.service

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Notification
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.NotificationType
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

/**
 * Доменный сервис для работы с уведомлениями
 */
interface NotificationService {
    /**
     * Создать и сохранить уведомление
     */
    fun createNotification(userId: UserId, type: NotificationType, message: String, relatedEntityId: String? = null): Notification

    /**
     * Получить непрочитанные уведомления пользователя
     */
    fun getUnreadNotifications(userId: UserId): List<Notification>

    /**
     * Отметить уведомление как прочитанное
     */
    fun markAsRead(notificationId: String, userId: UserId)

    /**
     * Отметить все уведомления пользователя как прочитанные
     */
    fun markAllAsRead(userId: UserId)
}