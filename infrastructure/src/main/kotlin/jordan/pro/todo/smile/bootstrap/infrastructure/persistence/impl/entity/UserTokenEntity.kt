package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_tokens")
data class UserTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "token", nullable = false, unique = true)
    val token: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(name = "is_revoked", nullable = false)
    var isRevoked: Boolean = false,

    @Version
    @Column(name = "version")
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserTokenEntity) return false
        if (id != null && other.id != null) return id == other.id
        return token == other.token
    }

    override fun hashCode(): Int {
        return token.hashCode()
    }

    override fun toString(): String {
        return "UserTokenEntity(id=$id, userId=${user.id}, expiresAt=$expiresAt, isRevoked=$isRevoked)"
    }
}