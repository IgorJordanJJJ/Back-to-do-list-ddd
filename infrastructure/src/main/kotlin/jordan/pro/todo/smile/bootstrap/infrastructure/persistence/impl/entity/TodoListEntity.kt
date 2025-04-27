package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.UUID

/**
 * JPA сущность для списка задач
 */
@Entity
@Table(name = "todo_lists")
data class TodoListEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    val owner: UserEntity,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    @OneToMany(
        mappedBy = "todoList",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val tasks: MutableSet<TaskEntity> = mutableSetOf()

    @OneToMany(
        mappedBy = "todoList",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val sharedWith: MutableSet<TodoListSharingEntity> = mutableSetOf()

    // Методы управления коллекциями
    fun addTask(task: TaskEntity) {
        tasks.add(task)
        task.todoList = this
    }

    fun removeTask(task: TaskEntity) {
        tasks.remove(task)
        task.todoList = null
    }

    fun shareWith(user: UserEntity, sharedBy: UserEntity) {
        val sharing = TodoListSharingEntity(
            todoList = this,
            user = user,
            sharedBy = sharedBy,
            sharedAt = LocalDateTime.now()
        )
        sharedWith.add(sharing)
    }

    fun unshareWith(user: UserEntity) {
        sharedWith.removeIf { it.user.id == user.id }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TodoListEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "TodoListEntity(id=$id, title='$title', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}