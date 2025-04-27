package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

import java.util.UUID

/**
 * Объект-значение: Идентификатор пользователя
 */
@JvmInline
value class UserId private constructor(val value: UUID) {
    companion object {
        fun create(): UserId = UserId(UUID.randomUUID())
        fun from(id: UUID): UserId = UserId(id)
        fun from(id: String): UserId = UserId(UUID.fromString(id))
    }

    override fun toString(): String = value.toString()
}