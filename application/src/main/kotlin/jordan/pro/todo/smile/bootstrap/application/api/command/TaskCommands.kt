package jordan.pro.todo.smile.bootstrap.application.api.command

import jordan.pro.todo.smile.bootstrap.application.api.dto.PriorityDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.StatusDto
import java.time.LocalDateTime

/**
 * Команда: Создать новую задачу
 */
data class CreateTaskCommand(
    val todoListId: String,
    val title: String,
    val description: String,
    val priority: PriorityDto,
    val dueDate: LocalDateTime?,
    val tags: Set<String>,
    val createdByUserId: String
)

/**
 * Команда: Обновить задачу
 */
data class UpdateTaskCommand(
    val todoListId: String,
    val taskId: String,
    val title: String,
    val description: String,
    val priority: PriorityDto,
    val status: StatusDto,
    val dueDate: LocalDateTime?,
    val tags: Set<String>,
    val updatedByUserId: String
)

/**
 * Команда: Удалить задачу
 */
data class DeleteTaskCommand(
    val todoListId: String,
    val taskId: String,
    val deletedByUserId: String
)

/**
 * Команда: Выполнить задачу
 */
data class CompleteTaskCommand(
    val todoListId: String,
    val taskId: String,
    val completedByUserId: String
)

/**
 * Команда: Назначить задачу пользователю
 */
data class AssignTaskCommand(
    val todoListId: String,
    val taskId: String,
    val assignToUserId: String,
    val assignedByUserId: String
)

/**
 * Команда: Отменить назначение задачи
 */
data class UnassignTaskCommand(
    val todoListId: String,
    val taskId: String,
    val unassignedByUserId: String
)

/**
 * Команда: Добавить метку к задаче
 */
data class AddTaskTagCommand(
    val todoListId: String,
    val taskId: String,
    val tag: String,
    val addedByUserId: String
)

/**
 * Команда: Удалить метку из задачи
 */
data class RemoveTaskTagCommand(
    val todoListId: String,
    val taskId: String,
    val tag: String,
    val removedByUserId: String
)

/**
 * Команда: Переместить задачу в другой список
 */
data class MoveTaskCommand(
    val sourceTodoListId: String,
    val targetTodoListId: String,
    val taskId: String,
    val movedByUserId: String
)