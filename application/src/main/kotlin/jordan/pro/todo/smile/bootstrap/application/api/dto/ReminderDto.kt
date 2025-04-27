package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для напоминания
 */
data class ReminderDto(
    val id: String,
    val taskId: String,
    val userId: String,
    val reminderTime: LocalDateTime,
    val isSent: Boolean,
    val createdAt: LocalDateTime
)