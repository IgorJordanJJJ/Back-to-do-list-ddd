package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для краткой информации о списке задач
 */
data class TodoListSummaryDto(
    val id: String,
    val title: String,
    val description: String,
    val taskCount: Int,
    val completedTaskCount: Int,
    val overdueTaskCount: Int,
    val ownerId: String,
    val isShared: Boolean,
    val updatedAt: LocalDateTime
)