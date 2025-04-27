package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Описание
 */
@JvmInline
value class Description private constructor(val value: String) {
    companion object {
        fun create(value: String): Description {
            require(value.length <= 5000) { "Описание не может превышать 5000 символов" }
            return Description(value)
        }

        fun empty(): Description = Description("")
    }

    override fun toString(): String = value
}