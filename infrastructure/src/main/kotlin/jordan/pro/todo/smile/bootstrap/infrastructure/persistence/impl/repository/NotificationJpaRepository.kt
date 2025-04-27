package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.NotificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface NotificationJpaRepository : JpaRepository<NotificationEntity, UUID> {
    /**
     * Найти уведомления пользователя, отсортированные по дате создания (сначала новые)
     */
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): List<NotificationEntity>

    /**
     * Найти непрочитанные уведомления пользователя
     */
    fun findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId: UUID): List<NotificationEntity>

    /**
     * Найти уведомления пользователя по типу
     */
    fun findByUserIdAndType(userId: UUID, type: String): List<NotificationEntity>

    /**
     * Найти уведомления, созданные после указанной даты
     */
    fun findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId: UUID, createdAt: LocalDateTime): List<NotificationEntity>

    /**
     * Отметить все непрочитанные уведомления пользователя как прочитанные
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    fun updateAllUnreadToRead(@Param("userId") userId: UUID): Int
}
