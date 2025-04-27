package jordan.pro.todo.smile.bootstrap.domain.core.model.entities

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TokenId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Сущность: Токен пользователя для аутентификации
 */
class UserToken private constructor(
    val id: TokenId,
    val userId: UserId,
    val token: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    var isRevoked: Boolean
) {
    companion object {
        fun create(userId: UserId, token: String, expiresInMinutes: Long): UserToken {
            val now = LocalDateTime.now()
            return UserToken(
                id = TokenId.create(),
                userId = userId,
                token = token,
                createdAt = now,
                expiresAt = now.plusMinutes(expiresInMinutes),
                isRevoked = false
            )
        }

        fun reconstitute(
            id: TokenId,
            userId: UserId,
            token: String,
            createdAt: LocalDateTime,
            expiresAt: LocalDateTime,
            isRevoked: Boolean
        ): UserToken {
            return UserToken(id, userId, token, createdAt, expiresAt, isRevoked)
        }
    }

    fun revoke() {
        isRevoked = true
    }

    fun isValid(): Boolean {
        val now = LocalDateTime.now()
        return !isRevoked && now.isBefore(expiresAt)
    }

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
}