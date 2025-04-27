package jordan.pro.todo.smile.bootstrap.ports.rest_api.controller


import jakarta.validation.Valid
import jordan.pro.todo.smile.bootstrap.application.api.command.CreateTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.ShareTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UnshareTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListSummaryDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTodoListQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserTodoListsQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchTodoListsQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.TodoListSearchResult
import jordan.pro.todo.smile.bootstrap.application.api.service.TodoListCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.TodoListQueryService
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.CreateTodoListRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.UpdateTodoListRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.TodoListResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.mapper.RestMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/todo-lists")
class TodoListController(
    private val todoListCommandService: TodoListCommandService,
    private val todoListQueryService: TodoListQueryService,
    private val restMapper: RestMapper
) {

    @GetMapping
    fun getAllTodoLists(
        @RequestParam userId: String,
        @RequestParam(defaultValue = "true") includeShared: Boolean
    ): ResponseEntity<List<TodoListSummaryDto>> {
        val query = GetUserTodoListsQuery(
            userId = userId,
            includeShared = includeShared
        )

        val todoLists = todoListQueryService.handle(query)
        return ResponseEntity.ok(todoLists)
    }

    @GetMapping("/{id}")
    fun getTodoList(
        @PathVariable id: String,
        @RequestParam userId: String
    ): ResponseEntity<TodoListResponse> {
        val query = GetTodoListQuery(
            todoListId = id,
            userId = userId
        )

        val todoList = todoListQueryService.handle(query)
        return ResponseEntity.ok(restMapper.toResponse(todoList))
    }

    @PostMapping
    fun createTodoList(
        @Valid @RequestBody request: CreateTodoListRequest,
        @RequestParam userId: String
    ): ResponseEntity<TodoListResponse> {
        val command = CreateTodoListCommand(
            title = request.title,
            description = request.description,
            userId = userId
        )

        val todoList = todoListCommandService.createTodoList(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toResponse(todoList))
    }

    @PutMapping("/{id}")
    fun updateTodoList(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateTodoListRequest,
        @RequestParam userId: String
    ): ResponseEntity<TodoListResponse> {
        val command = UpdateTodoListCommand(
            todoListId = id,
            title = request.title,
            description = request.description,
            userId = userId
        )

        val todoList = todoListCommandService.updateTodoList(command)
        return ResponseEntity.ok(restMapper.toResponse(todoList))
    }

    @DeleteMapping("/{id}")
    fun deleteTodoList(
        @PathVariable id: String,
        @RequestParam userId: String
    ): ResponseEntity<Unit> {
        val command = DeleteTodoListCommand(
            todoListId = id,
            userId = userId
        )

        todoListCommandService.deleteTodoList(command)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/share")
    fun shareTodoList(
        @PathVariable id: String,
        @RequestParam shareWithUserId: String,
        @RequestParam userId: String
    ): ResponseEntity<TodoListResponse> {
        val command = ShareTodoListCommand(
            todoListId = id,
            shareWithUserId = shareWithUserId,
            sharedByUserId = userId
        )

        val todoList = todoListCommandService.shareTodoList(command)
        return ResponseEntity.ok(restMapper.toResponse(todoList))
    }

    @DeleteMapping("/{id}/share/{shareWithUserId}")
    fun unshareTodoList(
        @PathVariable id: String,
        @PathVariable shareWithUserId: String,
        @RequestParam userId: String
    ): ResponseEntity<TodoListResponse> {
        val command = UnshareTodoListCommand(
            todoListId = id,
            revokeFromUserId = shareWithUserId,
            revokedByUserId = userId
        )

        val todoList = todoListCommandService.unshareTodoList(command)
        return ResponseEntity.ok(restMapper.toResponse(todoList))
    }

    @GetMapping("/search")
    fun searchTodoLists(
        @RequestParam userId: String,
        @RequestParam(required = false, defaultValue = "") query: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<TodoListSearchResult> {
        val searchQuery = SearchTodoListsQuery(
            userId = userId,
            searchTerm = query,
            page = page,
            size = size
        )

        val result = todoListQueryService.handle(searchQuery)
        return ResponseEntity.ok(result)
    }
}