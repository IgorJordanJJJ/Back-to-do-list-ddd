package jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository

import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.entity.TodoListEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA репозиторий для TodoListEntity
 */
@Repository
interface TodoListJpaRepository : JpaRepository<TodoListEntity, UUID> {
    /**
     * Найти списки задач по идентификатору владельца
     */
    fun findByOwnerId(ownerId: UUID): List<TodoListEntity>

    /**
     * Найти списки задач, к которым пользователь имеет доступ (через общий доступ)
     */
    @Query("SELECT tl FROM TodoListEntity tl JOIN tl.sharedWith s WHERE s.user.id = :userId")
    fun findSharedWithUser(@Param("userId") userId: UUID): List<TodoListEntity>

    /**
     * Найти все списки задач, доступные пользователю (созданные им или с общим доступом)
     */
    @Query("SELECT tl FROM TodoListEntity tl WHERE tl.owner.id = :userId " +
            "OR EXISTS (SELECT s FROM TodoListSharingEntity s WHERE s.todoList = tl AND s.user.id = :userId)")
    fun findAllAccessibleByUser(@Param("userId") userId: UUID): List<TodoListEntity>

    /**
     * Поиск списков задач по части названия
     */
    @Query("SELECT tl FROM TodoListEntity tl WHERE " +
            "(tl.owner.id = :userId OR EXISTS (SELECT s FROM TodoListSharingEntity s WHERE s.todoList = tl AND s.user.id = :userId)) " +
            "AND LOWER(tl.title) LIKE LOWER(CONCAT('%', :titleQuery, '%'))")
    fun searchByTitle(@Param("userId") userId: UUID, @Param("titleQuery") titleQuery: String): List<TodoListEntity>

    /**
     * Посчитать количество списков задач, созданных пользователем
     */
    fun countByOwnerId(ownerId: UUID): Int
}