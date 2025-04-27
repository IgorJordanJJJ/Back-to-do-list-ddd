package jordan.pro.todo.smile.bootstrap.domain.core.event

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Событие: Напоминание удалено
 */
data class ReminderDeletedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val reminderId: ReminderId,
    val taskId: TaskId,
    val userId: UserId
) : DomainEvent