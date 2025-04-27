package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для задачи
 */
data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val priority: PriorityDto,
    val status: StatusDto,
    val dueDate: LocalDateTime?,
    val tags: Set<String>,
    val isOverdue: Boolean,
    val createdBy: String,
    val assignedTo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime?
)