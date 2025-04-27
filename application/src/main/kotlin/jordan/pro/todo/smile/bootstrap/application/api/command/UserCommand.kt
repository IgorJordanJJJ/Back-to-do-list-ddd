package jordan.pro.todo.smile.bootstrap.application.api.command

/**
 * Команда: Создать пользователя
 */
data class CreateUserCommand(
    val name: String,
    val email: String
)

/**
 * Команда: Обновить данные пользователя
 */
data class UpdateUserCommand(
    val id: String,
    val name: String
)

/**
 * Команда: Зарегистрировать нового пользователя
 */
data class RegisterUserCommand(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Команда: Аутентифицировать пользователя
 */
data class LoginUserCommand(
    val email: String,
    val password: String,
    val ipAddress: String? = null
)

/**
 * Команда: Выйти из системы (отозвать токен)
 */
data class LogoutUserCommand(
    val userId: String,
    val token: String
)

/**
 * Команда: Изменить пароль пользователя
 */
data class ChangePasswordCommand(
    val userId: String,
    val currentPassword: String,
    val newPassword: String
)