package jordan.pro.todo.smile.bootstrap.infrastructure.security.api.service

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

interface SecurityService {
    /**
     * Генерировать JWT-токен для пользователя
     */
    fun generateToken(userId: UserId): String

    /**
     * Проверить валидность токена
     */
    fun validateToken(token: String): Boolean

    /**
     * Извлечь идентификатор пользователя из токена
     */
    fun getUserIdFromToken(token: String): UserId
}