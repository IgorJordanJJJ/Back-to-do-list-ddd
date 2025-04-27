package jordan.pro.todo.smile.bootstrap.application.api.dto

/**
 * DTO для изменения пароля
 */
data class ChangePasswordDto(
    val oldPassword: String,
    val newPassword: String
)