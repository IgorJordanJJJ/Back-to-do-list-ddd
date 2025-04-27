package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Имя пользователя
 */
@JvmInline
value class UserName private constructor(val value: String) {
    companion object {
        fun create(value: String): UserName {
            require(value.isNotBlank()) { "Имя пользователя не может быть пустым" }
            require(value.length >= 3) { "Имя пользователя должно содержать не менее 3 символов" }
            return UserName(value)
        }
    }

    override fun toString(): String = value
}