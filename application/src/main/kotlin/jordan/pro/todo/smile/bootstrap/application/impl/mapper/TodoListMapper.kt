package jordan.pro.todo.smile.bootstrap.application.impl.mapper

import jordan.pro.todo.smile.bootstrap.application.api.dto.PriorityDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.StatusDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TaskStatsDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListSummaryDto
import jordan.pro.todo.smile.bootstrap.domain.core.model.aggregates.TodoList
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskPriority
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskStatus
import org.springframework.stereotype.Component

@Component
class TodoListMapper(
    private val taskMapper: TaskMapper
) {
    /**
     * Преобразовать доменную модель TodoList в DTO
     */
    fun toDto(todoList: TodoList): TodoListDto {
        // Преобразуем задачи
        val taskDtos = todoList.tasks.map { taskMapper.toDto(it) }

        // Считаем статистику
        val taskStats = calculateTaskStats(todoList)

        return TodoListDto(
            id = todoList.id.toString(),
            title = todoList.title.toString(),
            description = todoList.description.toString(),
            tasks = taskDtos,
            ownerId = todoList.ownerId.toString(),
            sharedWith = todoList.sharedWith.map { it.toString() },
            createdAt = todoList.createdAt,
            updatedAt = todoList.updatedAt,
            taskStats = taskStats
        )
    }

    /**
     * Преобразовать доменную модель TodoList в краткое DTO
     */
    fun toSummaryDto(todoList: TodoList): TodoListSummaryDto {
        // Считаем основные метрики
        val taskCount = todoList.tasks.size
        val completedTaskCount = todoList.tasks.count { it.status.isCompleted() }
        val overdueTaskCount = todoList.tasks.count { it.isOverdue() }

        return TodoListSummaryDto(
            id = todoList.id.toString(),
            title = todoList.title.toString(),
            description = todoList.description.toString(),
            taskCount = taskCount,
            completedTaskCount = completedTaskCount,
            overdueTaskCount = overdueTaskCount,
            ownerId = todoList.ownerId.toString(),
            isShared = todoList.sharedWith.isNotEmpty(),
            updatedAt = todoList.updatedAt
        )
    }

    /**
     * Расчет статистики задач в списке
     */
    private fun calculateTaskStats(todoList: TodoList): TaskStatsDto {
        val tasks = todoList.tasks

        // Статистика по статусам
        val byStatus = TaskStatus.values().associateWith { status ->
            tasks.count { it.status == status }
        }.mapKeys { (status, _) ->
            mapToStatusDto(status)
        }

        // Статистика по приоритетам
        val byPriority = TaskPriority.values().associateWith { priority ->
            tasks.count { it.priority == priority }
        }.mapKeys { (priority, _) ->
            mapToPriorityDto(priority)
        }

        return TaskStatsDto(
            total = tasks.size,
            completed = tasks.count { it.status.isCompleted() },
            overdue = tasks.count { it.isOverdue() },
            byStatus = byStatus,
            byPriority = byPriority
        )
    }

    /**
     * Маппинг доменного статуса в DTO
     */
    private fun mapToStatusDto(status: TaskStatus): StatusDto {
        return when (status) {
            TaskStatus.TODO -> StatusDto.TODO
            TaskStatus.IN_PROGRESS -> StatusDto.IN_PROGRESS
            TaskStatus.BLOCKED -> StatusDto.BLOCKED
            TaskStatus.COMPLETED -> StatusDto.COMPLETED
            TaskStatus.ARCHIVED -> StatusDto.ARCHIVED
        }
    }

    /**
     * Маппинг доменного приоритета в DTO
     */
    private fun mapToPriorityDto(priority: TaskPriority): PriorityDto {
        return when (priority) {
            TaskPriority.LOW -> PriorityDto.LOW
            TaskPriority.MEDIUM -> PriorityDto.MEDIUM
            TaskPriority.HIGH -> PriorityDto.HIGH
            TaskPriority.CRITICAL -> PriorityDto.CRITICAL
        }
    }
}