package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.TodoListRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.aggregates.TodoList
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.TodoListEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.TodoListJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
open class TodoListRepositoryImpl(
    private val todoListJpaRepository: TodoListJpaRepository,
    private val todoListEntityMapper: TodoListEntityMapper
) : TodoListRepository {

    @Transactional(readOnly = true)
    override fun findById(id: TodoListId): TodoList? {
        return todoListJpaRepository.findById(id.value)
            .map { todoListEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByUserId(userId: UserId): List<TodoList> {
        return todoListJpaRepository.findByOwnerId(userId.value)
            .map { todoListEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findSharedWithUser(userId: UserId): List<TodoList> {
        return todoListJpaRepository.findSharedWithUser(userId.value)
            .map { todoListEntityMapper.toDomain(it) }
    }

    override fun save(todoList: TodoList): TodoList {
        val entity = todoListEntityMapper.toEntity(todoList)
        val savedEntity = todoListJpaRepository.save(entity)
        return todoListEntityMapper.toDomain(savedEntity)
    }

    override fun delete(id: TodoListId) {
        todoListJpaRepository.deleteById(id.value)
    }

    @Transactional(readOnly = true)
    override fun exists(id: TodoListId): Boolean {
        return todoListJpaRepository.existsById(id.value)
    }

    @Transactional(readOnly = true)
    override fun countByOwnerId(ownerId: UserId): Int {
        return todoListJpaRepository.countByOwnerId(ownerId.value)
    }

    @Transactional(readOnly = true)
    override fun findAllAccessibleByUser(userId: UserId): List<TodoList> {
        return todoListJpaRepository.findAllAccessibleByUser(userId.value)
            .map { todoListEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun searchByTitle(userId: UserId, titleQuery: String): List<TodoList> {
        return todoListJpaRepository.searchByTitle(userId.value, titleQuery)
            .map { todoListEntityMapper.toDomain(it) }
    }
}