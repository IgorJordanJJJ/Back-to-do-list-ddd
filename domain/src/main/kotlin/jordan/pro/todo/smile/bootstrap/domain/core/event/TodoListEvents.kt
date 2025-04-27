package jordan.pro.todo.smile.bootstrap.domain.core.event

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Description
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Title
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Событие: Создан новый список задач
 */
data class TodoListCreatedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val todoListId: TodoListId,
    val title: Title,
    val description: Description,
    val ownerId: UserId
) : DomainEvent

/**
 * Событие: Список задач обновлен
 */
data class TodoListUpdatedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val todoListId: TodoListId,
    val title: Title,
    val description: Description
) : DomainEvent

/**
 * Событие: Список задач удален
 */
data class TodoListDeletedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val todoListId: TodoListId
) : DomainEvent

/**
 * Событие: Предоставлен доступ к списку задач
 */
data class TodoListSharedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val todoListId: TodoListId,
    val sharedWithUserId: UserId,
    val sharedByUserId: UserId
) : DomainEvent

/**
 * Событие: Отозван доступ к списку задач
 */
data class TodoListUnsharedEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val todoListId: TodoListId,
    val revokedFromUserId: UserId,
    val revokedByUserId: UserId
) : DomainEvent