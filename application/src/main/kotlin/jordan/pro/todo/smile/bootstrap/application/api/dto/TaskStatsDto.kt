package jordan.pro.todo.smile.bootstrap.application.api.dto

/**
 * DTO для статистики задач в списке
 */
data class TaskStatsDto(
    val total: Int,
    val completed: Int,
    val overdue: Int,
    val byStatus: Map<StatusDto, Int>,
    val byPriority: Map<PriorityDto, Int>
)