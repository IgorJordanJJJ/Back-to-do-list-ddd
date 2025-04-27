package jordan.pro.todo.smile.bootstrap.domain.core.exception

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TaskId

/**
 * Исключение: Задача не найдена
 */
class TaskNotFoundException(id: TaskId) :
    DomainException("Задача с ID ${id.value} не найдена")