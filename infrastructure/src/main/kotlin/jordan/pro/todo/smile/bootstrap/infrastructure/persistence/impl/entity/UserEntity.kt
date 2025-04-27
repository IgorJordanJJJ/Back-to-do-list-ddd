package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "name", nullable = false, length = 255)
    var name: String,

    @Column(name = "email", nullable = false, length = 255, unique = true)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime,

    @Version
    @Column(name = "version")
    var version: Long = 0,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val reminders: MutableSet<ReminderEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], orphanRemoval = true)
    val todoLists: MutableSet<TodoListEntity> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    // Необходимо переопределить toString для избежания циклических ссылок
    override fun toString(): String {
        return "UserEntity(id=$id, name='$name', email='$email', createdAt=$createdAt, updatedAt=$updatedAt)"
    }
}