package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Task
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.*
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.TaskEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.TodoListJpaRepository
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class TaskEntityMapper(
    private val userJpaRepository: UserJpaRepository,
    private val todoListJpaRepository: TodoListJpaRepository
) {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(task: Task): TaskEntity {
        // Получаем необходимые связанные сущности
        val createdByEntity = userJpaRepository.findById(task.createdBy.value)
            .orElseThrow { IllegalStateException("Не найден создатель задачи с ID ${task.createdBy}") }

        val assignedToEntity = task.assignedTo?.let { assignedToId ->
            userJpaRepository.findById(assignedToId.value)
                .orElse(null)
        }

        val todoListEntity = todoListJpaRepository.findById(task.todoListId.value)
            .orElseThrow { IllegalStateException("Не найден список задач с ID ${task.todoListId}") }

        val entity = TaskEntity(
            id = task.id.value,
            title = task.title.toString(),
            description = task.description.toString(),
            priority = task.priority.value,
            status = task.status.value,
            dueDate = task.dueDate?.value,
            createdBy = createdByEntity,
            assignedTo = assignedToEntity,
            todoList = todoListEntity,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
            completedAt = task.completedAt
        )

        // Преобразуем теги
        task.tags.forEach { tag ->
            entity.addTag(tag.toString())
        }

        return entity
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: TaskEntity): Task {
        // Преобразуем теги
        val tags = entity.tags.map {
            Tag.create(it.tag)
        }.toMutableSet()

        // Преобразуем dueDate, если он установлен
        val dueDate = entity.dueDate?.let { DueDate.create(it) }

        // Преобразуем assignedTo, если он установлен
        val assignedTo = entity.assignedTo?.let { UserId.from(it.id) }

        return Task.reconstitute(
            id = TaskId.from(entity.id),
            todoListId = TodoListId.from(entity.todoList?.id ?: throw IllegalStateException("У задачи отсутствует список задач")),
            title = Title.create(entity.title),
            description = Description.create(entity.description),
            priority = TaskPriority.fromValue(entity.priority),
            status = TaskStatus.fromValue(entity.status),
            dueDate = dueDate,
            tags = tags,
            createdBy = UserId.from(entity.createdBy.id),
            assignedTo = assignedTo,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            completedAt = entity.completedAt
        )
    }
}
