package jordan.pro.todo.smile.bootstrap.ports.rest_api.controller

import jakarta.validation.Valid
import jordan.pro.todo.smile.bootstrap.application.api.command.CreateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.service.UserCommandService
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.infrastructure.security.api.service.SecurityService
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.CreateUserRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.LoginRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.ErrorResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.TokenResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.UserResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.mapper.RestMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * Контроллер для аутентификации
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userCommandService: UserCommandService,
    private val userRepository: UserRepository,
    private val securityService: SecurityService,
    private val restMapper: RestMapper
) {

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        val command = CreateUserCommand(
            name = request.name,
            email = request.email
        )

        val user = userCommandService.createUser(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toUserResponse(user))
    }

    /**
     * Аутентификация пользователя и выдача JWT токена
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
        val email = Email.create(request.email)

        // В упрощенной версии мы просто проверяем существование пользователя по email
        // В реальном приложении здесь была бы проверка пароля
        val user = userRepository.findByEmail(email)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse(
                    status = HttpStatus.UNAUTHORIZED.value(),
                    message = "Неверный email или пароль",
                    timestamp = LocalDateTime.now()
                )
            )

        // Генерируем JWT токен
        val token = securityService.generateToken(user.id)

        return ResponseEntity.ok(TokenResponse(token = token, userId = user.id.toString()))
    }
}