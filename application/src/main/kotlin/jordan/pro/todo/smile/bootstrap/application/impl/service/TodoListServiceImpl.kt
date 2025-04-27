package jordan.pro.todo.smile.bootstrap.application.impl.service

import jordan.pro.todo.smile.bootstrap.application.api.command.CreateTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.DeleteTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.ShareTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UnshareTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.command.UpdateTodoListCommand
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListDto
import jordan.pro.todo.smile.bootstrap.application.api.dto.TodoListSummaryDto
import jordan.pro.todo.smile.bootstrap.application.api.query.GetTodoListQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.GetUserTodoListsQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.SearchTodoListsQuery
import jordan.pro.todo.smile.bootstrap.application.api.query.TodoListSearchResult
import jordan.pro.todo.smile.bootstrap.application.api.service.TodoListCommandService
import jordan.pro.todo.smile.bootstrap.application.api.service.TodoListQueryService
import jordan.pro.todo.smile.bootstrap.application.impl.mapper.TodoListMapper
import jordan.pro.todo.smile.bootstrap.domain.api.repository.TodoListRepository
import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.api.service.TodoListAccessService
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListCreatedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListDeletedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListSharedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListUnsharedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListUpdatedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TodoListNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.domain.core.model.aggregates.TodoList
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Description
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Title
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class TodoListServiceImpl(
    private val todoListRepository: TodoListRepository,
    private val userRepository: UserRepository,
    private val todoListAccessService: TodoListAccessService,
    @Qualifier("domainEventPublisherImpl") private val eventPublisher: DomainEventPublisher,
    private val todoListMapper: TodoListMapper
) : TodoListCommandService, TodoListQueryService {

    @Transactional
    override fun createTodoList(command: CreateTodoListCommand): TodoListDto {
        val userId = UserId.from(command.userId)

        // Проверяем существование пользователя
        userRepository.findById(userId) ?: throw UserNotAuthorizedException(userId, "create todo list")

        // Создаем новый список задач
        val todoList = TodoList.create(
            title = Title.create(command.title),
            description = Description.create(command.description),
            ownerId = userId
        )

        // Сохраняем в репозитории
        val savedTodoList = todoListRepository.save(todoList)

        // Публикуем событие
        val event = TodoListCreatedEvent(
            todoListId = savedTodoList.id,
            title = savedTodoList.title,
            description = savedTodoList.description,
            ownerId = savedTodoList.ownerId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return todoListMapper.toDto(savedTodoList)
    }

    @Transactional
    override fun updateTodoList(command: UpdateTodoListCommand): TodoListDto {
        val todoListId = TodoListId.from(command.todoListId)
        val userId = UserId.from(command.userId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем, что пользователь является владельцем
        if (!todoList.isOwner(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Обновляем список задач
        todoList.update(
            title = Title.create(command.title),
            description = Description.create(command.description)
        )

        // Сохраняем в репозитории
        val updatedTodoList = todoListRepository.save(todoList)

        // Публикуем событие
        val event = TodoListUpdatedEvent(
            todoListId = updatedTodoList.id,
            title = updatedTodoList.title,
            description = updatedTodoList.description
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return todoListMapper.toDto(updatedTodoList)
    }

    @Transactional
    override fun deleteTodoList(command: DeleteTodoListCommand) {
        val todoListId = TodoListId.from(command.todoListId)
        val userId = UserId.from(command.userId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем, что пользователь является владельцем
        if (!todoList.isOwner(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Удаляем из репозитория
        todoListRepository.delete(todoListId)

        // Публикуем событие
        val event = TodoListDeletedEvent(
            todoListId = todoListId
        )
        eventPublisher.publish(event)
    }

    @Transactional
    override fun shareTodoList(command: ShareTodoListCommand): TodoListDto {
        val todoListId = TodoListId.from(command.todoListId)
        val shareWithUserId = UserId.from(command.shareWithUserId)
        val sharedByUserId = UserId.from(command.sharedByUserId)

        // Предоставляем доступ через доменный сервис
        todoListAccessService.grantAccess(todoListId, shareWithUserId, sharedByUserId)

        // Получаем обновленный список задач
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Публикуем событие
        val event = TodoListSharedEvent(
            todoListId = todoListId,
            sharedWithUserId = shareWithUserId,
            sharedByUserId = sharedByUserId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return todoListMapper.toDto(todoList)
    }

    @Transactional
    override fun unshareTodoList(command: UnshareTodoListCommand): TodoListDto {
        val todoListId = TodoListId.from(command.todoListId)
        val revokeFromUserId = UserId.from(command.revokeFromUserId)
        val revokedByUserId = UserId.from(command.revokedByUserId)

        // Отзываем доступ через доменный сервис
        todoListAccessService.revokeAccess(todoListId, revokeFromUserId, revokedByUserId)

        // Получаем обновленный список задач
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Публикуем событие
        val event = TodoListUnsharedEvent(
            todoListId = todoListId,
            revokedFromUserId = revokeFromUserId,
            revokedByUserId = revokedByUserId
        )
        eventPublisher.publish(event)

        // Преобразуем и возвращаем DTO
        return todoListMapper.toDto(todoList)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetTodoListQuery): TodoListDto {
        val todoListId = TodoListId.from(query.todoListId)
        val userId = UserId.from(query.userId)

        // Получаем список задач из репозитория
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем, что пользователь имеет доступ
        if (!todoList.hasAccess(userId)) {
            throw UserNotAuthorizedException(userId, todoListId)
        }

        // Преобразуем и возвращаем DTO
        return todoListMapper.toDto(todoList)
    }

    @Transactional(readOnly = true)
    override fun handle(query: GetUserTodoListsQuery): List<TodoListSummaryDto> {
        val userId = UserId.from(query.userId)

        // Получаем списки задач в зависимости от флага includeShared
        val todoLists = if (query.includeShared) {
            todoListRepository.findAllAccessibleByUser(userId)
        } else {
            todoListRepository.findByUserId(userId)
        }

        // Преобразуем и возвращаем список DTO
        return todoLists.map { todoListMapper.toSummaryDto(it) }
    }

    @Transactional(readOnly = true)
    override fun handle(query: SearchTodoListsQuery): TodoListSearchResult {
        val userId = UserId.from(query.userId)

        // Ищем списки задач по названию
        val todoLists = todoListRepository.searchByTitle(userId, query.searchTerm)

        // Применяем пагинацию (в реальном коде это должно быть реализовано в репозитории)
        val totalItems = todoLists.size
        val totalPages = (totalItems + query.size - 1) / query.size
        val startIndex = query.page * query.size
        val endIndex = minOf(startIndex + query.size, totalItems)

        val pagedItems = if (startIndex < totalItems) {
            todoLists.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // Преобразуем и возвращаем результат
        return TodoListSearchResult(
            items = pagedItems.map { todoListMapper.toSummaryDto(it) },
            totalItems = totalItems,
            totalPages = totalPages,
            currentPage = query.page
        )
    }
}