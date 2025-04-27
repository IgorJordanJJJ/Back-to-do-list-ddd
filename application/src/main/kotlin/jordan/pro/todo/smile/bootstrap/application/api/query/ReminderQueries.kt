package jordan.pro.todo.smile.bootstrap.application.api.query

import jordan.pro.todo.smile.bootstrap.application.api.dto.ReminderDto

/**
 * Запрос: Получить напоминание по ID
 */
data class GetReminderByIdQuery(
    val reminderId: String,
    val userId: String
) {
    interface Handler {
        fun handle(query: GetReminderByIdQuery): ReminderDto
    }
}

/**
 * Запрос: Получить напоминания для задачи
 */
data class GetTaskRemindersQuery(
    val taskId: String,
    val userId: String
) {
    interface Handler {
        fun handle(query: GetTaskRemindersQuery): List<ReminderDto>
    }
}

/**
 * Запрос: Получить напоминания пользователя
 */
data class GetUserRemindersQuery(
    val userId: String,
    val includeCompleted: Boolean = false
) {
    interface Handler {
        fun handle(query: GetUserRemindersQuery): List<ReminderDto>
    }
}