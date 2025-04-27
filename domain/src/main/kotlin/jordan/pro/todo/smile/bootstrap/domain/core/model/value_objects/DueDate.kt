package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

import java.time.LocalDateTime

/**
 * Объект-значение: Срок выполнения
 */
data class DueDate(val value: LocalDateTime) {
    companion object {
        fun create(dateTime: LocalDateTime): DueDate {
            require(dateTime.isAfter(LocalDateTime.now().minusMinutes(1))) {
                "Срок выполнения не может быть в прошлом"
            }
            return DueDate(dateTime)
        }
    }

    fun isOverdue(): Boolean = value.isBefore(LocalDateTime.now())
}