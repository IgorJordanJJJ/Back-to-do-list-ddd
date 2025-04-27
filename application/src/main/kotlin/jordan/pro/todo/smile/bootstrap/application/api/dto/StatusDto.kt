package jordan.pro.todo.smile.bootstrap.application.api.dto

/**
 * DTO для статуса задачи
 */
enum class StatusDto(val value: String, val label: String) {
    TODO("TODO", "К выполнению"),
    IN_PROGRESS("IN_PROGRESS", "В процессе"),
    BLOCKED("BLOCKED", "Заблокировано"),
    COMPLETED("COMPLETED", "Выполнено"),
    ARCHIVED("ARCHIVED", "В архиве")
}