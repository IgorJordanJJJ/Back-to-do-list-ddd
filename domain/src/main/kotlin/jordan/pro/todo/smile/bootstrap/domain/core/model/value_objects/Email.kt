package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Электронная почта
 */
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

        fun create(value: String): Email {
            require(value.isNotBlank()) { "Email не может быть пустым" }
            require(EMAIL_REGEX.matches(value)) { "Некорректный формат email" }
            return Email(value)
        }
    }

    override fun toString(): String = value
}