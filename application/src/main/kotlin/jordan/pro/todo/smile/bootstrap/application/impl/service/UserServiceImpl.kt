// UserServiceImpl.kt
package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchUsersQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.UserSearchResultDto
import jordan.pro.todo.smile.bootstrap.application.api.service.UserCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.UserQueryService
import jordan.pro.todo.smile.bootstrap.application.impl.mapper.UserMapper
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.UserName
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    @Autowired private val passwordEncoder: PasswordEncoder
) : UserCommandService, UserQueryService {

    @Transactional
    override fun createUser(command: CreateUserCommand): UserDto {
        val email = Email.create(command.email)

        // Проверяем, что пользователя с таким email не существует
        require(!userRepository.existsByEmail(email)) { "Пользователь с email ${command.email} уже существует" }

        // Создаем нового пользователя с пустым паролем
        val passwordHash = passwordEncoder.encode("defaultPassword") // временный пароль

        val user = User.create(
            name = UserName.create(command.name),
            email = email,
            passwordHash = passwordHash
        )

        // Сохраняем в репозитории
        val savedUser = userRepository.save(user)

        // Преобразуем и возвращаем DTO
        return userMapper.toDto(savedUser)
    }

    @Transactional
    override fun updateUser(command: UpdateUserCommand): UserDto {
        val userId = UserId.from(command.id)

        // Получаем пользователя из репозитория
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Пользователь с ID ${command.id} не найден")

        // Обновляем имя пользователя
        user.updateName(UserName.create(command.name))

        // Сохраняем в репозитории
        val updatedUser = userRepository.save(user)

        // Преобразуем и возвращаем DTO
        return userMapper.toDto(updatedUser)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUserByIdQuery): UserDto {
        val userId = UserId.from(query.userId)

        // Получаем пользователя из репозитория
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Пользователь с ID ${query.userId} не найден")

        // Преобразуем и возвращаем DTO
        return userMapper.toDto(user)
    }

    @Transactional(readOnly = true)
    override fun handle(query: SearchUsersQuery): UserSearchResultDto {
        // Ищем пользователей по имени или email
        val users = userRepository.searchByNameOrEmail(query.query)

        // Применяем пагинацию (в реальном коде это должно быть реализовано в репозитории)
        val totalItems = users.size
        val totalPages = (totalItems + query.size - 1) / query.size
        val startIndex = query.page * query.size
        val endIndex = minOf(startIndex + query.size, totalItems)

        val pagedItems = if (startIndex < totalItems) {
            users.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // Преобразуем и возвращаем результат
        return UserSearchResultDto(
            items = pagedItems.map { userMapper.toDto(it) },
            totalItems = totalItems,
            totalPages = totalPages,
            currentPage = query.page
        )
    }
}