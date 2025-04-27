package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.ChangePasswordCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.LoginUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.LogoutUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.RegisterUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.AuthenticationResponseDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByEmailQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByTokenQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.ValidateTokenQuery

/**
 * Сервис для аутентификации пользователей
 */
interface AuthenticationService :
    ValidateTokenQuery.Handler,
    GetUserByTokenQuery.Handler,
    GetUserByEmailQuery.Handler {

    /**
     * Регистрация нового пользователя
     */
    fun registerUser(command: RegisterUserCommand): UserDto

    /**
     * Вход в систему
     */
    fun login(command: LoginUserCommand): AuthenticationResponseDto

    /**
     * Выход из системы (отзыв токена)
     */
    fun logout(command: LogoutUserCommand)

    /**
     * Изменение пароля
     */
    fun changePassword(command: ChangePasswordCommand): Boolean
}