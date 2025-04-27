package jordan.pro.todo.smile.bootstrap.application.api.query

import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto

/**
 * Запрос: Валидировать токен
 */
data class ValidateTokenQuery(
    val token: String
) {
    interface Handler {
        fun handle(query: ValidateTokenQuery): Boolean
    }
}

/**
 * Запрос: Получить пользователя по токену
 */
data class GetUserByTokenQuery(
    val token: String
) {
    interface Handler {
        fun handle(query: GetUserByTokenQuery): UserDto?
    }
}

/**
 * Запрос: Получить пользователя по email
 */
data class GetUserByEmailQuery(
    val email: String
) {
    interface Handler {
        fun handle(query: GetUserByEmailQuery): UserDto?
    }
}