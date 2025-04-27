package jordan.pro.todo.smile.bootstrap.infrastructure.persistence

import jordan.pro.todo.smile.bootstrap.domain.api.repository.UserTokenRepository
import jordan.pro.todo.smile.bootstrap.domain.core.model.entities.UserToken
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TokenId
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.mapper.UserTokenEntityMapper
import jordan.pro.todo.smile.bootstrap.infrastructure.persistence.impl.repository.UserTokenJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional
open class UserTokenRepositoryImpl(
    private val userTokenJpaRepository: UserTokenJpaRepository,
    private val userTokenEntityMapper: UserTokenEntityMapper
) : UserTokenRepository {

    @Transactional(readOnly = true)
    override fun findById(id: TokenId): UserToken? {
        return userTokenJpaRepository.findById(id.value)
            .map { userTokenEntityMapper.toDomain(it) }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    override fun findByToken(token: String): UserToken? {
        return userTokenJpaRepository.findByToken(token)
            ?.let { userTokenEntityMapper.toDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun findActiveByUserId(userId: UserId): List<UserToken> {
        val now = LocalDateTime.now()
        return userTokenJpaRepository.findByUserIdAndIsRevokedFalseAndExpiresAtAfter(userId.value, now)
            .map { userTokenEntityMapper.toDomain(it) }
    }

    override fun save(token: UserToken): UserToken {
        val entity = userTokenEntityMapper.toEntity(token)
        val savedEntity = userTokenJpaRepository.save(entity)
        return userTokenEntityMapper.toDomain(savedEntity)
    }

    override fun revokeAllByUserId(userId: UserId) {
        val activeTokens = findActiveByUserId(userId)
        activeTokens.forEach { it.revoke() }
        saveAll(activeTokens)
    }

    private fun saveAll(tokens: List<UserToken>): List<UserToken> {
        val entities = tokens.map { userTokenEntityMapper.toEntity(it) }
        val savedEntities = userTokenJpaRepository.saveAll(entities)
        return savedEntities.map { userTokenEntityMapper.toDomain(it) }
    }

    override fun delete(id: TokenId) {
        userTokenJpaRepository.deleteById(id.value)
    }

    override fun deleteExpiredTokens() {
        val now = LocalDateTime.now()
        userTokenJpaRepository.deleteByExpiresAtBefore(now)
    }
}