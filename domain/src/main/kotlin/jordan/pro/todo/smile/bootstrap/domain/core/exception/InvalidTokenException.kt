package jordan.pro.todo.smile.bootstrap.domain.core.exception

/**
 * Исключение: Недействительный токен
 */
class InvalidTokenException(token: String) : DomainException("Токен недействителен или истек: $token")