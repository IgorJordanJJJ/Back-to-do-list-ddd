package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Хранилище доменных событий
 */
interface EventStore {
    /**
     * Сохранить событие
     */
    fun save(event: DomainEvent)

    /**
     * Сохранить несколько событий
     */
    fun saveAll(events: Collection<DomainEvent>)

    /**
     * Получить события для списка задач
     */
    fun getEventsForTodoList(todoListId: TodoListId): List<DomainEvent>

    /**
     * Получить события для задачи
     */
    fun getEventsForTask(taskId: TaskId): List<DomainEvent>

    /**
     * Получить события, связанные с пользователем
     */
    fun getEventsForUser(userId: UserId): List<DomainEvent>

    /**
     * Получить события за период времени
     */
    fun getEventsInTimeRange(startTime: LocalDateTime, endTime: LocalDateTime): List<DomainEvent>

    /**
     * Получить события определенного типа
     */
    fun getEventsByType(eventType: Class<out DomainEvent>): List<DomainEvent>
}