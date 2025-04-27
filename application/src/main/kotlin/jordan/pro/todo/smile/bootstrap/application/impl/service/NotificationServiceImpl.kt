package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateNotificationCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkAllNotificationsAsReadCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkNotificationAsReadCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.NotificationDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.NotificationType
import jordan.pro.todo.smile.bootstrap.application.api.query.GetNotificationByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserNotificationsQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.NotificationPageDto
import jordan.pro.todo.smile.bootstrap.application.api.service.NotificationService
import jordan.pro.todo.smile.bootstrap.domain.api.repository.NotificationRepository
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Notification
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.NotificationId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository
) : NotificationService {

    @Transactional
    override fun createNotification(command: CreateNotificationCommand): NotificationDto {
        val userId = UserId.from(command.userId)
        val type = jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.NotificationType.valueOf(command.type)

        // Создание нового уведомления
        val notification = Notification.create(
            userId = userId,
            type = type,
            message = command.message,
            relatedEntityId = command.relatedEntityId
        )

        // Сохранение уведомления
        val savedNotification = notificationRepository.save(notification)

        // Возврат DTO уведомления
        return toDto(savedNotification)
    }

    @Transactional
    override fun markAsRead(command: MarkNotificationAsReadCommand): NotificationDto {
        val notificationId = NotificationId.from(command.notificationId)
        val userId = UserId.from(command.userId)

        // Поиск уведомления
        val notification = notificationRepository.findById(notificationId)
            ?: throw IllegalArgumentException("Уведомление с ID ${command.notificationId} не найдено")

        // Проверка владельца уведомления
        if (notification.userId != userId) {
            throw UserNotAuthorizedException(userId, "mark notification ${notificationId} as read")
        }

        // Отметка уведомления как прочитанного
        notification.markAsRead()

        // Сохранение изменений
        val updatedNotification = notificationRepository.save(notification)

        // Возврат DTO уведомления
        return toDto(updatedNotification)
    }

    @Transactional
    override fun markAllAsRead(command: MarkAllNotificationsAsReadCommand): Int {
        val userId = UserId.from(command.userId)

        // Отметка всех уведомлений как прочитанных
        notificationRepository.markAllAsRead(userId)

        // Возврат количества непрочитанных уведомлений (теперь должно быть 0)
        return notificationRepository.findUnreadByUserId(userId).size
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetNotificationByIdQuery): NotificationDto {
        val notificationId = NotificationId.from(query.notificationId)
        val userId = UserId.from(query.userId)

        // Поиск уведомления
        val notification = notificationRepository.findById(notificationId)
            ?: throw IllegalArgumentException("Уведомление с ID ${query.notificationId} не найдено")

        // Проверка владельца уведомления
        if (notification.userId != userId) {
            throw UserNotAuthorizedException(userId, "view notification ${notificationId}")
        }

        // Возврат DTO уведомления
        return toDto(notification)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUserNotificationsQuery): NotificationPageDto {
        val userId = UserId.from(query.userId)

        // Получение уведомлений в зависимости от параметров
        val notifications = when {
            query.onlyUnread && query.since != null -> {
                notificationRepository.findUnreadByUserId(userId)
                    .filter { it.createdAt.isAfter(query.since) }
            }
            query.onlyUnread -> {
                notificationRepository.findUnreadByUserId(userId)
            }
            query.since != null -> {
                notificationRepository.findByUserIdAndCreatedAfter(userId, query.since)
            }
            else -> {
                notificationRepository.findByUserId(userId)
            }
        }

        // Сортировка уведомлений (сначала непрочитанные, затем по дате создания)
        val sortedNotifications = notifications.sortedWith(
            compareByDescending<Notification> { !it.isRead }
                .thenByDescending { it.createdAt }
        )

        // Применение пагинации
        val totalItems = sortedNotifications.size
        val totalPages = (totalItems + query.size - 1) / query.size
        val startIndex = query.page * query.size
        val endIndex = minOf(startIndex + query.size, totalItems)

        val pagedItems = if (startIndex < totalItems) {
            sortedNotifications.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // Подсчет непрочитанных уведомлений
        val unreadCount = notifications.count { !it.isRead }

        // Возврат результата
        return NotificationPageDto(
            items = pagedItems.map { toDto(it) },
            totalItems = totalItems,
            totalPages = totalPages,
            currentPage = query.page,
            unreadCount = unreadCount
        )
    }

    /**
     * Преобразование доменной модели в DTO
     */
    private fun toDto(notification: Notification): NotificationDto {
        return NotificationDto(
            id = notification.id.toString(),
            userId = notification.userId.toString(),
            type = NotificationType.valueOf(notification.type.name),
            message = notification.message,
            relatedEntityId = notification.relatedEntityId,
            isRead = notification.isRead,
            createdAt = notification.createdAt
        )
    }
}