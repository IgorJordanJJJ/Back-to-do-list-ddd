package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response

import jordan.pro.todo.smile.bootstrap.application.api.dto.PriorityDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.StatusDto
import java.time.LocalDateTime

/**
 * REST API ответ: задача
 */
data class TaskResponse(
    val id: String,
    val title: String,
    val description: String,
    val priority: PriorityDto,
    val status: StatusDto,
    val dueDate: LocalDateTime?,
    val tags: Set<String>,
    val isOverdue: Boolean,
    val createdBy: UserResponse,
    val assignedTo: UserResponse?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime?
)