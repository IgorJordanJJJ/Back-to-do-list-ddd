package jordan.pro.todo.smile.bootstrap.domain.core.event

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Событие: Пользователь вошел в систему
 */
data class UserLoggedInEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val userId: UserId,
    val ipAddress: String
) : DomainEvent