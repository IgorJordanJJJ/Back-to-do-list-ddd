package jordan.pro.todo.smile.bootstrap.domain.core.event

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.UserName
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime
import java.util.UUID

/**
 * Событие: Пользователь зарегистрирован
 */
data class UserRegisteredEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    val userId: UserId,
    val name: UserName,
    val email: Email
) : DomainEvent