package jordan.pro.todo.smile.bootstrap.ports.rest_api.controller

import jakarta.validation.Valid
import jordan.pro.todo.smile.bootstrap.application.api.command.CreateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateUserCommand
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchUsersQuery
import jordan.pro.todo.smile.bootstrap.application.api.service.UserCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.UserQueryService
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.CreateUserRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.UpdateUserRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.UserResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.UserSearchResultResponse
import jordan.pro.todo.smile.bootstrap.ports.rest_api.mapper.RestMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userCommandService: UserCommandService,
    private val userQueryService: UserQueryService,
    private val restMapper: RestMapper
) {

    @GetMapping("/{id}")
    fun getUser(
        @PathVariable id: String
    ): ResponseEntity<UserResponse> {
        val query = GetUserByIdQuery(id)
        val user = userQueryService.handle(query)
        return ResponseEntity.ok(restMapper.toUserResponse(user))
    }

    @PostMapping
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val command = CreateUserCommand(
            name = request.name,
            email = request.email
        )

        val user = userCommandService.createUser(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toUserResponse(user))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val command = UpdateUserCommand(
            id = id,
            name = request.name
        )

        val user = userCommandService.updateUser(command)
        return ResponseEntity.ok(restMapper.toUserResponse(user))
    }

    @GetMapping("/search")
    fun searchUsers(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<UserSearchResultResponse> {
        val searchQuery = SearchUsersQuery(
            query = query ?: "",
            page = page,
            size = size
        )

        val result = userQueryService.handle(searchQuery)
        return ResponseEntity.ok(restMapper.toUserSearchResultResponse(result))
    }
}