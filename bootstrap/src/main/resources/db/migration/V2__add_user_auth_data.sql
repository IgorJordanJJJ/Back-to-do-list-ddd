-- Добавление колонки для хранения хеша пароля
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255);

-- Создание таблицы для хранения токенов
CREATE TABLE user_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Индекс для быстрого поиска по токену
CREATE INDEX idx_user_tokens_token ON user_tokens(token);

-- Создание таблицы для отслеживания событий RabbitMQ
CREATE TABLE message_outbox (
    id BIGSERIAL PRIMARY KEY,
    event_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    routing_key VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    retry_count INT NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 0 NOT NULL
);

-- Индекс для быстрого поиска необработанных сообщений
CREATE INDEX idx_message_outbox_status ON message_outbox(status);

-- Обновление по умолчанию для безопасности
UPDATE users SET password_hash = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG' WHERE password_hash IS NULL;

-- После установки временного пароля устанавливаем NOT NULL ограничение
ALTER TABLE users ALTER COLUMN password_hash SET NOT NULL;