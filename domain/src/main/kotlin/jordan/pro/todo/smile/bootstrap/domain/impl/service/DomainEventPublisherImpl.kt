package jordan.pro.todo.smile.bootstrap.domain.impl.service

import jordan.pro.todo.smile.bootstrap.domain.api.repository.EventStore
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

/**
 * Реализация публикатора доменных событий
 */
@Service
class DomainEventPublisherImpl(
    private val eventStore: EventStore,
    private val applicationEventPublisher: ApplicationEventPublisher
) : DomainEventPublisher {

    private val logger = LoggerFactory.getLogger(DomainEventPublisherImpl::class.java)

    override fun publish(event: DomainEvent) {
        try {
            // Сохраняем событие в EventStore
            eventStore.save(event)

            // Публикуем событие в Spring ApplicationEventPublisher
            applicationEventPublisher.publishEvent(event)

            logger.debug("Published event: {}", event)
        } catch (e: Exception) {
            logger.error("Failed to publish event: {}", event, e)
        }
    }

    override fun publishAll(events: Collection<DomainEvent>) {
        events.forEach { publish(it) }
    }
}