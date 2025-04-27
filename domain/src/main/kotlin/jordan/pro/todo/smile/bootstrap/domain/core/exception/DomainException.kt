package jordan.pro.todo.smile.bootstrap.domain.core.exception

/**
 * Базовое исключение для доменных ошибок
 */
abstract class DomainException(message: String) : RuntimeException(message)