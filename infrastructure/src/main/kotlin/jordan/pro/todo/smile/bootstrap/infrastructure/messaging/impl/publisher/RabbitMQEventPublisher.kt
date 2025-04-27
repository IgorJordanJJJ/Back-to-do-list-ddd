package jordan.pro.todo.smile.bootstrap.infrastructure.messaging.impl.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderCreatedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TaskCreatedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListCreatedEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
open class RabbitMQEventPublisher(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) : DomainEventPublisher {

    companion object {
        private const val EXCHANGE_NAME = "todo-service.events"
    }

    override fun publish(event: DomainEvent) {
        val routingKey = getRoutingKeyForEvent(event)

        try {
            val message = objectMapper.writeValueAsString(event)
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, message)
        } catch (e: Exception) {
            // В реальном приложении здесь должна быть обработка ошибок,
            // например, запись в журнал ошибок или повторная отправка
            throw RuntimeException("Ошибка при публикации события ${event.javaClass.simpleName}: ${e.message}", e)
        }
    }

    override fun publishAll(events: Collection<DomainEvent>) {
        events.forEach { publish(it) }
    }

    private fun getRoutingKeyForEvent(event: DomainEvent): String {
        return when (event) {
            // TodoList events
            is TodoListCreatedEvent -> "event.todolist.created"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListUpdatedEvent -> "event.todolist.updated"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListDeletedEvent -> "event.todolist.deleted"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListSharedEvent -> "event.todolist.shared"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListUnsharedEvent -> "event.todolist.unshared"

            // Task events
            is TaskCreatedEvent -> "event.task.created"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskUpdatedEvent -> "event.task.updated"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskDeletedEvent -> "event.task.deleted"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskCompletedEvent -> "event.task.completed"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskAssignedEvent -> "event.task.assigned"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskUnassignedEvent -> "event.task.unassigned"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskTagAddedEvent -> "event.task.tag.added"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskTagRemovedEvent -> "event.task.tag.removed"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.TaskMovedEvent -> "event.task.moved"

            // Reminder events
            is ReminderCreatedEvent -> "event.reminder.created"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderUpdatedEvent -> "event.reminder.updated"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderDeletedEvent -> "event.reminder.deleted"
            is jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderSentEvent -> "event.reminder.sent"

            // Default case for any other events
            else -> "event.unknown.${event.javaClass.simpleName.toLowerCase()}"
        }
    }
}