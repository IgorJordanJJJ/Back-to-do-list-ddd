package jordan.pro.todo.smile.bootstrap.infrastructure.messaging.impl.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import jordan.pro.todo.smile.bootstrap.application.api.command.EnqueueMessageCommand
import jordan.pro.todo.smile.bootstrap.application.api.service.MessageOutboxService
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

/**
 * Публикатор доменных событий с использованием паттерна Outbox для надежной доставки
 */
@Component
@Primary
class MessageOutboxEventPublisher(
    private val messageOutboxService: MessageOutboxService,
    private val directPublisher: RabbitMQEventPublisher,
    private val objectMapper: ObjectMapper
) : DomainEventPublisher {

    private val logger = LoggerFactory.getLogger(MessageOutboxEventPublisher::class.java)

    override fun publish(event: DomainEvent) {
        try {
            // Получаем маршрутный ключ для события
            val routingKey = getRoutingKeyForEvent(event)
            // Сериализуем событие в JSON
            val payload = objectMapper.writeValueAsString(event)

            // Создаем команду для добавления в очередь
            val command = EnqueueMessageCommand(
                eventId = event.eventId,
                eventType = event.javaClass.simpleName,
                payload = payload,
                routingKey = routingKey
            )

            // Сохраняем в очередь
            messageOutboxService.enqueueMessage(command)

        } catch (e: Exception) {
            logger.error("Ошибка при публикации события через outbox: ${e.message}", e)
            // Пытаемся опубликовать напрямую как резервный вариант
            directPublisher.publish(event)
        }
    }

    override fun publishAll(events: Collection<DomainEvent>) {
        events.forEach { publish(it) }
    }

    /**
     * Получение маршрутного ключа для события
     */
    private fun getRoutingKeyForEvent(event: DomainEvent): String {
        // Используем тот же метод определения ключа, что и в RabbitMQEventPublisher
        return when (val eventClassName = event.javaClass.simpleName) {
            // Примеры для TodoList событий
            "TodoListCreatedEvent" -> "event.todolist.created"
            "TodoListUpdatedEvent" -> "event.todolist.updated"
            "TodoListDeletedEvent" -> "event.todolist.deleted"
            "TodoListSharedEvent" -> "event.todolist.shared"
            "TodoListUnsharedEvent" -> "event.todolist.unshared"

            // Примеры для Task событий
            "TaskCreatedEvent" -> "event.task.created"
            "TaskUpdatedEvent" -> "event.task.updated"
            "TaskDeletedEvent" -> "event.task.deleted"
            "TaskCompletedEvent" -> "event.task.completed"
            "TaskAssignedEvent" -> "event.task.assigned"
            "TaskUnassignedEvent" -> "event.task.unassigned"

            // Примеры для User событий
            "UserRegisteredEvent" -> "event.user.registered"
            "UserLoggedInEvent" -> "event.user.login"

            // Напоминания
            "ReminderCreatedEvent" -> "event.reminder.created"
            "ReminderUpdatedEvent" -> "event.reminder.updated"
            "ReminderDeletedEvent" -> "event.reminder.deleted"
            "ReminderSentEvent" -> "event.reminder.sent"

            // По умолчанию
            else -> "event.unknown.${eventClassName.toLowerCase()}"
        }
    }
}