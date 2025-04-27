package jordan.pro.todo.smile.bootstrap.application.api.query

import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto

/**
 * Запрос: Получить пользователя по ID
 */
data class GetUserByIdQuery(
    val userId: String
) {
    interface Handler {
        fun handle(query: GetUserByIdQuery): UserDto
    }
}

/**
 * Запрос: Поиск пользователей
 */
data class SearchUsersQuery(
    val query: String,
    val page: Int = 0,
    val size: Int = 20
) {
    interface Handler {
        fun handle(query: SearchUsersQuery): UserSearchResultDto
    }
}

/**
 * Результат поиска пользователей
 */
data class UserSearchResultDto(
    val items: List<UserDto>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int
)