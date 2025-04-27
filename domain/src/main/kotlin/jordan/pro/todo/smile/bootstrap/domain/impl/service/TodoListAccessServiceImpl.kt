package jordan.pro.todo.smile.bootstrap.domain.impl.service

import jordan.pro.todo.smile.bootstrap.domain.api.repository.TodoListRepository
import jordan.pro.todo.smile.bootstrap.domain.api.service.DomainEventPublisher
import jordan.pro.todo.smile.bootstrap.domain.api.service.TodoListAccessService
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListSharedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.event.TodoListUnsharedEvent
import jordan.pro.todo.smile.bootstrap.domain.core.exception.TodoListNotFoundException
import jordan.pro.todo.smile.bootstrap.domain.core.exception.UserNotAuthorizedException
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class TodoListAccessServiceImpl(
    private val todoListRepository: TodoListRepository,
    @Qualifier("domainEventPublisherImpl") private val eventPublisher: DomainEventPublisher
) : TodoListAccessService {

    override fun checkAccess(userId: UserId, todoListId: TodoListId): Boolean {
        val todoList = todoListRepository.findById(todoListId)
            ?: return false

        return todoList.hasAccess(userId)
    }

    override fun checkOwnership(userId: UserId, todoListId: TodoListId): Boolean {
        val todoList = todoListRepository.findById(todoListId)
            ?: return false

        return todoList.isOwner(userId)
    }

    @Transactional
    override fun grantAccess(todoListId: TodoListId, userId: UserId, grantedBy: UserId) {
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем, что предоставляющий доступ пользователь является владельцем
        if (!todoList.isOwner(grantedBy)) {
            throw UserNotAuthorizedException(grantedBy, todoListId)
        }

        // Предоставляем доступ
        todoList.shareWith(userId)

        // Сохраняем изменения
        todoListRepository.save(todoList)

        // Публикуем событие
        val event = TodoListSharedEvent(
            todoListId = todoListId,
            sharedWithUserId = userId,
            sharedByUserId = grantedBy
        )
        eventPublisher.publish(event)
    }

    @Transactional
    override fun revokeAccess(todoListId: TodoListId, userId: UserId, revokedBy: UserId) {
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Проверяем, что отзывающий доступ пользователь является владельцем
        if (!todoList.isOwner(revokedBy)) {
            throw UserNotAuthorizedException(revokedBy, todoListId)
        }

        // Отзываем доступ
        todoList.unshareWith(userId)

        // Сохраняем изменения
        todoListRepository.save(todoList)

        // Публикуем событие
        val event = TodoListUnsharedEvent(
            todoListId = todoListId,
            revokedFromUserId = userId,
            revokedByUserId = revokedBy
        )
        eventPublisher.publish(event)
    }

    override fun getUsersWithAccess(todoListId: TodoListId): List<UserId> {
        val todoList = todoListRepository.findById(todoListId)
            ?: throw TodoListNotFoundException(todoListId)

        // Создаем список из владельца и всех пользователей с общим доступом
        val users = mutableListOf(todoList.ownerId)
        users.addAll(todoList.sharedWith)

        return users
    }
}