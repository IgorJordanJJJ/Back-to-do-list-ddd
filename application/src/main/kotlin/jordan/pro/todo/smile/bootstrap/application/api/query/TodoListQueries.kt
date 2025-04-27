package jordan.pro.todo.smile.bootstrap.application.api.query

import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListSummaryDto

/**
 * Запрос: Получить список задач по ID
 */
data class GetTodoListQuery(
    val todoListId: String,
    val userId: String
) {
    interface Handler {
        fun handle(query: GetTodoListQuery): TodoListDto
    }
}

/**
 * Запрос: Получить все списки задач пользователя
 */
data class GetUserTodoListsQuery(
    val userId: String,
    val includeShared: Boolean = true
) {
    interface Handler {
        fun handle(query: GetUserTodoListsQuery): List<TodoListSummaryDto>
    }
}

/**
 * Запрос: Поиск списков задач
 */
data class SearchTodoListsQuery(
    val userId: String,
    val searchTerm: String,
    val page: Int = 0,
    val size: Int = 20
) {
    interface Handler {
        fun handle(query: SearchTodoListsQuery): TodoListSearchResult
    }
}

/**
 * Результат поиска списков задач
 */
data class TodoListSearchResult(
    val items: List<TodoListSummaryDto>,
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int
)