package jordan.pro.todo.smile.bootstrap.domain.core.event

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime
import java.util.*

/**
 * Событие: Задача завершена
 */
data class TaskCompletedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val taskId: TaskId,
    val todoListId: TodoListId,
    val completedBy: UserId
) : jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent