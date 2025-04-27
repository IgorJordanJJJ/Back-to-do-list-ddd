package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateNotificationCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkAllNotificationsAsReadCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkNotificationAsReadCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.NotificationDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetNotificationByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserNotificationsQuery

/**
 * Сервис для работы с уведомлениями
 */
interface NotificationService :
    GetNotificationByIdQuery.Handler,
    GetUserNotificationsQuery.Handler {

    /**
     * Создать уведомление
     */
    fun createNotification(command: CreateNotificationCommand): NotificationDto

    /**
     * Отметить уведомление как прочитанное
     */
    fun markAsRead(command: MarkNotificationAsReadCommand): NotificationDto

    /**
     * Отметить все уведомления пользователя как прочитанные
     */
    fun markAllAsRead(command: MarkAllNotificationsAsReadCommand): Int
}