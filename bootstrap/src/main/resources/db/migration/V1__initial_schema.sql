-- Создание таблицы пользователей
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
     version BIGINT DEFAULT 0 NOT NULL
);

-- Создание таблицы списков задач
CREATE TABLE todo_lists (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Создание таблицы задач
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    todo_list_id UUID NOT NULL REFERENCES todo_lists(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    due_date TIMESTAMP,
    created_by UUID NOT NULL REFERENCES users(id),
    assigned_to UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Создание таблицы меток задач
CREATE TABLE task_tags (
    id BIGSERIAL PRIMARY KEY,
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    tag VARCHAR(30) NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Создание таблицы общего доступа к спискам задач
CREATE TABLE todo_list_sharing (
    id BIGSERIAL PRIMARY KEY,
    todo_list_id UUID NOT NULL REFERENCES todo_lists(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shared_at TIMESTAMP NOT NULL,
    shared_by UUID NOT NULL REFERENCES users(id),
    version BIGINT DEFAULT 0 NOT NULL,
    UNIQUE (todo_list_id, user_id)
);

-- Создание таблицы напоминаний
CREATE TABLE reminders (
    id BIGSERIAL PRIMARY KEY,
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reminder_time TIMESTAMP NOT NULL,
    is_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Создание таблицы уведомлений
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    related_entity_id VARCHAR(255),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Создание таблицы доменных событий
CREATE TABLE domain_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    user_id UUID REFERENCES users(id),
    occurred_on TIMESTAMP NOT NULL,
    payload TEXT NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Индексы
CREATE INDEX idx_todo_lists_owner_id ON todo_lists(owner_id);
CREATE INDEX idx_tasks_todo_list_id ON tasks(todo_list_id);
CREATE INDEX idx_tasks_created_by ON tasks(created_by);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_task_tags_task_id ON task_tags(task_id);
CREATE INDEX idx_task_tags_tag ON task_tags(tag);
CREATE INDEX idx_todo_list_sharing_todo_list_id ON todo_list_sharing(todo_list_id);
CREATE INDEX idx_todo_list_sharing_user_id ON todo_list_sharing(user_id);
CREATE INDEX idx_reminders_task_id ON reminders(task_id);
CREATE INDEX idx_reminders_user_id ON reminders(user_id);
CREATE INDEX idx_reminders_reminder_time ON reminders(reminder_time);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_domain_events_entity_id ON domain_events(entity_id);
CREATE INDEX idx_domain_events_entity_type ON domain_events(entity_type);
CREATE INDEX idx_domain_events_occurred_on ON domain_events(occurred_on);