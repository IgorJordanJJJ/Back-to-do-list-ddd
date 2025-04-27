package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.ReminderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ReminderJpaRepository : JpaRepository<ReminderEntity, Long> {

    fun findByTaskId(taskId: java.util.UUID): List<ReminderEntity>

    fun findByUserId(userId: java.util.UUID): List<ReminderEntity>

    fun findByTaskIdAndUserId(taskId: java.util.UUID, userId: java.util.UUID): List<ReminderEntity>

    fun findByUserIdAndIsSent(userId: java.util.UUID, isSent: Boolean): List<ReminderEntity>

    fun findByReminderTimeBeforeAndIsSent(reminderTime: LocalDateTime, isSent: Boolean): List<ReminderEntity>

    @Modifying
    @Query("DELETE FROM ReminderEntity r WHERE r.task.id = :taskId")
    fun deleteByTaskId(@Param("taskId") taskId: java.util.UUID)

    @Modifying
    @Query("DELETE FROM ReminderEntity r WHERE r.user.id = :userId")
    fun deleteByUserId(@Param("userId") userId: java.util.UUID)
}