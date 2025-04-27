package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tasks")
data class TaskEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String,

    @Column(name = "priority", nullable = false)
    var priority: Int,

    @Column(name = "status", nullable = false, length = 20)
    var status: String,

    @Column(name = "due_date")
    var dueDate: LocalDateTime?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    val createdBy: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    var assignedTo: UserEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", nullable = false)
    var todoList: TodoListEntity? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime?,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    @OneToMany(
        mappedBy = "task",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val tags: MutableSet<TaskTagEntity> = mutableSetOf()

    @OneToMany(
        mappedBy = "task",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val reminders: MutableSet<ReminderEntity> = mutableSetOf()

    // Методы управления тегами
    fun addTag(tag: String) {
        val tagEntity = TaskTagEntity(task = this, tag = tag)
        tags.add(tagEntity)
    }

    fun removeTag(tag: String) {
        tags.removeIf { it.tag == tag }
    }

    // Методы управления напоминаниями
    fun addReminder(user: UserEntity, reminderTime: LocalDateTime) {
        val reminder = ReminderEntity(
            task = this,
            user = user,
            reminderTime = reminderTime,
            isSent = false,
            createdAt = LocalDateTime.now()
        )
        reminders.add(reminder)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaskEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "TaskEntity(id=$id, title='$title', status='$status', dueDate=$dueDate, createdAt=$createdAt)"
    }
}