package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.UserTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface UserTokenJpaRepository : JpaRepository<UserTokenEntity, Long> {
    /**
     * Найти токен по значению токена
     */
    fun findByToken(token: String): UserTokenEntity?

    /**
     * Найти все токены пользователя
     */
    fun findByUserId(userId: UUID): List<UserTokenEntity>

    /**
     * Найти активные токены пользователя, срок действия которых еще не истек
     */
    fun findByUserIdAndIsRevokedFalseAndExpiresAtAfter(userId: UUID, currentTime: LocalDateTime): List<UserTokenEntity>

    /**
     * Удалить просроченные токены
     */
    @Modifying
    @Query("DELETE FROM UserTokenEntity t WHERE t.expiresAt < ?1")
    fun deleteByExpiresAtBefore(expirationDate: LocalDateTime): Int
}