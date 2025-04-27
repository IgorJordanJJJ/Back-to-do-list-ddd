package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

/**
 * Объект-значение: Идентификатор напоминания
 */
@JvmInline
value class ReminderId private constructor(val value: Long) {
    companion object {
        fun create(): ReminderId = ReminderId(System.currentTimeMillis())
        fun from(id: Long): ReminderId = ReminderId(id)
        fun from(id: String): ReminderId = ReminderId(id.toLong())
    }

    override fun toString(): String = value.toString()
}