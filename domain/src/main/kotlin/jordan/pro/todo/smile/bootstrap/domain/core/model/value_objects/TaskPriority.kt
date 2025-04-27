package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Приоритет задачи
 */
enum class TaskPriority(val value: Int, val label: String) {
    LOW(1, "Низкий"),
    MEDIUM(2, "Средний"),
    HIGH(3, "Высокий"),
    CRITICAL(4, "Критический");

    companion object {
        fun fromValue(value: Int): TaskPriority {
            return values().find { it.value == value }
                ?: throw IllegalArgumentException("Неизвестное значение приоритета: $value")
        }
    }
}