package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

import java.util.UUID

/**
 * Объект-значение: Идентификатор задачи
 */
@JvmInline
value class TaskId private constructor(val value: UUID) {
    companion object {
        fun create(): TaskId = TaskId(UUID.randomUUID())
        fun from(id: UUID): TaskId = TaskId(id)
        fun from(id: String): TaskId = TaskId(UUID.fromString(id))
    }

    override fun toString(): String = value.toString()
}