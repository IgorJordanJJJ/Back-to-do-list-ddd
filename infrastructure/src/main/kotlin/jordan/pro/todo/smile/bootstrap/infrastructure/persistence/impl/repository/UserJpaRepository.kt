package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, UUID> {
    /**
     * Найти пользователя по email
     */
    fun findByEmail(email: String): UserEntity?

    /**
     * Проверить существование пользователя по email
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Поиск пользователей по части имени или email
     */
    fun findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        nameQuery: String,
        emailQuery: String
    ): List<UserEntity>
}