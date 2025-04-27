// OutboxMessageProcessor.kt
package jordan.pro.todo.smile.bootstrap.infrastructure.messaging.impl.processor

import jordan.pro.todo.smile.bootstrap.application.api.command.MarkMessageAsFailedCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MarkMessageAsProcessedCommand
import jordan.pro.todo.smile.bootstrap.application.api.service.MessageOutboxService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Компонент для обработки отложенных сообщений из очереди
 */
@Component
class OutboxMessageProcessor(
    private val messageOutboxService: MessageOutboxService,
    private val rabbitTemplate: RabbitTemplate
) {
    private val logger = LoggerFactory.getLogger(OutboxMessageProcessor::class.java)

    /**
     * Периодически обрабатывает сообщения из очереди и отправляет их в RabbitMQ
     */
    @Scheduled(fixedDelayString = "\${todo-service.outbox.process-interval-ms:5000}")
    fun processMessages() {
        logger.debug("Начало обработки сообщений из очереди...")

        try {
            // Получаем сообщения для обработки (по умолчанию 10)
            val messages = messageOutboxService.getPendingMessages()

            if (messages.isEmpty()) {
                return
            }

            logger.info("Найдено {} сообщений для обработки", messages.size)

            // Обрабатываем каждое сообщение
            messages.forEach { message ->
                try {
                    // Отправляем в RabbitMQ
                    rabbitTemplate.convertAndSend(
                        "todo-service.events", // exchange
                        message.routingKey,    // routing key
                        message.payload        // payload
                    )

                    // Отмечаем как обработанное
                    messageOutboxService.markAsProcessed(
                        MarkMessageAsProcessedCommand(message.id)
                    )

                    logger.debug("Сообщение {} успешно обработано", message.id)
                } catch (e: Exception) {
                    logger.error("Ошибка при обработке сообщения {}: {}", message.id, e.message, e)

                    // Отмечаем как неудачное
                    messageOutboxService.markAsFailed(
                        MarkMessageAsFailedCommand(message.id)
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Общая ошибка при обработке сообщений из очереди: {}", e.message, e)
        }
    }

    /**
     * Ежедневно удаляет старые обработанные сообщения
     */
    @Scheduled(cron = "0 0 1 * * ?") // Каждый день в 1:00
    fun cleanupOldMessages() {
        logger.info("Начало очистки старых обработанных сообщений...")

        try {
            val olderThanDays = 30 // Настройте по необходимости
            val count = messageOutboxService.cleanupProcessedMessages(olderThanDays)

            logger.info("Удалено {} старых обработанных сообщений", count)
        } catch (e: Exception) {
            logger.error("Ошибка при очистке старых сообщений: {}", e.message, e)
        }
    }
}