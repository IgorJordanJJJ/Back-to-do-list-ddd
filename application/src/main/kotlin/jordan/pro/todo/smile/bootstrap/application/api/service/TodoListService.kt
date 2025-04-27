package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.ShareTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UnshareTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTodoListQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserTodoListsQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchTodoListsQuery

/**
 * Сервис для работы со списками задач - command часть
 */
interface TodoListCommandService {
    /**
     * Создать новый список задач
     */
    fun createTodoList(command: CreateTodoListCommand): TodoListDto

    /**
     * Обновить список задач
     */
    fun updateTodoList(command: UpdateTodoListCommand): TodoListDto

    /**
     * Удалить список задач
     */
    fun deleteTodoList(command: DeleteTodoListCommand)

    /**
     * Предоставить доступ к списку задач другому пользователю
     */
    fun shareTodoList(command: ShareTodoListCommand): TodoListDto

    /**
     * Отозвать доступ к списку задач у пользователя
     */
    fun unshareTodoList(command: UnshareTodoListCommand): TodoListDto
}

/**
 * Сервис для работы со списками задач - query часть
 */
interface TodoListQueryService :
    GetTodoListQuery.Handler,
    GetUserTodoListsQuery.Handler,
    SearchTodoListsQuery.Handler