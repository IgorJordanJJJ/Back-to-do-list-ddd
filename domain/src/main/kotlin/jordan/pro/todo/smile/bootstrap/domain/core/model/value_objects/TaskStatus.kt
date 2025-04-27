package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Статус задачи
 */
enum class TaskStatus(val value: String, val label: String) {
    TODO("TODO", "К выполнению"),
    IN_PROGRESS("IN_PROGRESS", "В процессе"),
    BLOCKED("BLOCKED", "Заблокировано"),
    COMPLETED("COMPLETED", "Выполнено"),
    ARCHIVED("ARCHIVED", "В архиве");

    companion object {
        fun fromValue(value: String): TaskStatus {
            return values().find { it.value == value }
                ?: throw IllegalArgumentException("Неизвестное значение статуса: $value")
        }
    }

    fun isCompleted(): Boolean = this == COMPLETED || this == ARCHIVED
}