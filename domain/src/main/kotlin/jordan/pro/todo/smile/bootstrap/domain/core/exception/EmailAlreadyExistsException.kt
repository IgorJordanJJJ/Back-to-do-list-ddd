package jordan.pro.todo.smile.bootstrap.domain.core.exception

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.Email

/**
 * Исключение: Email уже используется
 */
class EmailAlreadyExistsException(email: Email) : DomainException("Пользователь с email ${email.value} уже существует")