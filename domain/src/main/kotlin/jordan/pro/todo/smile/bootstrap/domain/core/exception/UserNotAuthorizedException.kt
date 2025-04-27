package jordan.pro.todo.smile.bootstrap.domain.core.exception

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.UserId

/**
 * Исключение: Пользователь не авторизован для выполнения операции
 */
class UserNotAuthorizedException(userId: UserId, resourceId: Any) :
    DomainException("Пользователь с ID ${userId.value} не имеет прав доступа к ресурсу $resourceId")
