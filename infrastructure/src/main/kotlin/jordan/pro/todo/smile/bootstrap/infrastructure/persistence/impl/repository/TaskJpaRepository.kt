package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface TaskJpaRepository : JpaRepository<TaskEntity, UUID> {

    fun findByTodoListId(todoListId: UUID): List<TaskEntity>

    fun findByDueDateBeforeAndStatusNot(dueDate: LocalDateTime, status: String): List<TaskEntity>

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.todoList.id = :todoListId")
    fun deleteByTodoListId(@Param("todoListId") todoListId: UUID)

    @Query("SELECT t FROM TaskEntity t WHERE " +
            "t.todoList.id IN (SELECT tl.id FROM TodoListEntity tl WHERE tl.owner.id = :userId " +
            "OR EXISTS (SELECT s FROM TodoListSharingEntity s WHERE s.todoList = tl AND s.user.id = :userId))")
    fun findAllAccessibleByUser(@Param("userId") userId: UUID): List<TaskEntity>

    @Query("SELECT t FROM TaskEntity t WHERE " +
            "t.assignedTo = :userId AND t.status = :status")
    fun findByAssignedToAndStatus(@Param("userId") userId: UUID, @Param("status") status: String): List<TaskEntity>

    @Query("SELECT t FROM TaskEntity t WHERE " +
            "t.dueDate IS NOT NULL AND t.dueDate BETWEEN :startDate AND :endDate AND " +
            "t.todoList.id IN (SELECT tl.id FROM TodoListEntity tl WHERE tl.owner.id = :userId " +
            "OR EXISTS (SELECT s FROM TodoListSharingEntity s WHERE s.todoList = tl AND s.user.id = :userId))")
    fun findUpcomingTasksByUser(@Param("userId") userId: UUID,
                                @Param("startDate") startDate: LocalDateTime,
                                @Param("endDate") endDate: LocalDateTime): List<TaskEntity>
}