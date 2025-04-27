package jordan.pro.todo.smile.bootstrap.infrastructure.messaging.impl.schedule

import jordan.pro.todo.smile.bootstrap.application.api.dto.ReminderDto
import jordan.pro.todo.smile.bootstrap.application.api.service.ReminderCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.ReminderQueryService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Компонент для выполнения запланированных задач
 */
@Component
class ScheduledTasks(
    private val reminderQueryService: ReminderQueryService,
    private val reminderCommandService: ReminderCommandService
) {

    private val logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

    /**
     * Проверка напоминаний, которые нужно отправить
     */
    @Scheduled(fixedRateString = "\${todo-service.task-reminder.check-interval-seconds:60}000")
    fun checkReminders() {
        logger.debug("Checking reminders to send...")

        try {
            val remindersToSend = reminderQueryService.getRemindersToSend()
            logger.info("Found {} reminders to send", remindersToSend.size)

            for (reminder in remindersToSend) {
                processReminder(reminder)
            }
        } catch (e: Exception) {
            logger.error("Error while checking reminders", e)
        }
    }

    private fun processReminder(reminder: ReminderDto) {
        try {
            // В реальном приложении здесь был бы код для отправки напоминания
            // например, через email, push-уведомление и т.д.
            logger.info("Sending reminder: {}", reminder)

            // Отмечаем напоминание как отправленное
            reminderCommandService.markReminderAsSent(reminder.id)

            logger.info("Reminder {} successfully sent", reminder.id)
        } catch (e: Exception) {
            logger.error("Error while processing reminder {}", reminder.id, e)
        }
    }
}