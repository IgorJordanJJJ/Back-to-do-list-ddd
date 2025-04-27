package jordan.pro.todo.smile.bootstrap.application.impl.mapper

import jordan.pro.todo.smile.bootstrap.application.api.dto.PriorityDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.StatusDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TaskDto
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Task
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskPriority
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskStatus
import org.springframework.stereotype.Component

@Component
class TaskMapper {
    /**
     * Преобразовать доменную модель Task в DTO
     */
    fun toDto(task: Task): TaskDto {
        return TaskDto(
            id = task.id.toString(),
            title = task.title.toString(),
            description = task.description.toString(),
            priority = mapToPriorityDto(task.priority),
            status = mapToStatusDto(task.status),
            dueDate = task.dueDate?.value,
            tags = task.tags.map { it.toString() }.toSet(),
            isOverdue = task.isOverdue(),
            createdBy = task.createdBy.toString(),
            assignedTo = task.assignedTo?.toString(),
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
            completedAt = task.completedAt
        )
    }

    /**
     * Маппинг доменного статуса в DTO
     */
    fun mapToStatusDto(status: TaskStatus): StatusDto {
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
    fun mapToPriorityDto(priority: TaskPriority): PriorityDto {
        return when (priority) {
            TaskPriority.LOW -> PriorityDto.LOW
            TaskPriority.MEDIUM -> PriorityDto.MEDIUM
            TaskPriority.HIGH -> PriorityDto.HIGH
            TaskPriority.CRITICAL -> PriorityDto.CRITICAL
        }
    }

    /**
     * Обратный маппинг из DTO приоритета в доменный
     */
    fun mapToDomainPriority(priorityDto: PriorityDto): TaskPriority {
        return when (priorityDto) {
            PriorityDto.LOW -> TaskPriority.LOW
            PriorityDto.MEDIUM -> TaskPriority.MEDIUM
            PriorityDto.HIGH -> TaskPriority.HIGH
            PriorityDto.CRITICAL -> TaskPriority.CRITICAL
        }
    }

    /**
     * Обратный маппинг из DTO статуса в доменный
     */
    fun mapToDomainStatus(statusDto: StatusDto): TaskStatus {
        return when (statusDto) {
            StatusDto.TODO -> TaskStatus.TODO
            StatusDto.IN_PROGRESS -> TaskStatus.IN_PROGRESS
            StatusDto.BLOCKED -> TaskStatus.BLOCKED
            StatusDto.COMPLETED -> TaskStatus.COMPLETED
            StatusDto.ARCHIVED -> TaskStatus.ARCHIVED
        }
    }
}