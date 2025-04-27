package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.*
import jordan.pro.todo.smile.bootstrap.application.api.dto.TaskDto
import jordan.pro.todo.smile.bootstrap.application.api.query.*
import jordan.pro.todo.smile.bootstrap.application.api.service.TaskCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.TaskQueryService
import jordan.pro.todo.smile.bootstrap.application.impl.mapper.TaskMapper
import jordan.pro.todo.smile.bootstrap.domain.api.repository.TodoListRepository
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.core.event.*
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TaskNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TodoListNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.*
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
open class TaskServiceImpl(
    private val todoListRepository: TodoListRepository,
    private val userRepository: UserRepository,
    @Qualifier("domainEventPublisherImpl") private val eventPublisher: DomainEventPublisher,
    private val taskMapper: TaskMapper
) : TaskCommandService, TaskQueryService {

    @Transactional
    override fun createTask(command: CreateTaskCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val userId = UserId.from(command.createdByUserId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Создаем новую задачу
        val task = todoList.addTask(
            title = Title.create(command.title),
            description = Description.create(command.description),
            priority = taskMapper.mapToDomainPriority(command.priority),
            dueDate = command.dueDate?.let { DueDate.create(it) },
            createdBy = userId
        )

        // Добавляем теги, если они есть
        command.tags.forEach { tag ->
            task.addTag(Tag.create(tag))
        }

        // Сохраняем обновленный список задач с новой задачей
        val savedTodoList = todoListRepository.save(todoList)

        // Находим сохраненную задачу
        val savedTask = savedTodoList.getTask(task.id)

        // Публикуем событие
        val event = TaskCreatedEvent(
            taskId = savedTask.id,
            todoListId = todoListId,
            title = savedTask.title,
            description = savedTask.description,
            priority = savedTask.priority,
            dueDate = savedTask.dueDate,
            createdBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(savedTask)
    }

    @Transactional
    override fun updateTask(command: UpdateTaskCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.updatedByUserId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Обновляем задачу
        todoList.updateTask(
            taskId = taskId,
            title = Title.create(command.title),
            description = Description.create(command.description),
            priority = taskMapper.mapToDomainPriority(command.priority),
            status = taskMapper.mapToDomainStatus(command.status),
            dueDate = command.dueDate?.let { DueDate.create(it) }
        )

        // Обновляем теги
        val task = todoList.getTask(taskId)
        val currentTags = task.tags.map { it.toString() }.toSet()

        // Удаляем теги, которых нет в запросе
        currentTags.filter { it !in command.tags }.forEach { tag ->
            task.removeTag(Tag.create(tag))
        }

        // Добавляем новые теги
        command.tags.filter { it !in currentTags }.forEach { tag ->
            task.addTag(Tag.create(tag))
        }

        // Сохраняем обновленный список задач
        val savedTodoList = todoListRepository.save(todoList)

        // Находим обновленную задачу
        val updatedTask = savedTodoList.getTask(taskId)

        // Публикуем событие
        val event = TaskUpdatedEvent(
            taskId = updatedTask.id,
            todoListId = todoListId,
            title = updatedTask.title,
            description = updatedTask.description,
            priority = updatedTask.priority,
            status = updatedTask.status,
            dueDate = updatedTask.dueDate,
            updatedBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(updatedTask)
    }

    @Transactional
    override fun deleteTask(command: DeleteTaskCommand) {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.deletedByUserId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Удаляем задачу
        todoList.removeTask(taskId)

        // Сохраняем обновленный список задач
        todoListRepository.save(todoList)

        // Публикуем событие
        val event = TaskDeletedEvent(
            taskId = taskId,
            todoListId = todoListId,
            deletedBy = userId
        )
        eventPublisher.publish(event)
    }

    @Transactional
    override fun completeTask(command: CompleteTaskCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.completedByUserId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Выполняем задачу
        todoList.completeTask(taskId)

        // Сохраняем обновленный список задач
        val savedTodoList = todoListRepository.save(todoList)

        // Находим выполненную задачу
        val completedTask = savedTodoList.getTask(taskId)

        // Публикуем событие
        val event = TaskCompletedEvent(
            taskId = completedTask.id,
            todoListId = todoListId,
            completedBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(completedTask)
    }

    @Transactional
    override fun assignTask(command: AssignTaskCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val assignToUserId = UserId.from(command.assignToUserId)
        val assignedByUserId = UserId.from(command.assignedByUserId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(assignedByUserId)) {
            throw UserNotAuthorizedException(assignedByUserId, todoListId)
        }

        // Проверяем, что назначаемый пользователь имеет доступ к списку задач
        if (!todoList.hasAccess(assignToUserId)) {
            throw UserNotAuthorizedException(assignToUserId, todoListId)
        }

        // Назначаем задачу
        todoList.assignTask(taskId, assignToUserId)

        // Сохраняем обновленный список задач
        val savedTodoList = todoListRepository.save(todoList)

        // Находим назначенную задачу
        val assignedTask = savedTodoList.getTask(taskId)

        // Публикуем событие
        val event = TaskAssignedEvent(
            taskId = assignedTask.id,
            todoListId = todoListId,
            assignedTo = assignToUserId,
            assignedBy = assignedByUserId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(assignedTask)
    }

    @Transactional
    override fun unassignTask(command: UnassignTaskCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.unassignedByUserId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Отменяем назначение задачи
        val task = todoList.getTask(taskId)
        task.unassign()

        // Сохраняем обновленный список задач
        val savedTodoList = todoListRepository.save(todoList)

        // Находим обновленную задачу
        val updatedTask = savedTodoList.getTask(taskId)

        // Публикуем событие
        val event = TaskUnassignedEvent(
            taskId = updatedTask.id,
            todoListId = todoListId,
            unassignedBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(updatedTask)
    }

    @Transactional
    override fun addTaskTag(command: AddTaskTagCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.addedByUserId)
        val tag = Tag.create(command.tag)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Добавляем метку
        todoList.addTaskTag(taskId, tag)

        // Сохраняем обновленный список задач
        val savedTodoList = todoListRepository.save(todoList)

        // Находим обновленную задачу
        val updatedTask = savedTodoList.getTask(taskId)

        // Публикуем событие
        val event = TaskTagAddedEvent(
            taskId = updatedTask.id,
            todoListId = todoListId,
            tag = tag,
            addedBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(updatedTask)
    }

    @Transactional
    override fun removeTaskTag(command: RemoveTaskTagCommand): TaskDto {
        val todoListId = TodoListId.from(command.todoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.removedByUserId)
        val tag = Tag.create(command.tag)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем доступ пользователя
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Удаляем метку
        todoList.removeTaskTag(taskId, tag)

        // Сохраняем обновленный список задач
        val savedTodoList = todoListRepository.save(todoList)

        // Находим обновленную задачу
        val updatedTask = savedTodoList.getTask(taskId)

        // Публикуем событие
        val event = TaskTagRemovedEvent(
            taskId = updatedTask.id,
            todoListId = todoListId,
            tag = tag,
            removedBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(updatedTask)
    }

    @Transactional
    override fun moveTask(command: MoveTaskCommand): TaskDto {
        val sourceTodoListId = TodoListId.from(command.sourceTodoListId)
        val targetTodoListId = TodoListId.from(command.targetTodoListId)
        val taskId = TaskId.from(command.taskId)
        val userId = UserId.from(command.movedByUserId)

        // Получаем исходный список задач
        val sourceTodoList = todoListRepository.findById(sourceTodoListId)
            ?: throw TodoListNotFoundException(sourceTodoListId)

        // Получаем целевой список задач
        val targetTodoList = todoListRepository.findById(targetTodoListId)
            ?: throw TodoListNotFoundException(targetTodoListId)

        // Проверяем доступ пользователя к обоим спискам
        if (!sourceTodoList.hasAccess(userId) || !targetTodoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, "move task between lists")
        }

        // Получаем задачу из исходного списка
        val task = sourceTodoList.getTask(taskId)

        // Удаляем задачу из исходного списка
        sourceTodoList.removeTask(taskId)

        // Создаем новую задачу в целевом списке
        val movedTask = targetTodoList.addTask(
            title = task.title,
            description = task.description,
            priority = task.priority,
            dueDate = task.dueDate,
            createdBy = task.createdBy
        )

        // Копируем дополнительные свойства
        if (task.assignedTo != null) {
            movedTask.assignTo(task.assignedTo!!)
        }
        task.tags.forEach { tag ->
            movedTask.addTag(tag)
        }
        if (task.status != TaskStatus.TODO) {
            movedTask.update(
                title = movedTask.title,
                description = movedTask.description,
                priority = movedTask.priority,
                status = task.status,
                dueDate = movedTask.dueDate
            )
        }

        // Сохраняем оба списка задач
        todoListRepository.save(sourceTodoList)
        val savedTargetTodoList = todoListRepository.save(targetTodoList)

        // Находим перемещенную задачу
        val savedMovedTask = savedTargetTodoList.getTask(movedTask.id)

        // Публикуем событие
        val event = TaskMovedEvent(
            oldTaskId = taskId,
            newTaskId = savedMovedTask.id,
            sourceTodoListId = sourceTodoListId,
            targetTodoListId = targetTodoListId,
            movedBy = userId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return taskMapper.toDto(savedMovedTask)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetTaskQuery): TaskDto {
        val taskId = TaskId.from(query.taskId)
        val userId = UserId.from(query.userId)

        // Находим задачу в репозитории (через поиск по всем спискам пользователя)
        val todoLists = todoListRepository.findAllAccessibleByUser(userId)

        for (todoList in todoLists) {
            try {
                val task = todoList.getTask(taskId)
                return taskMapper.toDto(task)
            } catch (e: TaskNotFoundException) {
                // Задача не найдена в этом списке, продолжаем поиск
                continue
            }
        }

        // Если задача не найдена ни в одном из списков
        throw TaskNotFoundException(taskId)
    }

    @Transactional(readOnly = true)
    override fun handle(query: SearchTasksQuery): TaskSearchResultDto {
        val userId = UserId.from(query.userId)

        // Получаем все доступные списки задач
        val todoLists = todoListRepository.findAllAccessibleByUser(userId)

        // Собираем все задачи
        val allTasks = todoLists.flatMap { it.tasks }

        // Применяем фильтры
        var filteredTasks = allTasks.toList()

        // Фильтр по статусу
        if (query.status != null) {
            val status = taskMapper.mapToDomainStatus(query.status)
            filteredTasks = filteredTasks.filter { it.status == status }
        }

        // Фильтр по приоритету
        if (query.priority != null) {
            val priority = taskMapper.mapToDomainPriority(query.priority)
            filteredTasks = filteredTasks.filter { it.priority == priority }
        }

        // Фильтр по тегам
        if (query.tags != null && query.tags.isNotEmpty()) {
            filteredTasks = filteredTasks.filter { task ->
                val taskTags = task.tags.map { it.toString() }
                query.tags.all { it in taskTags }
            }
        }

        // Фильтр по диапазону дат - исправление проблемы smart cast
        if (query.dueDateStart != null) {
            filteredTasks = filteredTasks.filter { task ->
                task.dueDate?.value?.isAfter(query.dueDateStart) == true
            }
        }
        if (query.dueDateEnd != null) {
            filteredTasks = filteredTasks.filter { task ->
                task.dueDate?.value?.isBefore(query.dueDateEnd) == true
            }
        }

        // Фильтр по просроченным задачам
        if (query.isOverdue == true) {
            filteredTasks = filteredTasks.filter { it.isOverdue() }
        }

        // Фильтр по назначенным задачам
        if (query.assignedToMe == true) {
            filteredTasks = filteredTasks.filter { it.assignedTo == userId }
        }

        // Текстовый поиск
        if (query.textQuery != null && query.textQuery.isNotBlank()) {
            val searchText = query.textQuery.lowercase()
            filteredTasks = filteredTasks.filter { task ->
                task.title.toString().lowercase().contains(searchText) ||
                        task.description.toString().lowercase().contains(searchText)
            }
        }

        // Сортировка (по умолчанию по дате обновления)
        filteredTasks = filteredTasks.sortedByDescending { it.updatedAt }

        // Применяем пагинацию
        val totalItems = filteredTasks.size
        val totalPages = (totalItems + query.size - 1) / query.size
        val startIndex = query.page * query.size
        val endIndex = minOf(startIndex + query.size, totalItems)

        val pagedItems = if (startIndex < totalItems) {
            filteredTasks.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // Преобразуем и возвращаем результат
        return TaskSearchResultDto(
            items = pagedItems.map { taskMapper.toDto(it) },
            totalItems = totalItems,
            totalPages = totalPages,
            currentPage = query.page
        )
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUpcomingTasksQuery): List<TaskDto> {
        val userId = UserId.from(query.userId)
        val endDate = LocalDateTime.now().plusDays(query.days.toLong())

        // Получаем все доступные списки задач
        val todoLists = todoListRepository.findAllAccessibleByUser(userId)

        // Собираем все задачи
        val allTasks = todoLists.flatMap { it.tasks }

        // Фильтруем задачи с приближающимся сроком - исправление проблемы smart cast
        val upcomingTasks = allTasks.filter { task ->
            !task.status.isCompleted() &&
                    task.dueDate != null &&
                    task.dueDate?.value?.isAfter(LocalDateTime.now()) == true &&
                    task.dueDate?.value?.isBefore(endDate) == true
        }

        // Сортируем по дате выполнения (ближайшие сначала)
        val sortedTasks = upcomingTasks.sortedBy { it.dueDate?.value }

        // Преобразуем и возвращаем результат
        return sortedTasks.map { taskMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetOverdueTasksQuery): List<TaskDto> {
        val userId = UserId.from(query.userId)

        // Получаем все доступные списки задач
        val todoLists = todoListRepository.findAllAccessibleByUser(userId)

        // Собираем все задачи
        val allTasks = todoLists.flatMap { it.tasks }

        // Фильтруем просроченные задачи - используем метод isOverdue() вместо доступа к свойствам
        val overdueTasks = allTasks.filter { it.isOverdue() }

        // Сортируем по дате выполнения (самые просроченные сначала)
        val sortedTasks = overdueTasks.sortedBy { it.dueDate?.value }

        // Преобразуем и возвращаем результат
        return sortedTasks.map { taskMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetAssignedTasksQuery): List<TaskDto> {
        val userId = UserId.from(query.userId)

        // Получаем все доступные списки задач
        val todoLists = todoListRepository.findAllAccessibleByUser(userId)

        // Собираем все задачи, назначенные на пользователя
        var assignedTasks = todoLists.flatMap { it.tasks }
            .filter { it.assignedTo == userId }

        // Фильтруем по статусу, если указан
        if (query.status != null) {
            val status = taskMapper.mapToDomainStatus(query.status)
            assignedTasks = assignedTasks.filter { it.status == status }
        }

        // Сортируем по приоритету (высокий сначала) и дате обновления
        val sortedTasks = assignedTasks.sortedWith(
            compareByDescending<jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Task> { it.priority.value }
                .thenByDescending { it.updatedAt }
        )

        // Преобразуем и возвращаем результат
        return sortedTasks.map { taskMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetRecentlyUpdatedTasksQuery): List<TaskDto> {
        val userId = UserId.from(query.userId)

        // Получаем все доступные списки задач
        val todoLists = todoListRepository.findAllAccessibleByUser(userId)

        // Собираем все задачи
        val allTasks = todoLists.flatMap { it.tasks }

        // Сортируем по дате обновления (самые новые сначала) и ограничиваем количество
        val recentTasks = allTasks.sortedByDescending { it.updatedAt }
            .take(query.limit)

        // Преобразуем и возвращаем результат
        return recentTasks.map { taskMapper.toDto(it) }
    }
}