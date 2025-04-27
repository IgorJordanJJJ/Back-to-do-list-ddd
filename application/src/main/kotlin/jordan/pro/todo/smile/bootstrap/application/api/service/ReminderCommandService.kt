package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.ReminderDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetReminderByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTaskRemindersQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserRemindersQuery

/**
 * Сервис для работы с напоминаниями - command часть
 */
interface ReminderCommandService {
    /**
     * Создать новое напоминание
     */
    fun createReminder(command: CreateReminderCommand): ReminderDto

    /**
     * Обновить напоминание
     */
    fun updateReminder(command: UpdateReminderCommand): ReminderDto

    /**
     * Удалить напоминание
     */
    fun deleteReminder(command: DeleteReminderCommand)

    /**
     * Отметить напоминание как отправленное
     */
    fun markReminderAsSent(reminderId: String): ReminderDto
}

/**
 * Сервис для работы с напоминаниями - query часть
 */
interface ReminderQueryService {
    /**
     * Получить напоминание по ID
     */
    fun handle(query: GetReminderByIdQuery): ReminderDto

    /**
     * Получить напоминания для задачи
     */
    fun handle(query: GetTaskRemindersQuery): List<ReminderDto>

    /**
     * Получить напоминания пользователя
     */
    fun handle(query: GetUserRemindersQuery): List<ReminderDto>

    /**
     * Получить напоминания, которые нужно отправить в указанный момент времени
     */
    fun getRemindersToSend(): List<ReminderDto>
}