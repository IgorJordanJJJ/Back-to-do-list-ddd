package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

/**
 * REST API запрос: создание напоминания
 */
data class CreateReminderRequest(
    @field:NotNull(message = "Время напоминания не может быть пустым")
    @field:FutureOrPresent(message = "Время напоминания должно быть в будущем")
    val reminderTime: LocalDateTime
)