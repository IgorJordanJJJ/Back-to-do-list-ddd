package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.TaskRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Task
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.TaskEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.TaskJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional
open class TaskRepositoryImpl(
    private val taskJpaRepository: TaskJpaRepository,
    private val taskEntityMapper: TaskEntityMapper
) : TaskRepository {

    @Transactional(readOnly = true)
    override fun findById(id: TaskId): Task? {
        return taskJpaRepository.findById(id.value)
            .map { taskEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByTodoListId(todoListId: TodoListId): List<Task> {
        return taskJpaRepository.findByTodoListId(todoListId.value)
            .map { taskEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findOverdueTasks(): List<Task> {
        val now = LocalDateTime.now()
        return taskJpaRepository.findByDueDateBeforeAndStatusNot(now, "COMPLETED")
            .map { taskEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByAssignedToAndStatus(userId: UserId, status: String): List<Task> {
        return taskJpaRepository.findByAssignedToAndStatus(userId.value, status)
            .map { taskEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findUpcomingTasksByUser(userId: UserId, startDate: LocalDateTime, endDate: LocalDateTime): List<Task> {
        return taskJpaRepository.findUpcomingTasksByUser(userId.value, startDate, endDate)
            .map { taskEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findAllAccessibleByUser(userId: UserId): List<Task> {
        return taskJpaRepository.findAllAccessibleByUser(userId.value)
            .map { taskEntityMapper.toDomain(it) }
    }

    override fun save(task: Task): Task {
        val entity = taskEntityMapper.toEntity(task)
        val savedEntity = taskJpaRepository.save(entity)
        return taskEntityMapper.toDomain(savedEntity)
    }

    override fun saveAll(tasks: Collection<Task>): List<Task> {
        val entities = tasks.map { taskEntityMapper.toEntity(it) }
        val savedEntities = taskJpaRepository.saveAll(entities)
        return savedEntities.map { taskEntityMapper.toDomain(it) }
    }

    override fun delete(id: TaskId) {
        taskJpaRepository.deleteById(id.value)
    }

    override fun deleteByTodoListId(todoListId: TodoListId) {
        taskJpaRepository.deleteByTodoListId(todoListId.value)
    }
}
