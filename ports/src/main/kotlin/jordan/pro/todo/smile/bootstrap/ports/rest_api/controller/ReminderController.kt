package jordan.pro.todo.smile.bootstrap.ports.rest_api.controller

import jakarta.validation.Valid
import jordan.pro.todo.smile.bootstrap.application.api.command.CreateReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateReminderCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.ReminderDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetReminderByIdQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTaskRemindersQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserRemindersQuery
import jordan.pro.todo.smile.bootstrap.application.api.service.ReminderCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.ReminderQueryService
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.CreateReminderRequest
import jordan.pro.todo.smile.bootstrap.ports.rest_api.dto.request.UpdateReminderRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class ReminderController(
    private val reminderCommandService: ReminderCommandService,
    private val reminderQueryService: ReminderQueryService
) {

    @GetMapping("/reminders/{id}")
    fun getReminder(
        @PathVariable id: String,
        @RequestParam userId: String
    ): ResponseEntity<ReminderDto> {
        val query = GetReminderByIdQuery(
            reminderId = id,
            userId = userId
        )

        val reminder = reminderQueryService.handle(query)
        return ResponseEntity.ok(reminder)
    }

    @GetMapping("/tasks/{taskId}/reminders")
    fun getTaskReminders(
        @PathVariable taskId: String,
        @RequestParam userId: String
    ): ResponseEntity<List<ReminderDto>> {
        val query = GetTaskRemindersQuery(
            taskId = taskId,
            userId = userId
        )

        val reminders = reminderQueryService.handle(query)
        return ResponseEntity.ok(reminders)
    }

    @GetMapping("/users/{userId}/reminders")
    fun getUserReminders(
        @PathVariable userId: String,
        @RequestParam(required = false, defaultValue = "false") includeCompleted: Boolean
    ): ResponseEntity<List<ReminderDto>> {
        val query = GetUserRemindersQuery(
            userId = userId,
            includeCompleted = includeCompleted
        )

        val reminders = reminderQueryService.handle(query)
        return ResponseEntity.ok(reminders)
    }

    @PostMapping("/tasks/{taskId}/reminders")
    fun createReminder(
        @PathVariable taskId: String,
        @Valid @RequestBody request: CreateReminderRequest,
        @RequestParam userId: String
    ): ResponseEntity<ReminderDto> {
        val command = CreateReminderCommand(
            taskId = taskId,
            userId = userId,
            reminderTime = request.reminderTime
        )

        val reminder = reminderCommandService.createReminder(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(reminder)
    }

    @PutMapping("/reminders/{id}")
    fun updateReminder(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateReminderRequest,
        @RequestParam userId: String
    ): ResponseEntity<ReminderDto> {
        val command = UpdateReminderCommand(
            reminderId = id,
            userId = userId,
            reminderTime = request.reminderTime
        )

        val reminder = reminderCommandService.updateReminder(command)
        return ResponseEntity.ok(reminder)
    }

    @DeleteMapping("/reminders/{id}")
    fun deleteReminder(
        @PathVariable id: String,
        @RequestParam userId: String
    ): ResponseEntity<Unit> {
        val command = DeleteReminderCommand(
            reminderId = id,
            userId = userId
        )

        reminderCommandService.deleteReminder(command)
        return ResponseEntity.noContent().build()
    }
}