package jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response

/**
 * REST API ответ: результат поиска пользователей
 */
data class UserSearchResultResponse(
    val items: List<UserResponse>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int
)