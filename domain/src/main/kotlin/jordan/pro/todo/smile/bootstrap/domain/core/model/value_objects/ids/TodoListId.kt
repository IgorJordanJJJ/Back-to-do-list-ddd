package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

import java.util.UUID

/**
 * Объект-значение: Идентификатор списка задач
 */
@JvmInline
value class TodoListId private constructor(val value: UUID) {
    companion object {
        fun create(): TodoListId = TodoListId(UUID.randomUUID())
        fun from(id: UUID): TodoListId = TodoListId(id)
        fun from(id: String): TodoListId = TodoListId(UUID.fromString(id))
    }

    override fun toString(): String = value.toString()
}