package jordan.pro.todo.smile.bootstrap.application.impl.mapper

import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserTokenRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val userTokenRepository: UserTokenRepository
) {
    /**
     * Преобразовать доменную модель User в DTO
     */
    fun toDto(user: User): UserDto {
        // Проверка наличия активных токенов
        val hasActiveTokens = userTokenRepository.findActiveByUserId(user.id).isNotEmpty()

        return UserDto(
            id = user.id.toString(),
            name = user.name.toString(),
            email = user.email.toString(),
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            hasActiveTokens = hasActiveTokens
        )
    }
}