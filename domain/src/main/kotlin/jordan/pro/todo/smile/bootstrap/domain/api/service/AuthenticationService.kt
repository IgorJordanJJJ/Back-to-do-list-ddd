package jordan.pro.todo.smile.bootstrap.domain.api.service

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.UserToken
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email

/**
 * Доменный сервис для аутентификации пользователей
 */
interface AuthenticationService {
    /**
     * Зарегистрировать нового пользователя
     */
    fun registerUser(name: String, email: String, password: String): User

    /**
     * Аутентифицировать пользователя по email и паролю
     */
    fun authenticate(email: String, password: String): UserToken?

    /**
     * Проверить валидность токена
     */
    fun validateToken(token: String): Boolean

    /**
     * Отозвать токен
     */
    fun revokeToken(token: String)

    /**
     * Отозвать все токены пользователя
     */
    fun revokeAllUserTokens(email: Email)

    /**
     * Изменить пароль пользователя
     */
    fun changePassword(email: Email, currentPassword: String, newPassword: String): Boolean
}
