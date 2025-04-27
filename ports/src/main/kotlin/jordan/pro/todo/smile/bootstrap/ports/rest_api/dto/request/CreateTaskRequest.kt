package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * REST API запрос: создание задачи
 */
data class CreateTaskRequest(
    @field:NotBlank(message = "Название не может быть пустым")
    @field:Size(max = 255, message = "Название не может превышать 255 символов")
    val title: String,

    @field:Size(max = 5000, message = "Описание не может превышать 5000 символов")
    val description: String = "",

    val priority: String = "MEDIUM",

    val dueDate: String? = null,

    val tags: Set<String> = emptySet()
)
