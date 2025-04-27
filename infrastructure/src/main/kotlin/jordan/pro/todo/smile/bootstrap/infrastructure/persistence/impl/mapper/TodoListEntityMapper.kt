package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import jordan.pro.todo.smile.bootstrap.domain.core.model.aggregates.TodoList
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Description
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Title
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.TodoListEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.TodoListSharingEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TodoListEntityMapper(
    private val taskEntityMapper: TaskEntityMapper,
    private val userJpaRepository: UserJpaRepository
) {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(todoList: TodoList): TodoListEntity {
        // Получаем необходимые связанные сущности
        val ownerEntity = userJpaRepository.findById(todoList.ownerId.value)
            .orElseThrow { IllegalStateException("Не найден владелец списка задач с ID ${todoList.ownerId}") }

        val entity = TodoListEntity(
            id = todoList.id.value,
            title = todoList.title.toString(),
            description = todoList.description.toString(),
            owner = ownerEntity,
            createdAt = todoList.createdAt,
            updatedAt = todoList.updatedAt
        )

        // Преобразуем задачи
        val taskEntities = todoList.tasks.map {
            val taskEntity = taskEntityMapper.toEntity(it)
            taskEntity.todoList = entity // Устанавливаем обратную связь
            taskEntity
        }.toMutableSet()
        entity.tasks.addAll(taskEntities)

        // Преобразуем общий доступ (sharedWith)
        todoList.sharedWith.forEach { userId ->
            val userEntity = userJpaRepository.findById(userId.value)
                .orElseThrow { IllegalStateException("Не найден пользователь с ID $userId для общего доступа") }

            val sharingEntity = TodoListSharingEntity(
                todoList = entity,
                user = userEntity,
                sharedBy = ownerEntity, // Предполагаем, что владелец поделился
                sharedAt = LocalDateTime.now()
            )
            entity.sharedWith.add(sharingEntity)
        }

        return entity
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: TodoListEntity): TodoList {
        // Преобразуем задачи
        val tasks = entity.tasks.map {
            taskEntityMapper.toDomain(it)
        }.toMutableSet()

        // Преобразуем sharedWith
        val sharedWith = entity.sharedWith.map {
            UserId.from(it.user.id)
        }.toMutableSet()

        return TodoList.reconstitute(
            id = TodoListId.from(entity.id),
            title = Title.create(entity.title),
            description = Description.create(entity.description),
            tasks = tasks,
            ownerId = UserId.from(entity.owner.id),
            sharedWith = sharedWith,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
