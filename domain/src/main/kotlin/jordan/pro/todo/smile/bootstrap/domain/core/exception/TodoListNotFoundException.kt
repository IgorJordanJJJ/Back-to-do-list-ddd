package jordan.pro.todo.smile.bootstrap.domain.core.exception

import jordan.pro.todo.smile.bootstrap.domain.core.model.value_objects.ids.TodoListId

/**
 * Исключение: Список задач не найден
 */
class TodoListNotFoundException(id: TodoListId) :
    DomainException("Список задач с ID ${id.value} не найден")