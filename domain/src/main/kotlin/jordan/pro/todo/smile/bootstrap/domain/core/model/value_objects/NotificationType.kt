package jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects

/**
 * Объект-значение: Тип уведомления
 */
enum class NotificationType(val value: String, val description: String) {
    TASK_ASSIGNED("TASK_ASSIGNED", "Задача назначена"),
    TASK_COMPLETED("TASK_COMPLETED", "Задача выполнена"),
    TASK_DUE_SOON("TASK_DUE_SOON", "Скоро дедлайн задачи"),
    TASK_OVERDUE("TASK_OVERDUE", "Задача просрочена"),
    TODOLIST_SHARED("TODOLIST_SHARED", "Предоставлен доступ к списку задач"),
    TODOLIST_UNSHARED("TODOLIST_UNSHARED", "Отозван доступ к списку задач"),
    SYSTEM("SYSTEM", "Системное уведомление");

    companion object {
        fun fromValue(value: String): NotificationType {
            return values().find { it.value == value }
                ?: throw IllegalArgumentException("Неизвестный тип уведомления: $value")
        }
    }
}