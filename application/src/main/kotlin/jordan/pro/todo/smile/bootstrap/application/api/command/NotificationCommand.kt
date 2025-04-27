package jordan.pro.todo.smile.bootstrap.application.api.command

/**
 * Команда: Создать уведомление
 */
data class CreateNotificationCommand(
    val userId: String,
    val type: String,
    val message: String,
    val relatedEntityId: String? = null
)

/**
 * Команда: Отметить уведомление как прочитанное
 */
data class MarkNotificationAsReadCommand(
    val notificationId: String,
    val userId: String
)

/**
 * Команда: Отметить все уведомления пользователя как прочитанные
 */
data class MarkAllNotificationsAsReadCommand(
    val userId: String
)