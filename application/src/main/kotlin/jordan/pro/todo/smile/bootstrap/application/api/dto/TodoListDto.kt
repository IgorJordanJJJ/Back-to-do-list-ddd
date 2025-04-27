package jordan.pro.todo.smile.bootstrap.application.api.dto

import java.time.LocalDateTime

/**
 * DTO для списка задач
 */
data class TodoListDto(
    val id: String,
    val title: String,
    val description: String,
    val tasks: List<TaskDto>,
    val ownerId: String,
    val sharedWith: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val taskStats: TaskStatsDto
)