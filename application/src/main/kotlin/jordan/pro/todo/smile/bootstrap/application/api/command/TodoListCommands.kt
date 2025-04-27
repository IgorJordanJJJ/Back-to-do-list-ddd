package jordan.pro.todo.smile.bootstrap.application.api.command

/**
 * Команда: Создать новый список задач
 */
data class CreateTodoListCommand(
    val title: String,
    val description: String,
    val userId: String
)

/**
 * Команда: Обновить список задач
 */
data class UpdateTodoListCommand(
    val todoListId: String,
    val title: String,
    val description: String,
    val userId: String
)

/**
 * Команда: Удалить список задач
 */
data class DeleteTodoListCommand(
    val todoListId: String,
    val userId: String
)

/**
 * Команда: Предоставить доступ к списку задач
 */
data class ShareTodoListCommand(
    val todoListId: String,
    val shareWithUserId: String,
    val sharedByUserId: String
)

/**
 * Команда: Отозвать доступ к списку задач
 */
data class UnshareTodoListCommand(
    val todoListId: String,
    val revokeFromUserId: String,
    val revokedByUserId: String
)