package jordan.pro.todo.smile.bootstrap.application.api.query

import jordan.pro.todo.smile.bootstrap.application.api.dto.PriorityDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.StatusDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TaskDto
import java.time.LocalDateTime

/**
 * Запрос: Получить задачу по ID
 */
data class GetTaskQuery(
    val taskId: String,
    val userId: String
) {
    interface Handler {
        fun handle(query: GetTaskQuery): TaskDto
    }
}

/**
 * Запрос: Поиск задач по различным критериям
 */
data class SearchTasksQuery(
    val userId: String,
    val status: StatusDto? = null,
    val priority: PriorityDto? = null,
    val tags: List<String>? = null,
    val dueDateStart: LocalDateTime? = null,
    val dueDateEnd: LocalDateTime? = null,
    val isOverdue: Boolean? = null,
    val assignedToMe: Boolean? = null,
    val textQuery: String? = null,
    val page: Int = 0,
    val size: Int = 20
) {
    interface Handler {
        fun handle(query: SearchTasksQuery): TaskSearchResultDto
    }
}

/**
 * Запрос: Получить задачи пользователя, у которых скоро наступит срок выполнения
 */
data class GetUpcomingTasksQuery(
    val userId: String,
    val days: Int = 3
) {
    interface Handler {
        fun handle(query: GetUpcomingTasksQuery): List<TaskDto>
    }
}

/**
 * Запрос: Получить просроченные задачи пользователя
 */
data class GetOverdueTasksQuery(
    val userId: String
) {
    interface Handler {
        fun handle(query: GetOverdueTasksQuery): List<TaskDto>
    }
}

/**
 * Запрос: Получить задачи, назначенные на пользователя
 */
data class GetAssignedTasksQuery(
    val userId: String,
    val status: StatusDto? = null
) {
    interface Handler {
        fun handle(query: GetAssignedTasksQuery): List<TaskDto>
    }
}

/**
 * Запрос: Получить недавно обновленные задачи
 */
data class GetRecentlyUpdatedTasksQuery(
    val userId: String,
    val limit: Int = 10
) {
    interface Handler {
        fun handle(query: GetRecentlyUpdatedTasksQuery): List<TaskDto>
    }
}

/**
 * Результат поиска задач
 */
data class TaskSearchResultDto(
    val items: List<TaskDto>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int
)