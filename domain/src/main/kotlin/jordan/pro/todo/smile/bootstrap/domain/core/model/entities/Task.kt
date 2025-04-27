package jordan.pro.todo.smile.bootstrap.domain.core.model.entities

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Title
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Description
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskPriority
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.TaskStatus
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.DueDate
import java.time.LocalDateTime

/**
 * Сущность: Задача в списке задач
 */
class Task private constructor(
    val id: TaskId,
    val todoListId: TodoListId,
    var title: Title,
    var description: Description,
    var priority: TaskPriority,
    var status: TaskStatus,
    var dueDate: DueDate?,
    val tags: MutableSet<jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag>,
    val createdBy: UserId,
    var assignedTo: UserId?,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var completedAt: LocalDateTime?
) {
    companion object {
        fun create(
            todoListId: TodoListId,
            title: Title,
            description: Description,
            priority: TaskPriority,
            dueDate: DueDate?,
            createdBy: UserId
        ): Task {
            val now = LocalDateTime.now()
            return Task(
                id = TaskId.create(),
                todoListId = todoListId,
                title = title,
                description = description,
                priority = priority,
                status = TaskStatus.TODO,
                dueDate = dueDate,
                tags = mutableSetOf(),
                createdBy = createdBy,
                assignedTo = null,
                createdAt = now,
                updatedAt = now,
                completedAt = null
            )
        }

        fun reconstitute(
            id: TaskId,
            todoListId: TodoListId,
            title: Title,
            description: Description,
            priority: TaskPriority,
            status: TaskStatus,
            dueDate: DueDate?,
            tags: MutableSet<jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag>,
            createdBy: UserId,
            assignedTo: UserId?,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
            completedAt: LocalDateTime?
        ): Task {
            return Task(
                id, todoListId, title, description, priority, status, dueDate,
                tags, createdBy, assignedTo, createdAt, updatedAt, completedAt
            )
        }
    }

    fun update(
        title: Title,
        description: Description,
        priority: TaskPriority,
        status: TaskStatus,
        dueDate: DueDate?
    ) {
        this.title = title
        this.description = description
        this.priority = priority

        // Если статус изменился на "Выполнено"
        if (!this.status.isCompleted() && status.isCompleted()) {
            this.completedAt = LocalDateTime.now()
        } else if (this.status.isCompleted() && !status.isCompleted()) {
            // Если задача была выполнена, а теперь снова активна
            this.completedAt = null
        }

        this.status = status
        this.dueDate = dueDate
        this.updatedAt = LocalDateTime.now()
    }

    fun markAsCompleted() {
        if (!status.isCompleted()) {
            this.status = TaskStatus.COMPLETED
            this.completedAt = LocalDateTime.now()
            this.updatedAt = LocalDateTime.now()
        }
    }

    fun addTag(tag: jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag) {
        this.tags.add(tag)
        this.updatedAt = LocalDateTime.now()
    }

    fun removeTag(tag: jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Tag) {
        this.tags.remove(tag)
        this.updatedAt = LocalDateTime.now()
    }

    fun assignTo(userId: UserId) {
        this.assignedTo = userId
        this.updatedAt = LocalDateTime.now()
    }

    fun unassign() {
        this.assignedTo = null
        this.updatedAt = LocalDateTime.now()
    }

    fun isOverdue(): Boolean {
        return !status.isCompleted() && dueDate?.isOverdue() == true
    }
}