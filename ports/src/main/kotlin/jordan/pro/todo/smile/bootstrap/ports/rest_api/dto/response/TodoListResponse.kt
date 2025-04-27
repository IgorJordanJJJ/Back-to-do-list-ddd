package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response

import java.time.LocalDateTime

/**
 * REST API ответ: список задач с полной информацией
 */
data class TodoListResponse(
    val id: String,
    val title: String,
    val description: String,
    val tasks: List<TaskResponse>,
    val owner: UserResponse,
    val sharedWith: List<UserResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val totalTasks: Int,
    val completedTasks: Int,
    val overdueTasks: Int
)