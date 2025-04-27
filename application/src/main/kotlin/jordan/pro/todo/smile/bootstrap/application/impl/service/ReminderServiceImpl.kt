package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.ReminderDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetReminderByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTaskRemindersQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserRemindersQuery
import jordan.pro.todo.smile.bootstrap.application.api.service.ReminderCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.ReminderQueryService
import jordan.pro.todo.smile.bootstrap.domain.api.repository.ReminderRepository
import jordan.pro.todo.smile.bootstrap.domain.api.repository.TaskRepository
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderCreatedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderDeletedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.ReminderUpdatedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.exception.ReminderNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TaskNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Reminder
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
open class ReminderServiceImpl(
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    @Qualifier("domainEventPublisherImpl") private val eventPublisher: DomainEventPublisher
) : ReminderCommandService, ReminderQueryService {

    @Transactional
    override fun createReminder(command: CreateReminderCommand): ReminderDto {
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.userId)

        // Проверяем существование задачи
        val task = taskRepository.findById(taskId)
            ?: throw TaskNotFoundException(taskId)

        // Создаем новое напоминание
        val reminder = Reminder.create(
            taskId = taskId,
            userId = userId,
            reminderTime = command.reminderTime
        )

        // Сохраняем в репозитории
        val savedReminder = reminderRepository.save(reminder)

        // Публикуем событие
        val event = ReminderCreatedEvent(
            reminderId = savedReminder.id,
            taskId = savedReminder.taskId,
            userId = savedReminder.userId,
            reminderTime = savedReminder.reminderTime
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return toDto(savedReminder)
    }

    @Transactional
    override fun updateReminder(command: UpdateReminderCommand): ReminderDto {
        val reminderId = ReminderId.from(command.reminderId)
        val userId = UserId.from(command.userId)

        // Получаем напоминание из репозитория
        val reminder = reminderRepository.findById(reminderId)
            ?: throw ReminderNotFoundException(reminderId)

        // Проверяем, что напоминание принадлежит пользователю
        if (reminder.userId != userId) {
            throw UserNotAuthorizedException(userId, "update reminder ${reminder.id}")
        }

        // Обновляем напоминание
        reminder.updateReminderTime(command.reminderTime)

        // Сохраняем в репозитории
        val updatedReminder = reminderRepository.save(reminder)

        // Публикуем событие
        val event = ReminderUpdatedEvent(
            reminderId = updatedReminder.id,
            taskId = updatedReminder.taskId,
            userId = updatedReminder.userId,
            reminderTime = updatedReminder.reminderTime
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return toDto(updatedReminder)
    }

    @Transactional
    override fun deleteReminder(command: DeleteReminderCommand) {
        val reminderId = ReminderId.from(command.reminderId)
        val userId = UserId.from(command.userId)

        // Получаем напоминание из репозитория
        val reminder = reminderRepository.findById(reminderId)
            ?: throw ReminderNotFoundException(reminderId)

        // Проверяем, что напоминание принадлежит пользователю
        if (reminder.userId != userId) {
            throw UserNotAuthorizedException(userId, "delete reminder ${reminder.id}")
        }

        // Удаляем из репозитория
        reminderRepository.delete(reminderId)

        // Публикуем событие
        val event = ReminderDeletedEvent(
            reminderId = reminder.id,
            taskId = reminder.taskId,
            userId = reminder.userId
        )
        eventPublisher.publish(event)
    }

    @Transactional
    override fun markReminderAsSent(reminderId: String): ReminderDto {
        val id = ReminderId.from(reminderId)

        // Получаем напоминание из репозитория
        val reminder = reminderRepository.findById(id)
            ?: throw ReminderNotFoundException(id)

        // Отмечаем напоминание как отправленное
        reminder.markAsSent()

        // Сохраняем в репозитории
        val updatedReminder = reminderRepository.save(reminder)

        // Преобразуем и возвращаем DTO
        return toDto(updatedReminder)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetReminderByIdQuery): ReminderDto {
        val reminderId = ReminderId.from(query.reminderId)
        val userId = UserId.from(query.userId)

        // Получаем напоминание из репозитория
        val reminder = reminderRepository.findById(reminderId)
            ?: throw ReminderNotFoundException(reminderId)

        // Проверяем, что напоминание принадлежит пользователю
        if (reminder.userId != userId) {
            throw UserNotAuthorizedException(userId, "view reminder ${reminder.id}")
        }

        // Преобразуем и возвращаем DTO
        return toDto(reminder)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetTaskRemindersQuery): List<ReminderDto> {
        val taskId = TaskId.from(query.taskId)
        val userId = UserId.from(query.userId)

        // Получаем напоминания из репозитория
        val reminders = reminderRepository.findByTaskIdAndUserId(taskId, userId)

        // Преобразуем и возвращаем список DTO
        return reminders.map { toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUserRemindersQuery): List<ReminderDto> {
        val userId = UserId.from(query.userId)

        // Получаем напоминания из репозитория
        val reminders = if (query.includeCompleted) {
            reminderRepository.findByUserId(userId)
        } else {
            reminderRepository.findByUserIdAndIsSent(userId, false)
        }

        // Преобразуем и возвращаем список DTO
        return reminders.map { toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getRemindersToSend(): List<ReminderDto> {
        val now = LocalDateTime.now()
        val reminders = reminderRepository.findByReminderTimeBeforeAndIsSent(now, false)

        // Преобразуем и возвращаем список DTO
        return reminders.map { toDto(it) }
    }

    private fun toDto(reminder: Reminder): ReminderDto {
        return ReminderDto(
            id = reminder.id.toString(),
            taskId = reminder.taskId.toString(),
            userId = reminder.userId.toString(),
            reminderTime = reminder.reminderTime,
            isSent = reminder.isSent,
            createdAt = reminder.createdAt
        )
    }
}