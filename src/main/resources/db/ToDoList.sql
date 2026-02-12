-- =====================================================
-- USERS
-- =====================================================

CREATE TABLE users
(
    user_id    BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)                           NOT NULL,
    email      VARCHAR(255)                          NOT NULL UNIQUE,
    password   VARCHAR(255)                          NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);


-- =====================================================
-- SECTIONS (Ordered per user via rank)
-- =====================================================

CREATE TABLE sections
(
    section_id   BIGSERIAL PRIMARY KEY,
    section_name VARCHAR(255)                          NOT NULL,
    rank         VARCHAR(50)                           NOT NULL,
    user_id      BIGINT                                NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT sections_user_fkey
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE,

    CONSTRAINT unique_section_rank_per_user
        UNIQUE (user_id, rank)
);


-- =====================================================
-- PARENT TASKS (Ordered per section via rank)
-- =====================================================

CREATE TABLE parent_tasks
(
    parent_task_id    BIGSERIAL PRIMARY KEY,
    parent_task_title VARCHAR(255)                          NOT NULL,
    deadline          TIMESTAMPTZ,
    priority          VARCHAR(10) DEFAULT 'LOW'             NOT NULL,
    is_completed      BOOLEAN     DEFAULT FALSE             NOT NULL,
    completed_at      TIMESTAMPTZ,
    rank              VARCHAR(50)                           NOT NULL,
    section_id        BIGINT                                NOT NULL,
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT parent_tasks_section_fkey
        FOREIGN KEY (section_id)
            REFERENCES sections (section_id)
            ON DELETE CASCADE,

    CONSTRAINT check_parent_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT unique_parent_rank_per_section
        UNIQUE (section_id, rank)
);


-- =====================================================
-- CHILD TASKS (Ordered per parent task via rank)
-- =====================================================

CREATE TABLE child_tasks
(
    child_task_id    BIGSERIAL PRIMARY KEY,
    child_task_title VARCHAR(255)                          NOT NULL,
    deadline         TIMESTAMPTZ,
    priority         VARCHAR(10) DEFAULT 'LOW'             NOT NULL,
    is_completed     BOOLEAN     DEFAULT FALSE             NOT NULL,
    completed_at     TIMESTAMPTZ,
    rank             VARCHAR(50)                           NOT NULL,
    parent_task_id   BIGINT                                NOT NULL,
    created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT child_tasks_parent_fkey
        FOREIGN KEY (parent_task_id)
            REFERENCES parent_tasks (parent_task_id)
            ON DELETE CASCADE,

    CONSTRAINT check_child_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT unique_child_rank_per_parent
        UNIQUE (parent_task_id, rank)
);

-- Sections
CREATE INDEX idx_sections_user_rank
    ON sections (user_id, rank);

CREATE INDEX idx_sections_user_updated
    ON sections (user_id, updated_at);


-- Parent tasks
CREATE INDEX idx_parent_tasks_section_rank
    ON parent_tasks (section_id, rank);

CREATE INDEX idx_parent_tasks_section_updated
    ON parent_tasks (section_id, updated_at);


-- Child tasks
CREATE INDEX idx_child_tasks_parent_rank
    ON child_tasks (parent_task_id, rank);

CREATE INDEX idx_child_tasks_parent_updated
    ON child_tasks (parent_task_id, updated_at);
