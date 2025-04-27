package jordan.pro.todo.smile.bootstrap.domain.core.exception

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.ReminderId

/**
 * Исключение: Напоминание не найдено
 */
class ReminderNotFoundException(id: ReminderId) :
    DomainException("Напоминание с ID ${id.value} не найдено")