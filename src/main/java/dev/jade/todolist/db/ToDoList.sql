DO
$$
    BEGIN
        CREATE TYPE priorities AS ENUM (
            'goblin', -- low
            'minotaur', -- medium
            'dragon' -- high
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
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sections
(
    section_id   BIGSERIAL PRIMARY KEY,
    section_name VARCHAR(50) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id      BIGINT      NOT NULL REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS parent_tasks
(
    parent_id    BIGSERIAL PRIMARY KEY,
    description  VARCHAR(255) NOT NULL,
    deadline     TIMESTAMPTZ           DEFAULT NULL,
    priority     priorities   NOT NULL DEFAULT 'goblin',
    is_completed BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    section_id   BIGINT       NOT NULL REFERENCES sections (section_id)
);

CREATE TABLE IF NOT EXISTS child_tasks
(
    child_id     BIGSERIAL PRIMARY KEY,
    description  VARCHAR(255) NOT NULL,
    deadline     TIMESTAMPTZ           DEFAULT NULL,
    priority     priorities   NOT NULL DEFAULT 'goblin',
    is_completed BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    parent_id    BIGINT       NOT NULL REFERENCES parent_tasks (parent_id)
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
        DROP TRIGGER IF EXISTS update_sections_updated_at ON sections;
        CREATE TRIGGER update_sections_updated_at
            BEFORE UPDATE
            ON sections
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        DROP TRIGGER IF EXISTS update_parent_tasks_updated_at ON parent_tasks;
        CREATE TRIGGER update_parent_tasks_updated_at
            BEFORE UPDATE
            ON parent_tasks
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        DROP TRIGGER IF EXISTS update_child_tasks_updated_at ON child_tasks;
        CREATE TRIGGER update_child_tasks_updated_at
            BEFORE UPDATE
            ON child_tasks
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();
    END
$$;