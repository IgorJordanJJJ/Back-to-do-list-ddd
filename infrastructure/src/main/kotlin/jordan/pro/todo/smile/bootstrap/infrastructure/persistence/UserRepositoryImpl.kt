package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.User
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.UserEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
open class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
    private val userEntityMapper: UserEntityMapper
) : UserRepository {

    @Transactional(readOnly = true)
    override fun findById(id: UserId): User? {
        return userJpaRepository.findById(id.value)
            .map { userEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByEmail(email: Email): User? {
        return userJpaRepository.findByEmail(email.toString())
            ?.let { userEntityMapper.toDomain(it) }
    }

    override fun save(user: User): User {
        val entity = userEntityMapper.toEntity(user)
        val savedEntity = userJpaRepository.save(entity)
        return userEntityMapper.toDomain(savedEntity)
    }

    override fun delete(id: UserId) {
        userJpaRepository.deleteById(id.value)
    }

    @Transactional(readOnly = true)
    override fun existsByEmail(email: Email): Boolean {
        return userJpaRepository.existsByEmail(email.toString())
    }

    @Transactional(readOnly = true)
    override fun existsById(id: UserId): Boolean {
        return userJpaRepository.existsById(id.value)
    }

    @Transactional(readOnly = true)
    override fun findAllByIds(ids: Collection<UserId>): List<User> {
        val uuids = ids.map { it.value }
        return userJpaRepository.findAllById(uuids)
            .map { userEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun searchByNameOrEmail(query: String): List<User> {
        return userJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
            .map { userEntityMapper.toDomain(it) }
    }
}
