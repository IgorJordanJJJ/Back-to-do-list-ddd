package jordan.pro.todo.smile.bootstrap.domain.core.event

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Description
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.DueDate
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskPriority
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskStatus
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Title
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Событие: Задача обновлена
 */
data class TaskUpdatedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val taskId: TaskId,
    val todoListId: TodoListId,
    val title: Title,
    val description: Description,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: DueDate?,
    val updatedBy: UserId
) : DomainEvent