package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.ChangePasswordCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.LoginUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.LogoutUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.RegisterUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.AuthenticationResponseDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByEmailQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByTokenQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.ValidateTokenQuery
import jordan.pro.todo.smile.bootstrap.application.api.service.AuthenticationService
import jordan.pro.todo.smile.bootstrap.application.impl.mapper.UserMapper
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserTokenRepository
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.core.event.UserLoggedInEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.UserRegisteredEvent
import jordan.pro.todo.smile.bootstrap.domain.core.exception.AuthenticationException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.EmailAlreadyExistsException
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.UserToken
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.UserName
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
open class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val userTokenRepository: UserTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userMapper: UserMapper,
    @Qualifier("domainEventPublisherImpl") private val eventPublisher: DomainEventPublisher,
    @Value("\${todo-service.security.jwt.expiration-minutes:60}") private val tokenExpirationMinutes: Long
) : AuthenticationService {

    @Transactional
    override fun registerUser(command: RegisterUserCommand): UserDto {
        val email = Email.create(command.email)

        // Проверка, что пользователь с таким email не существует
        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyExistsException(email)
        }

        // Хеширование пароля
        val passwordHash = passwordEncoder.encode(command.password)

        // Создание нового пользователя
        val user = User.create(
            name = UserName.create(command.name),
            email = email,
            passwordHash = passwordHash
        )

        // Сохранение пользователя
        val savedUser = userRepository.save(user)

        // Публикация события о регистрации
        val event = UserRegisteredEvent(
            userId = savedUser.id,
            name = savedUser.name,
            email = savedUser.email
        )
        eventPublisher.publish(event)

        // Возврат DTO пользователя
        return userMapper.toDto(savedUser)
    }

    @Transactional
    override fun login(command: LoginUserCommand): AuthenticationResponseDto {
        val email = Email.create(command.email)

        // Поиск пользователя по email
        val user = userRepository.findByEmail(email)
            ?: throw AuthenticationException("Пользователь с email ${command.email} не найден")

        // Проверка пароля
        if (!passwordEncoder.matches(command.password, user.passwordHash)) {
            throw AuthenticationException("Неверный пароль")
        }

        // Отзыв предыдущих токенов при необходимости
        // Здесь можно добавить логику по ограничению количества одновременных сессий

        // Генерация нового токена
        val tokenString = generateToken()
        val token = UserToken.create(
            userId = user.id,
            token = tokenString,
            expiresInMinutes = tokenExpirationMinutes
        )

        // Сохранение токена
        val savedToken = userTokenRepository.save(token)

        // Публикация события о входе
        val event = UserLoggedInEvent(
            userId = user.id,
            ipAddress = command.ipAddress ?: "unknown"
        )
        eventPublisher.publish(event)

        // Возврат DTO ответа аутентификации
        return AuthenticationResponseDto(
            userId = user.id.toString(),
            token = savedToken.token,
            expiresAt = savedToken.expiresAt,
            name = user.name.toString(),
            email = user.email.toString()
        )
    }

    @Transactional
    override fun logout(command: LogoutUserCommand) {
        // Поиск токена
        val userId = UserId.from(command.userId)
        val activeTokens = userTokenRepository.findActiveByUserId(userId)

        // Отзыв токена
        activeTokens.filter { it.token == command.token }.forEach { token ->
            token.revoke()
            userTokenRepository.save(token)
        }
    }

    @Transactional
    override fun changePassword(command: ChangePasswordCommand): Boolean {
        val userId = UserId.from(command.userId)

        // Поиск пользователя
        val user = userRepository.findById(userId)
            ?: throw AuthenticationException("Пользователь не найден")

        // Проверка текущего пароля
        if (!passwordEncoder.matches(command.currentPassword, user.passwordHash)) {
            throw AuthenticationException("Неверный текущий пароль")
        }

        // Обновление пароля
        val newPasswordHash = passwordEncoder.encode(command.newPassword)
        user.updatePassword(newPasswordHash)

        // Сохранение пользователя
        userRepository.save(user)

        // Отзыв всех токенов для безопасности
        userTokenRepository.revokeAllByUserId(userId)

        return true
    }

    @Transactional(readOnly = true)
    override fun handle(query: ValidateTokenQuery): Boolean {
        val token = userTokenRepository.findByToken(query.token) ?: return false
        return token.isValid()
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUserByTokenQuery): UserDto? {
        // Поиск токена
        val token = userTokenRepository.findByToken(query.token) ?: return null

        // Проверка валидности токена
        if (!token.isValid()) {
            return null
        }

        // Поиск пользователя
        val user = userRepository.findById(token.userId) ?: return null

        // Возврат DTO пользователя
        return userMapper.toDto(user)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUserByEmailQuery): UserDto? {
        val email = Email.create(query.email)
        val user = userRepository.findByEmail(email) ?: return null
        return userMapper.toDto(user)
    }

    /**
     * Генерация уникального токена
     */
    private fun generateToken(): String {
        return UUID.randomUUID().toString()
    }
}