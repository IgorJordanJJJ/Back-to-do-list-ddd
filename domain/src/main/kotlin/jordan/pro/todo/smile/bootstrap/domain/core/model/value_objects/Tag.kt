package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Метка (тег)
 */
@JvmInline
value class Tag private constructor(val value: String) {
    companion object {
        fun create(value: String): jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag {
            require(value.isNotBlank()) { "Метка не может быть пустой" }
            require(value.length <= 30) { "Метка не может превышать 30 символов" }
            require(!value.contains(" ")) { "Метка не может содержать пробелы" }
            return jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag(value.lowercase())
        }
    }

    override fun toString(): String = value
}