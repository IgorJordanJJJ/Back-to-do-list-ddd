package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

import java.util.UUID

/**
 * Объект-значение: Идентификатор уведомления
 */
@JvmInline
value class NotificationId private constructor(val value: UUID) {
    companion object {
        fun create(): NotificationId = NotificationId(UUID.randomUUID())
        fun from(id: UUID): NotificationId = NotificationId(id)
        fun from(id: String): NotificationId = NotificationId(UUID.fromString(id))
    }

    override fun toString(): String = value.toString()
}