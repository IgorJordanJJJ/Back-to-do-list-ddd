package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Task
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Репозиторий для работы с задачами
 */
interface TaskRepository {
    /**
     * Найти задачу по ID
     */
    fun findById(id: TaskId): Task?

    /**
     * Найти задачи по ID списка задач
     */
    fun findByTodoListId(todoListId: TodoListId): List<Task>

    /**
     * Найти просроченные задачи (срок выполнения прошёл, но статус не "выполнено")
     */
    fun findOverdueTasks(): List<Task>

    /**
     * Найти задачи, назначенные пользователю с указанным статусом
     */
    fun findByAssignedToAndStatus(userId: UserId, status: String): List<Task>

    /**
     * Найти предстоящие задачи для пользователя в указанном диапазоне дат
     */
    fun findUpcomingTasksByUser(userId: UserId, startDate: LocalDateTime, endDate: LocalDateTime): List<Task>

    /**
     * Найти все задачи, доступные пользователю (включая общие доступы)
     */
    fun findAllAccessibleByUser(userId: UserId): List<Task>

    /**
     * Сохранить задачу
     */
    fun save(task: Task): Task

    /**
     * Сохранить несколько задач
     */
    fun saveAll(tasks: Collection<Task>): List<Task>

    /**
     * Удалить задачу
     */
    fun delete(id: TaskId)

    /**
     * Удалить задачи по ID списка задач
     */
    fun deleteByTodoListId(todoListId: TodoListId)
}