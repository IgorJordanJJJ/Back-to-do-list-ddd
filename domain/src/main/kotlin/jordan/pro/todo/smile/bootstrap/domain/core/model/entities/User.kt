package jordan.pro.todo.smile.bootstrap.domain.core.model.entities

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.UserName
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Сущность: Пользователь системы
 */
class User private constructor(
    val id: UserId,
    var name: UserName,
    val email: Email,
    var passwordHash: String,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(name: UserName, email: Email, passwordHash: String): User {
            val now = LocalDateTime.now()
            return User(
                id = UserId.create(),
                name = name,
                email = email,
                passwordHash = passwordHash,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: UserId,
            name: UserName,
            email: Email,
            passwordHash: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): User {
            return User(id, name, email, passwordHash, createdAt, updatedAt)
        }
    }

    fun updateName(name: UserName) {
        this.name = name
        this.updatedAt = LocalDateTime.now()
    }

    fun updatePassword(newPasswordHash: String) {
        this.passwordHash = newPasswordHash
        this.updatedAt = LocalDateTime.now()
    }
}
