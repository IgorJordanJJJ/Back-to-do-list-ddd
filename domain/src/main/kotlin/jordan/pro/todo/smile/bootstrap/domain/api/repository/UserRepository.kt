package jordan.pro.todo.smile.bootstrap.domain.api.repository

import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

/**
 * Репозиторий для работы с пользователями
 */
interface UserRepository {
    /**
     * Найти пользователя по ID
     */
    fun findById(id: UserId): User?

    /**
     * Найти пользователя по email
     */
    fun findByEmail(email: Email): User?

    /**
     * Сохранить пользователя
     */
    fun save(user: User): User

    /**
     * Удалить пользователя
     */
    fun delete(id: UserId)

    /**
     * Проверить существование пользователя по email
     */
    fun existsByEmail(email: Email): Boolean

    /**
     * Проверить существование пользователя по ID
     */
    fun existsById(id: UserId): Boolean

    /**
     * Найти пользователей по списку ID
     */
    fun findAllByIds(ids: Collection<UserId>): List<User>

    /**
     * Поиск пользователей по части имени или email
     */
    fun searchByNameOrEmail(query: String): List<User>
}