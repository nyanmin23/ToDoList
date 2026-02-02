DO
$$
    BEGIN
        CREATE TYPE priorities AS ENUM (
            'low',
            'medium',
            'high'
            );
    EXCEPTION
        WHEN duplicate_object THEN NULL;
    END
$$;

CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(128) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sections
(
    section_id BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id    BIGINT      NOT NULL REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS parent_tasks
(
    parent_task_id BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    deadline       TIMESTAMP             DEFAULT NULL,
    priority       priorities   NOT NULL DEFAULT 'low',
    completed      BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at   TIMESTAMP             DEFAULT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    section_id     BIGINT       NOT NULL REFERENCES sections (section_id)
);

CREATE TABLE IF NOT EXISTS child_tasks
(
    child_task_id  BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    deadline       TIMESTAMP             DEFAULT NULL,
    priority       priorities   NOT NULL DEFAULT 'low',
    completed      BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at   TIMESTAMP             DEFAULT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    parent_task_id BIGINT       NOT NULL REFERENCES parent_tasks (parent_task_id)
);

CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DO
$$
    BEGIN
        CREATE OR REPLACE TRIGGER update_users_updated_at
            BEFORE UPDATE
            ON users
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        CREATE OR REPLACE TRIGGER update_sections_updated_at
            BEFORE UPDATE
            ON sections
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        CREATE OR REPLACE TRIGGER update_parent_tasks_updated_at
            BEFORE UPDATE
            ON parent_tasks
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        CREATE OR REPLACE TRIGGER update_child_tasks_updated_at
            BEFORE UPDATE
            ON child_tasks
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();
    END
$$;