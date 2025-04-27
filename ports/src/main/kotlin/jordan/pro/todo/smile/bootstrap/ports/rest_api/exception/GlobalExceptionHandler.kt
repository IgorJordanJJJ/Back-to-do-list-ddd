package jordan.pro.todo.smile.bootstrap.ports.rest_api.exception

import jakarta.servlet.http.HttpServletRequest
import jordan.pro.todo.smile.bootstrap.domain.core.exception.DomainException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TaskNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TodoListNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TodoListNotFoundException::class)
    fun handleTodoListNotFound(ex: TodoListNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message ?: "Список задач не найден",
            timestamp = LocalDateTime.now(),
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFound(ex: TaskNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message ?: "Задача не найдена",
            timestamp = LocalDateTime.now(),
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotAuthorizedException::class)
    fun handleUserNotAuthorized(ex: UserNotAuthorizedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            message = ex.message ?: "Доступ запрещен",
            timestamp = LocalDateTime.now(),
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message ?: "Ошибка бизнес-логики",
            timestamp = LocalDateTime.now(),
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val validationErrors = ex.bindingResult.fieldErrors.associate { fieldError: FieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Некорректное значение")
        }

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Ошибка валидации данных",
            timestamp = LocalDateTime.now(),
            path = request.requestURI,
            details = mapOf("validationErrors" to validationErrors)
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = "Внутренняя ошибка сервера",
            timestamp = LocalDateTime.now(),
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}