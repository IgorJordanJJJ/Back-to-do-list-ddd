package jordan.pro.todo.smile.bootstrap.domain.core.event

import java.time.LocalDateTime
import java.util.UUID

/**
 * Базовый интерфейс для всех доменных событий
 */
interface DomainEvent {
    val eventId: UUID
    val occurredOn: LocalDateTime
}