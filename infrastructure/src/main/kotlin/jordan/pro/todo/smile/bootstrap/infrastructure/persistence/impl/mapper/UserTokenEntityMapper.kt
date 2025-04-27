package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.UserToken
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TokenId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.UserTokenEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class UserTokenEntityMapper(
    private val userJpaRepository: UserJpaRepository
) {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(token: UserToken): UserTokenEntity {
        val userEntity = userJpaRepository.findById(token.userId.value)
            .orElseThrow { IllegalStateException("Не найден пользователь с ID ${token.userId}") }

        return UserTokenEntity(
            id = token.id.value,
            user = userEntity,
            token = token.token,
            createdAt = token.createdAt,
            expiresAt = token.expiresAt,
            isRevoked = token.isRevoked
        )
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: UserTokenEntity): UserToken {
        return UserToken.reconstitute(
            id = TokenId.from(entity.id!!),
            userId = UserId.from(entity.user.id),
            token = entity.token,
            createdAt = entity.createdAt,
            expiresAt = entity.expiresAt,
            isRevoked = entity.isRevoked
        )
    }
}