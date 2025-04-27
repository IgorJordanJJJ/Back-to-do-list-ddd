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
import java.util.*

@Entity
@Table(name = "task_tags")
data class TaskTagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    val task: TaskEntity,

    @Column(name = "tag", nullable = false, length = 30)
    val tag: String,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TaskTagEntity) return false
        if (id != null && other.id != null) return id == other.id
        return task.id == other.task.id && tag == other.tag
    }

    override fun hashCode(): Int {
        return Objects.hash(task.id, tag)
    }

    override fun toString(): String {
        return "TaskTagEntity(id=$id, tag='$tag')"
    }
}
