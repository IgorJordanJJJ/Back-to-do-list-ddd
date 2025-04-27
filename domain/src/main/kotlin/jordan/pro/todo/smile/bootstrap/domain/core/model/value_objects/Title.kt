package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Название
 */
@JvmInline
value class Title private constructor(val value: String) {
    companion object {
        fun create(value: String): Title {
            require(value.isNotBlank()) { "Название не может быть пустым" }
            require(value.length <= 255) { "Название не может превышать 255 символов" }
            return Title(value)
        }
    }

    override fun toString(): String = value
}