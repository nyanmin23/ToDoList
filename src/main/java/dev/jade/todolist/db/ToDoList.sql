CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)                           NOT NULL,
    email      VARCHAR(255)                          NOT NULL UNIQUE,
    password   VARCHAR(128)                          NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS sections
(
    section_id    BIGSERIAL PRIMARY KEY,
    section_name  VARCHAR(255)                          NOT NULL,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    display_order INTEGER                               NOT NULL,
    user_id       BIGINT                                NOT NULL,
    CONSTRAINT unique_section_display_order_per_user UNIQUE (user_id, display_order),
    CONSTRAINT sections_user_id_fkey FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS parent_tasks
(
    parent_task_id    BIGSERIAL PRIMARY KEY,
    parent_task_title VARCHAR(255)                          NOT NULL,
    deadline          TIMESTAMPTZ DEFAULT NULL,
    priority          VARCHAR(10) DEFAULT 'LOW'             NOT NULL,
    is_completed      BOOLEAN     DEFAULT FALSE             NOT NULL,
    completed_at      TIMESTAMPTZ DEFAULT NULL,
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    display_order     INTEGER                               NOT NULL,
    section_id        BIGINT                                NOT NULL,
    CONSTRAINT check_parent_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT unique_parent_display_order_per_section UNIQUE (section_id, display_order),
    CONSTRAINT parent_tasks_section_id_fkey FOREIGN KEY (section_id) REFERENCES sections (section_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS child_tasks
(
    child_task_id    BIGSERIAL PRIMARY KEY,
    child_task_title VARCHAR(255)                          NOT NULL,
    deadline         TIMESTAMPTZ DEFAULT NULL,
    priority         VARCHAR(10) DEFAULT 'LOW'             NOT NULL,
    is_completed     BOOLEAN     DEFAULT FALSE             NOT NULL,
    completed_at     TIMESTAMPTZ DEFAULT NULL,
    created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    display_order    INTEGER                               NOT NULL,
    parent_task_id   BIGINT                                NOT NULL,
    CONSTRAINT check_child_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT unique_child_display_order_per_parent UNIQUE (parent_task_id, display_order),
    CONSTRAINT child_tasks_parent_task_id_fkey FOREIGN KEY (parent_task_id) REFERENCES parent_tasks (parent_task_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sections_user_id ON sections (user_id);
CREATE INDEX IF NOT EXISTS idx_parent_tasks_section_id ON parent_tasks (section_id);
CREATE INDEX IF NOT EXISTS idx_child_tasks_parent_task_id ON child_tasks (parent_task_id);

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

ALTER TABLE users
    ALTER COLUMN password TYPE VARCHAR(255);