package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для уведомления
 */
data class NotificationDto(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val message: String,
    val relatedEntityId: String?,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)

/**
 * Типы уведомлений
 */
enum class NotificationType {
    TASK_CREATED,
    TASK_UPDATED,
    TASK_DELETED,
    TASK_COMPLETED,
    TASK_ASSIGNED,
    TASK_UNASSIGNED,
    TASK_DUE_SOON,
    TASK_OVERDUE,
    TODO_LIST_SHARED,
    TODO_LIST_UNSHARED,
    TODO_LIST_UPDATED,
    REMINDER
}