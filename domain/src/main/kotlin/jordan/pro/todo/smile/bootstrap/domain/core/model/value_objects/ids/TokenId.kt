package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids

/**
 * Объект-значение: Идентификатор токена
 */
@JvmInline
value class TokenId private constructor(val value: Long) {
    companion object {
        fun create(): TokenId = TokenId(System.currentTimeMillis())
        fun from(id: Long): TokenId = TokenId(id)
        fun from(id: String): TokenId = TokenId(id.toLong())
    }

    override fun toString(): String = value.toString()
}