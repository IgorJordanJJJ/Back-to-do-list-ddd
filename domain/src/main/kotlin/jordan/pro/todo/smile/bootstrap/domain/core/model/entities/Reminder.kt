package jordan.pro.todo.smile.bootstrap.domain.core.model.entities

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Сущность: Напоминание о задаче
 */
class Reminder private constructor(
    val id: ReminderId,
    val taskId: TaskId,
    val userId: UserId,
    var reminderTime: LocalDateTime,
    var isSent: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(
            taskId: TaskId,
            userId: UserId,
            reminderTime: LocalDateTime
        ): Reminder {
            require(reminderTime.isAfter(LocalDateTime.now())) { "Время напоминания должно быть в будущем" }

            val now = LocalDateTime.now()
            return Reminder(
                id = ReminderId.create(),
                taskId = taskId,
                userId = userId,
                reminderTime = reminderTime,
                isSent = false,
                createdAt = now
            )
        }

        fun reconstitute(
            id: ReminderId,
            taskId: TaskId,
            userId: UserId,
            reminderTime: LocalDateTime,
            isSent: Boolean,
            createdAt: LocalDateTime
        ): Reminder {
            return Reminder(id, taskId, userId, reminderTime, isSent, createdAt)
        }
    }

    fun updateReminderTime(newReminderTime: LocalDateTime) {
        require(newReminderTime.isAfter(LocalDateTime.now())) { "Время напоминания должно быть в будущем" }
        this.reminderTime = newReminderTime
        this.isSent = false
    }

    fun markAsSent() {
        this.isSent = true
    }

    fun isOverdue(): Boolean {
        return !isSent && reminderTime.isBefore(LocalDateTime.now())
    }
}