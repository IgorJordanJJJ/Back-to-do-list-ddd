package jordan.pro.todo.smile.bootstrap.application.api.command

import java.time.LocalDateTime

/**
 * Команда: Создать напоминание
 */
data class CreateReminderCommand(
    val taskId: String,
    val userId: String,
    val reminderTime: LocalDateTime
)

/**
 * Команда: Обновить напоминание
 */
data class UpdateReminderCommand(
    val reminderId: String,
    val userId: String,
    val reminderTime: LocalDateTime
)

/**
 * Команда: Удалить напоминание
 */
data class DeleteReminderCommand(
    val reminderId: String,
    val userId: String
)