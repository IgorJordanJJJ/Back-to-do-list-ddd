package jordan.pro.todo.smile.bootstrap.application.api.service

import jordan.pro.todo.smile.bootstrap.application.api.command.AddTaskTagCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.AssignTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.CompleteTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.CreateTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.MoveTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.RemoveTaskTagCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UnassignTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.TaskDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetAssignedTasksQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetOverdueTasksQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetRecentlyUpdatedTasksQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTaskQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUpcomingTasksQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchTasksQuery

/**
 * Сервис для работы с задачами - command часть
 */
interface TaskCommandService {
    /**
     * Создать новую задачу
     */
    fun createTask(command: CreateTaskCommand): TaskDto

    /**
     * Обновить задачу
     */
    fun updateTask(command: UpdateTaskCommand): TaskDto

    /**
     * Удалить задачу
     */
    fun deleteTask(command: DeleteTaskCommand)

    /**
     * Выполнить задачу
     */
    fun completeTask(command: CompleteTaskCommand): TaskDto

    /**
     * Назначить задачу пользователю
     */
    fun assignTask(command: AssignTaskCommand): TaskDto

    /**
     * Снять назначение задачи
     */
    fun unassignTask(command: UnassignTaskCommand): TaskDto

    /**
     * Добавить метку к задаче
     */
    fun addTaskTag(command: AddTaskTagCommand): TaskDto

    /**
     * Удалить метку из задачи
     */
    fun removeTaskTag(command: RemoveTaskTagCommand): TaskDto

    /**
     * Переместить задачу в другой список
     */
    fun moveTask(command: MoveTaskCommand): TaskDto
}

/**
 * Сервис для работы с задачами - query часть
 */
interface TaskQueryService :
    GetTaskQuery.Handler,
    SearchTasksQuery.Handler,
    GetUpcomingTasksQuery.Handler,
    GetOverdueTasksQuery.Handler,
    GetAssignedTasksQuery.Handler,
    GetRecentlyUpdatedTasksQuery.Handler