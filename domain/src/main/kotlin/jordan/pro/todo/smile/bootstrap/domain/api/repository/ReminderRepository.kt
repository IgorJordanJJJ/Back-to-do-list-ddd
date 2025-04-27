package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Reminder
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Репозиторий для работы с напоминаниями
 */
interface ReminderRepository {
    /**
     * Найти напоминание по ID
     */
    fun findById(id: ReminderId): Reminder?

    /**
     * Найти напоминания по ID задачи и ID пользователя
     */
    fun findByTaskIdAndUserId(taskId: TaskId, userId: UserId): List<Reminder>

    /**
     * Найти напоминания пользователя
     */
    fun findByUserId(userId: UserId): List<Reminder>

    /**
     * Найти напоминания пользователя по статусу отправки
     */
    fun findByUserIdAndIsSent(userId: UserId, isSent: Boolean): List<Reminder>

    /**
     * Найти напоминания, которые нужно отправить
     */
    fun findByReminderTimeBeforeAndIsSent(reminderTime: LocalDateTime, isSent: Boolean): List<Reminder>

    /**
     * Сохранить напоминание
     */
    fun save(reminder: Reminder): Reminder

    /**
     * Удалить напоминание
     */
    fun delete(id: ReminderId)

    /**
     * Удалить напоминания по ID задачи
     */
    fun deleteByTaskId(taskId: TaskId)

    /**
     * Удалить напоминания по ID пользователя
     */
    fun deleteByUserId(userId: UserId)
}