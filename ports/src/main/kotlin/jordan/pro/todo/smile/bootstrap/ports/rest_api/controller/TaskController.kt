package jordan.pro.todo.smile.bootstrap.ports.rest_api.controller

import jakarta.validation.Valid
import jordan.pro.todo.smile.bootstrap.application.api.command.AddTaskTagCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.AssignTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.CompleteTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.CreateTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.RemoveTaskTagCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UnassignTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateTaskCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.PriorityDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.StatusDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTaskQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchTasksQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.TaskSearchResultDto
import jordan.pro.todo.smile.bootstrap.application.api.service.TaskCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.TaskQueryService
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.CreateTaskRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.UpdateTaskRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.TaskResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.mapper.RestMapper
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/todo-lists/{todoListId}/tasks")
class TaskController(
    private val taskCommandService: TaskCommandService,
    private val taskQueryService: TaskQueryService,
    private val restMapper: RestMapper
) {

    @GetMapping("/{taskId}")
    fun getTask(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val query = GetTaskQuery(
            taskId = taskId,
            userId = userId
        )

        val task = taskQueryService.handle(query)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @PostMapping
    fun createTask(
        @PathVariable todoListId: String,
        @Valid @RequestBody request: CreateTaskRequest,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = CreateTaskCommand(
            todoListId = todoListId,
            title = request.title,
            description = request.description,
            priority = PriorityDto.valueOf(request.priority),
            dueDate = request.dueDate?.let { LocalDateTime.parse(it) },
            tags = request.tags,
            createdByUserId = userId
        )

        val task = taskCommandService.createTask(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toTaskResponse(task))
    }

    @PutMapping("/{taskId}")
    fun updateTask(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskRequest,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = UpdateTaskCommand(
            todoListId = todoListId,
            taskId = taskId,
            title = request.title,
            description = request.description,
            priority = PriorityDto.valueOf(request.priority),
            status = StatusDto.valueOf(request.status),
            dueDate = request.dueDate?.let { LocalDateTime.parse(it) },
            tags = request.tags,
            updatedByUserId = userId
        )

        val task = taskCommandService.updateTask(command)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @RequestParam userId: String
    ): ResponseEntity<Unit> {
        val command = DeleteTaskCommand(
            todoListId = todoListId,
            taskId = taskId,
            deletedByUserId = userId
        )

        taskCommandService.deleteTask(command)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{taskId}/complete")
    fun completeTask(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = CompleteTaskCommand(
            todoListId = todoListId,
            taskId = taskId,
            completedByUserId = userId
        )

        val task = taskCommandService.completeTask(command)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @PostMapping("/{taskId}/assign")
    fun assignTask(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @RequestParam assignToUserId: String,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = AssignTaskCommand(
            todoListId = todoListId,
            taskId = taskId,
            assignToUserId = assignToUserId,
            assignedByUserId = userId
        )

        val task = taskCommandService.assignTask(command)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @DeleteMapping("/{taskId}/assign")
    fun unassignTask(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = UnassignTaskCommand(
            todoListId = todoListId,
            taskId = taskId,
            unassignedByUserId = userId
        )

        val task = taskCommandService.unassignTask(command)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @PostMapping("/{taskId}/tags/{tag}")
    fun addTaskTag(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @PathVariable tag: String,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = AddTaskTagCommand(
            todoListId = todoListId,
            taskId = taskId,
            tag = tag,
            addedByUserId = userId
        )

        val task = taskCommandService.addTaskTag(command)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @DeleteMapping("/{taskId}/tags/{tag}")
    fun removeTaskTag(
        @PathVariable todoListId: String,
        @PathVariable taskId: String,
        @PathVariable tag: String,
        @RequestParam userId: String
    ): ResponseEntity<TaskResponse> {
        val command = RemoveTaskTagCommand(
            todoListId = todoListId,
            taskId = taskId,
            tag = tag,
            removedByUserId = userId
        )

        val task = taskCommandService.removeTaskTag(command)
        return ResponseEntity.ok(restMapper.toTaskResponse(task))
    }

    @GetMapping
    fun searchTasks(
        @PathVariable todoListId: String,
        @RequestParam userId: String,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) priority: String?,
        @RequestParam(required = false) tags: List<String>?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dueDateStart: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dueDateEnd: LocalDateTime?,
        @RequestParam(required = false) isOverdue: Boolean?,
        @RequestParam(required = false) assignedToMe: Boolean?,
        @RequestParam(required = false) textQuery: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<TaskSearchResultDto> {
        val query = SearchTasksQuery(
            userId = userId,
            status = status?.let { StatusDto.valueOf(it) },
            priority = priority?.let { PriorityDto.valueOf(it) },
            tags = tags,
            dueDateStart = dueDateStart,
            dueDateEnd = dueDateEnd,
            isOverdue = isOverdue,
            assignedToMe = assignedToMe,
            textQuery = textQuery,
            page = page,
            size = size
        )

        val result = taskQueryService.handle(query)
        return ResponseEntity.ok(result)
    }
}