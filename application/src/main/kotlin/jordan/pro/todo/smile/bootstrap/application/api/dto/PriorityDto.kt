package jordan.pro.todo.smile.bootstrap.application.api.dto

/**
 * DTO для приоритета задачи
 */
enum class PriorityDto(val value: Int, val label: String) {
    LOW(1, "Низкий"),
    MEDIUM(2, "Средний"),
    HIGH(3, "Высокий"),
    CRITICAL(4, "Критический")
}