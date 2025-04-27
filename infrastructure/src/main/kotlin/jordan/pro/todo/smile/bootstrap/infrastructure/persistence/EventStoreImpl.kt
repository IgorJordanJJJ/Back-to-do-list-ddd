package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.EventStore
import jordan.pro.todo.smile.bootstrap.domain.core.event.*
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.DomainEventEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.DomainEventJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Repository
@Transactional
open class EventStoreImpl(
    private val domainEventJpaRepository: DomainEventJpaRepository,
    private val domainEventEntityMapper: DomainEventEntityMapper
) : EventStore {

    private val logger = LoggerFactory.getLogger(EventStoreImpl::class.java)

    override fun save(event: DomainEvent) {
        try {
            val (entityId, entityType, userId) = extractEntityInfo(event)
            val entity = domainEventEntityMapper.toEntity(event, entityId, entityType, userId)
            domainEventJpaRepository.save(entity)
        } catch (e: Exception) {
            logger.error("Failed to save event: {}", event, e)
        }
    }

    override fun saveAll(events: Collection<DomainEvent>) {
        val entities = events.mapNotNull {
            try {
                val (entityId, entityType, userId) = extractEntityInfo(it)
                domainEventEntityMapper.toEntity(it, entityId, entityType, userId)
            } catch (e: Exception) {
                logger.error("Failed to create entity for event: {}", it, e)
                null
            }
        }
        domainEventJpaRepository.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun getEventsForTodoList(todoListId: TodoListId): List<DomainEvent> {
        val entities = domainEventJpaRepository.findByEntityIdAndEntityType(
            todoListId.toString(),
            "TodoList"
        )
        return entities.mapNotNull { domainEventEntityMapper.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    override fun getEventsForTask(taskId: TaskId): List<DomainEvent> {
        val entities = domainEventJpaRepository.findByEntityIdAndEntityType(
            taskId.toString(),
            "Task"
        )
        return entities.mapNotNull { domainEventEntityMapper.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    override fun getEventsForUser(userId: UserId): List<DomainEvent> {
        val entities = domainEventJpaRepository.findByUserId(userId.value)
        return entities.mapNotNull { domainEventEntityMapper.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    override fun getEventsInTimeRange(startTime: LocalDateTime, endTime: LocalDateTime): List<DomainEvent> {
        val entities = domainEventJpaRepository.findByOccurredOnBetween(startTime, endTime)
        return entities.mapNotNull { domainEventEntityMapper.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    override fun getEventsByType(eventType: Class<out DomainEvent>): List<DomainEvent> {
        val entities = domainEventJpaRepository.findByEventType(eventType.simpleName)
        return entities.mapNotNull { domainEventEntityMapper.fromEntity(it) }
    }

    private fun extractEntityInfo(event: DomainEvent): Triple<String, String, UUID?> {
        // Improved implementation to extract entity information based on event type
        return when (event) {
            // TodoList events
            is TodoListCreatedEvent -> Triple(event.todoListId.toString(), "TodoList", event.ownerId.value)
            is TodoListUpdatedEvent -> Triple(event.todoListId.toString(), "TodoList", null)
            is TodoListDeletedEvent -> Triple(event.todoListId.toString(), "TodoList", null)
            is TodoListSharedEvent -> Triple(event.todoListId.toString(), "TodoList", event.sharedByUserId.value)
            is TodoListUnsharedEvent -> Triple(event.todoListId.toString(), "TodoList", event.revokedByUserId.value)

            // Task events
            is TaskCreatedEvent -> Triple(event.taskId.toString(), "Task", event.createdBy.value)
            is TaskUpdatedEvent -> Triple(event.taskId.toString(), "Task", event.updatedBy.value)
            is TaskDeletedEvent -> Triple(event.taskId.toString(), "Task", event.deletedBy.value)
            is TaskCompletedEvent -> Triple(event.taskId.toString(), "Task", event.completedBy.value)
            is TaskAssignedEvent -> Triple(event.taskId.toString(), "Task", event.assignedBy.value)
            is TaskUnassignedEvent -> Triple(event.taskId.toString(), "Task", event.unassignedBy.value)
            is TaskTagAddedEvent -> Triple(event.taskId.toString(), "Task", event.addedBy.value)
            is TaskTagRemovedEvent -> Triple(event.taskId.toString(), "Task", event.removedBy.value)
            is TaskMovedEvent -> Triple(event.oldTaskId.toString(), "Task", event.movedBy.value)

            // Default case for any other events
            else -> {
                logger.warn("Unknown event type: {}", event.javaClass.simpleName)
                Triple(event.eventId.toString(), "Unknown", null)
            }
        }
    }
}