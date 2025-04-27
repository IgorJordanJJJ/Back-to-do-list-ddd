package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

/**
 * Объект-значение: Идентификатор сообщения в outbox
 */
@JvmInline
value class MessageId private constructor(val value: Long) {
    companion object {
        fun create(): MessageId = MessageId(System.currentTimeMillis())
        fun from(id: Long): MessageId = MessageId(id)
        fun from(id: String): MessageId = MessageId(id.toLong())
    }

    override fun toString(): String = value.toString()
}