package jordan.pro.todo.smile.bootstrap.ports.rest_api.mapper

import jordan.pro.todo.smile.bootstrap.application.api.dto.TaskDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto
import jordan.pro.todo.smile.bootstrap.application.api.query.UserSearchResultDto
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.TaskResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.TodoListResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.UserResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.UserSearchResultResponse
import org.springframework.stereotype.Component

@Component
class RestMapper {
    /**
     * Преобразование DTO в ответ REST API для списка задач
     */
    fun toResponse(todoListDto: TodoListDto): TodoListResponse {
        return TodoListResponse(
            id = todoListDto.id,
            title = todoListDto.title,
            description = todoListDto.description,
            tasks = todoListDto.tasks.map { toTaskResponse(it) },
            owner = UserResponse(id = todoListDto.ownerId),
            sharedWith = todoListDto.sharedWith.map { UserResponse(id = it) },
            createdAt = todoListDto.createdAt,
            updatedAt = todoListDto.updatedAt,
            totalTasks = todoListDto.taskStats.total,
            completedTasks = todoListDto.taskStats.completed,
            overdueTasks = todoListDto.taskStats.overdue
        )
    }

    /**
     * Преобразование DTO в ответ REST API для задачи
     */
    fun toTaskResponse(taskDto: TaskDto): TaskResponse {
        return TaskResponse(
            id = taskDto.id,
            title = taskDto.title,
            description = taskDto.description,
            priority = taskDto.priority,
            status = taskDto.status,
            dueDate = taskDto.dueDate,
            tags = taskDto.tags,
            isOverdue = taskDto.isOverdue,
            createdBy = UserResponse(id = taskDto.createdBy),
            assignedTo = taskDto.assignedTo?.let { UserResponse(id = it) },
            createdAt = taskDto.createdAt,
            updatedAt = taskDto.updatedAt,
            completedAt = taskDto.completedAt
        )
    }

    /**
     * Преобразование UserDto в UserResponse
     */
    fun toUserResponse(userDto: UserDto): UserResponse {
        return UserResponse(
            id = userDto.id,
            name = userDto.name,
            email = userDto.email
        )
    }

    /**
     * Преобразование результатов поиска пользователей
     */
    fun toUserSearchResultResponse(searchResult: UserSearchResultDto): UserSearchResultResponse {
        return UserSearchResultResponse(
            items = searchResult.items.map { toUserResponse(it) },
            totalItems = searchResult.totalItems,
            totalPages = searchResult.totalPages,
            currentPage = searchResult.currentPage
        )
    }
}