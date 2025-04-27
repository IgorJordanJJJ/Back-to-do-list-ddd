package jordan.pro.todo.smile.bootstrap.application.api.query

import jordan.pro.todo.smile.bootstrap.application.api.dto.NotificationDto
import java.time.LocalDateTime

/**
 * Запрос: Получить уведомление по ID
 */
data class GetNotificationByIdQuery(
    val notificationId: String,
    val userId: String
) {
    interface Handler {
        fun handle(query: GetNotificationByIdQuery): NotificationDto
    }
}

/**
 * Запрос: Получить уведомления пользователя
 */
data class GetUserNotificationsQuery(
    val userId: String,
    val onlyUnread: Boolean = false,
    val since: LocalDateTime? = null,
    val page: Int = 0,
    val size: Int = 20
) {
    interface Handler {
        fun handle(query: GetUserNotificationsQuery): NotificationPageDto
    }
}

/**
 * Результат запроса уведомлений с пагинацией
 */
data class NotificationPageDto(
    val items: List<NotificationDto>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val unreadCount: Int
)