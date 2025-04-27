package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Reminder
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.ReminderEntity
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.TaskJpaRepository
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class ReminderEntityMapper(
    private val taskJpaRepository: TaskJpaRepository,
    private val userJpaRepository: UserJpaRepository
) {
    /**
     * Преобразование из доменной модели в JPA сущность
     */
    fun toEntity(reminder: Reminder): ReminderEntity {
        // Получаем необходимые связанные сущности
        val taskEntity = taskJpaRepository.findById(reminder.taskId.value)
            .orElseThrow { IllegalStateException("Не найдена задача с ID ${reminder.taskId}") }

        val userEntity = userJpaRepository.findById(reminder.userId.value)
            .orElseThrow { IllegalStateException("Не найден пользователь с ID ${reminder.userId}") }

        return ReminderEntity(
            id = reminder.id.value,
            task = taskEntity,
            user = userEntity,
            reminderTime = reminder.reminderTime,
            isSent = reminder.isSent,
            createdAt = reminder.createdAt
        )
    }

    /**
     * Преобразование из JPA сущности в доменную модель
     */
    fun toDomain(entity: ReminderEntity): Reminder {
        return Reminder.reconstitute(
            id = ReminderId.from(entity.id!!),
            taskId = TaskId.from(entity.task.id),
            userId = UserId.from(entity.user.id),
            reminderTime = entity.reminderTime,
            isSent = entity.isSent,
            createdAt = entity.createdAt
        )
    }
}