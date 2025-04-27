package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.aggregates.TodoList
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

/**
 * Репозиторий для работы со списками задач
 */
interface TodoListRepository {
    /**
     * Найти список задач по идентификатору
     */
    fun findById(id: TodoListId): TodoList?

    /**
     * Найти все списки задач пользователя
     */
    fun findByUserId(userId: UserId): List<TodoList>

    /**
     * Найти списки задач с общим доступом для пользователя
     */
    fun findSharedWithUser(userId: UserId): List<TodoList>

    /**
     * Сохранить список задач
     */
    fun save(todoList: TodoList): TodoList

    /**
     * Удалить список задач
     */
    fun delete(id: TodoListId)

    /**
     * Проверить существование списка задач
     */
    fun exists(id: TodoListId): Boolean

    /**
     * Посчитать количество списков задач пользователя
     */
    fun countByOwnerId(ownerId: UserId): Int

    /**
     * Получить все списки задач пользователя (включая те, к которым он имеет доступ)
     */
    fun findAllAccessibleByUser(userId: UserId): List<TodoList>

    /**
     * Поиск списков задач по части названия
     */
    fun searchByTitle(userId: UserId, titleQuery: String): List<TodoList>
}