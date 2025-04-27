package jordan.pro.todo.smile.bootstrap.domain.api.service

import jordan.pro.todo.smile.bootstrap.domain.core.event.DomainEvent

/**
 * Интерфейс для публикации доменных событий
 */
interface DomainEventPublisher {
    /**
     * Опубликовать доменное событие
     */
    fun publish(event: DomainEvent)

    /**
     * Опубликовать несколько доменных событий
     */
    fun publishAll(events: Collection<DomainEvent>)
}