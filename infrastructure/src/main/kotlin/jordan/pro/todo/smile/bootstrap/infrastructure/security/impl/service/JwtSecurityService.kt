package jordan.pro.todo.smile.bootstrap.infrastructure.security.impl.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.Claims
import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId
import jordan.pro.todo.smile.bootstrap.infrastructure.security.api.service.SecurityService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtSecurityService(
    @Value("\${todo-service.security.jwt.secret}") private val jwtSecretString: String,
    @Value("\${todo-service.security.jwt.expiration-minutes}") private val expirationMinutes: Long
) : SecurityService {

    private val jwtSecret: SecretKey

    init {
        val secretBytes = jwtSecretString.toByteArray(StandardCharsets.UTF_8)
        require(secretBytes.size >= 32) {
            "JWT secret must be at least 256 bits (32 bytes) long. Current size: ${secretBytes.size * 8} bits."
        }
        jwtSecret = Keys.hmacShaKeyFor(secretBytes)
    }

    override fun generateToken(userId: UserId): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMinutes * 60 * 1000)

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(jwtSecret)
            .compact()
    }

    override fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getUserIdFromToken(token: String): UserId {
        val claims: Claims = Jwts.parser()
            .verifyWith(jwtSecret)
            .build()
            .parseSignedClaims(token)
            .payload

        return UserId.from(claims.subject)
    }
}
