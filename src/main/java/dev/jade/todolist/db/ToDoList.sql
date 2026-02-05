DO
$$
    BEGIN
        CREATE TYPE priorities AS ENUM (
            'LOW',
            'MEDIUM',
            'HIGH'
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
    section_id    BIGSERIAL PRIMARY KEY,
    section_name  VARCHAR(50) NOT NULL,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    display_order INTEGER     NOT NULL,
    user_id       BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS parent_tasks
(
    parent_task_id    BIGSERIAL PRIMARY KEY,
    parent_task_title VARCHAR(255) NOT NULL,
    deadline          TIMESTAMP             DEFAULT NULL,
    priority          priorities   NOT NULL DEFAULT 'low',
    is_completed      BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at      TIMESTAMP             DEFAULT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    display_order     INTEGER      NOT NULL,
    section_id        BIGINT       NOT NULL REFERENCES sections (section_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS child_tasks
(
    child_task_id    BIGSERIAL PRIMARY KEY,
    child_task_title VARCHAR(255) NOT NULL,
    deadline         TIMESTAMP             DEFAULT NULL,
    priority         priorities   NOT NULL DEFAULT 'low',
    is_completed     BOOLEAN      NOT NULL DEFAULT FALSE,
    completed_at     TIMESTAMP             DEFAULT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    display_order    INTEGER      NOT NULL,
    parent_task_id   BIGINT       NOT NULL REFERENCES parent_tasks (parent_task_id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at
        = CURRENT_TIMESTAMP;
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DO
$$
    BEGIN
        CREATE
            OR REPLACE TRIGGER update_users_updated_at
            BEFORE
                UPDATE
            ON users
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        CREATE
            OR REPLACE TRIGGER update_sections_updated_at
            BEFORE
                UPDATE
            ON sections
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        CREATE
            OR REPLACE TRIGGER update_parent_tasks_updated_at
            BEFORE
                UPDATE
            ON parent_tasks
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();

        CREATE
            OR REPLACE TRIGGER update_child_tasks_updated_at
            BEFORE
                UPDATE
            ON child_tasks
            FOR EACH ROW
        EXECUTE FUNCTION update_updated_at_column();
    END
$$;


SELECT users.user_id,
       users.username,
       sections.section_id,
       sections.display_order     AS section_display_order,
       parent_tasks.parent_task_id,
       parent_tasks.parent_task_title,
       parent_tasks.priority      AS parent_priority,
       parent_tasks.display_order AS parent_display_order,
       child_tasks.child_task_id,
       child_tasks.child_task_title,
       child_tasks.priority       AS child_priority,
       child_tasks.display_order  AS child_display_order
FROM users
         FULL JOIN sections ON users.user_id = sections.user_id
         FULL JOIN parent_tasks ON sections.section_id = parent_tasks.section_id
         FULL JOIN child_tasks ON parent_tasks.parent_task_id = child_tasks.parent_task_id;