package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.ReminderRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.Reminder
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.ReminderEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.ReminderJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional
open class ReminderRepositoryImpl(
    private val reminderJpaRepository: ReminderJpaRepository,
    private val reminderEntityMapper: ReminderEntityMapper
) : ReminderRepository {

    @Transactional(readOnly = true)
    override fun findById(id: ReminderId): Reminder? {
        return reminderJpaRepository.findById(id.value)
            .map { reminderEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByTaskIdAndUserId(taskId: TaskId, userId: UserId): List<Reminder> {
        return reminderJpaRepository.findByTaskIdAndUserId(taskId.value, userId.value)
            .map { reminderEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByUserId(userId: UserId): List<Reminder> {
        return reminderJpaRepository.findByUserId(userId.value)
            .map { reminderEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByUserIdAndIsSent(userId: UserId, isSent: Boolean): List<Reminder> {
        return reminderJpaRepository.findByUserIdAndIsSent(userId.value, isSent)
            .map { reminderEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findByReminderTimeBeforeAndIsSent(reminderTime: LocalDateTime, isSent: Boolean): List<Reminder> {
        return reminderJpaRepository.findByReminderTimeBeforeAndIsSent(reminderTime, isSent)
            .map { reminderEntityMapper.toDomain(it) }
    }

    override fun save(reminder: Reminder): Reminder {
        val entity = reminderEntityMapper.toEntity(reminder)
        val savedEntity = reminderJpaRepository.save(entity)
        return reminderEntityMapper.toDomain(savedEntity)
    }

    override fun delete(id: ReminderId) {
        reminderJpaRepository.deleteById(id.value)
    }

    override fun deleteByTaskId(taskId: TaskId) {
        reminderJpaRepository.deleteByTaskId(taskId.value)
    }

    override fun deleteByUserId(userId: UserId) {
        reminderJpaRepository.deleteByUserId(userId.value)
    }
}