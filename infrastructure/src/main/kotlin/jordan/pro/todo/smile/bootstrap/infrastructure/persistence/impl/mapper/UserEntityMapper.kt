package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.UserName
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class UserEntityMapper {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id.value,
            name = user.name.toString(),
            email = user.email.toString(),
            passwordHash = user.passwordHash ?: "", // Предполагаем, что в доменной модели добавили passwordHash
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: UserEntity): User {
        return User.reconstitute(
            id = UserId.from(entity.id),
            name = UserName.create(entity.name),
            email = Email.create(entity.email),
            passwordHash = entity.passwordHash,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}