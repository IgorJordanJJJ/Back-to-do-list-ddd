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
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "todo_list_sharing",
    uniqueConstraints = [UniqueConstraint(columnNames = ["todo_list_id", "user_id"])])
data class TodoListSharingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id", nullable = false)
    val todoList: TodoListEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by", nullable = false)
    val sharedBy: UserEntity,

    @Column(name = "shared_at", nullable = false)
    val sharedAt: LocalDateTime,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TodoListSharingEntity) return false
        if (id != null && other.id != null) return id == other.id
        return todoList.id == other.todoList.id && user.id == other.user.id
    }

    override fun hashCode(): Int {
        return Objects.hash(todoList.id, user.id)
    }

    override fun toString(): String {
        return "TodoListSharingEntity(id=$id, todoListId=${todoList.id}, userId=${user.id}, sharedAt=$sharedAt)"
    }
}