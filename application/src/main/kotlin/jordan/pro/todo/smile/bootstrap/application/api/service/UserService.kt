package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.UserDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchUsersQuery

/**
 * Сервис для работы с пользователями - command часть
 */
interface UserCommandService {
    /**
     * Создать нового пользователя
     */
    fun createUser(command: CreateUserCommand): UserDto

    /**
     * Обновить данные пользователя
     */
    fun updateUser(command: UpdateUserCommand): UserDto
}

/**
 * Сервис для работы с пользователями - query часть
 */
interface UserQueryService :
    GetUserByIdQuery.Handler,
    SearchUsersQuery.Handler