package jordan.pro.todo.smile.bootstrap.domain.api.service

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

/**
 * Доменный сервис для управления доступом к спискам задач
 */
interface TodoListAccessService {
    /**
     * Проверить доступ пользователя к списку задач
     */
    fun checkAccess(userId: UserId, todoListId: TodoListId): Boolean

    /**
     * Проверить права владельца на список задач
     */
    fun checkOwnership(userId: UserId, todoListId: TodoListId): Boolean

    /**
     * Предоставить доступ пользователю к списку задач
     */
    fun grantAccess(todoListId: TodoListId, userId: UserId, grantedBy: UserId)

    /**
     * Отозвать доступ пользователя к списку задач
     */
    fun revokeAccess(todoListId: TodoListId, userId: UserId, revokedBy: UserId)

    /**
     * Получить всех пользователей, имеющих доступ к списку задач
     */
    fun getUsersWithAccess(todoListId: TodoListId): List<UserId>
}