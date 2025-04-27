package jordan.pro.todo.smile.bootstrap.domain.core.model.aggregates

import jordan.pro.todo.smile.bootstrap.domain.core.exception.TaskNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Task
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Description
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.DueDate
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskPriority
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskStatus
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Title
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import java.time.LocalDateTime

/**
 * Агрегат: Список задач
 *
 * TodoList является корневым агрегатом, который
 * инкапсулирует задачи и обеспечивает их согласованность
 */
class TodoList private constructor(
    val id: TodoListId,
    var title: Title,
    var description: Description,
    val tasks: MutableSet<Task>,
    val ownerId: UserId,
    val sharedWith: MutableSet<UserId>,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            title: Title,
            description: Description,
            ownerId: UserId
        ): TodoList {
            val now = LocalDateTime.now()
            return TodoList(
                id = TodoListId.create(),
                title = title,
                description = description,
                tasks = mutableSetOf(),
                ownerId = ownerId,
                sharedWith = mutableSetOf(),
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: TodoListId,
            title: Title,
            description: Description,
            tasks: MutableSet<Task>,
            ownerId: UserId,
            sharedWith: MutableSet<UserId>,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): TodoList {
            return TodoList(
                id, title, description, tasks, ownerId,
                sharedWith, createdAt, updatedAt
            )
        }
    }

    fun update(title: Title, description: Description) {
        this.title = title
        this.description = description
        this.updatedAt = LocalDateTime.now()
    }

    fun addTask(
        title: Title,
        description: Description,
        priority: TaskPriority,
        dueDate: DueDate?,
        createdBy: UserId
    ): Task {
        val task = Task.create(
            todoListId = this.id,
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate,
            createdBy = createdBy
        )

        tasks.add(task)
        this.updatedAt = LocalDateTime.now()
        return task
    }

    fun removeTask(taskId: TaskId) {
        val task = findTaskById(taskId)
        tasks.remove(task)
        this.updatedAt = LocalDateTime.now()
    }

    fun getTask(taskId: TaskId): Task {
        return findTaskById(taskId)
    }

    fun updateTask(
        taskId: TaskId,
        title: Title,
        description: Description,
        priority: TaskPriority,
        status: TaskStatus,
        dueDate: DueDate?
    ) {
        val task = findTaskById(taskId)

        task.update(
            title = title,
            description = description,
            priority = priority,
            status = status,
            dueDate = dueDate
        )

        this.updatedAt = LocalDateTime.now()
    }

    fun completeTask(taskId: TaskId) {
        val task = findTaskById(taskId)
        task.markAsCompleted()
        this.updatedAt = LocalDateTime.now()
    }

    fun addTaskTag(taskId: TaskId, tag: jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag) {
        val task = findTaskById(taskId)
        task.addTag(tag)
        this.updatedAt = LocalDateTime.now()
    }

    fun removeTaskTag(taskId: TaskId, tag: jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag) {
        val task = findTaskById(taskId)
        task.removeTag(tag)
        this.updatedAt = LocalDateTime.now()
    }

    fun assignTask(taskId: TaskId, userId: UserId) {
        requireUserAccess(userId)

        val task = findTaskById(taskId)
        task.assignTo(userId)
        this.updatedAt = LocalDateTime.now()
    }

    fun shareWith(userId: UserId) {
        if (userId != ownerId && !sharedWith.contains(userId)) {
            sharedWith.add(userId)
            this.updatedAt = LocalDateTime.now()
        }
    }

    fun unshareWith(userId: UserId) {
        if (sharedWith.contains(userId)) {
            sharedWith.remove(userId)

            // Отменить все назначения задач для этого пользователя
            tasks.forEach { task ->
                if (task.assignedTo == userId) {
                    task.unassign()
                }
            }

            this.updatedAt = LocalDateTime.now()
        }
    }

    fun hasAccess(userId: UserId): Boolean {
        return userId == ownerId || sharedWith.contains(userId)
    }

    fun isOwner(userId: UserId): Boolean {
        return userId == ownerId
    }

    fun countTasksByStatus(status: TaskStatus): Int {
        return tasks.count { it.status == status }
    }

    fun countOverdueTasks(): Int {
        return tasks.count { it.isOverdue() }
    }

    private fun findTaskById(taskId: TaskId): Task {
        return tasks.find { it.id == taskId }
            ?: throw TaskNotFoundException(taskId)
    }

    private fun requireUserAccess(userId: UserId) {
        if (!hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, id)
        }
    }
}