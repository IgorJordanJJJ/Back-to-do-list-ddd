package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "reminders")
data class ReminderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: TaskEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "reminder_time", nullable = false)
    var reminderTime: LocalDateTime,

    @Column(name = "is_sent", nullable = false)
    var isSent: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReminderEntity) return false
        if (id != null && other.id != null) return id == other.id
        return task.id == other.task.id && user.id == other.user.id && reminderTime == other.reminderTime
    }

    override fun hashCode(): Int {
        return Objects.hash(task.id, user.id, reminderTime)
    }

    override fun toString(): String {
        return "ReminderEntity(id=$id, taskId=${task.id}, userId=${user.id}, reminderTime=$reminderTime, isSent=$isSent)"
    }
}