package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.UserToken
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TokenId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

/**
 * Репозиторий для работы с токенами пользователей
 */
interface UserTokenRepository {
    /**
     * Найти токен по ID
     */
    fun findById(id: TokenId): UserToken?

    /**
     * Найти токен по значению токена
     */
    fun findByToken(token: String): UserToken?

    /**
     * Найти все активные токены пользователя
     */
    fun findActiveByUserId(userId: UserId): List<UserToken>

    /**
     * Сохранить токен
     */
    fun save(token: UserToken): UserToken

    /**
     * Отозвать все токены пользователя
     */
    fun revokeAllByUserId(userId: UserId)

    /**
     * Удалить токен
     */
    fun delete(id: TokenId)

    /**
     * Удалить все просроченные токены
     */
    fun deleteExpiredTokens()
}
