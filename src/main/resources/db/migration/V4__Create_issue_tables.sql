CREATE TABLE issue (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    type VARCHAR(20),
    status VARCHAR(20),
    priority VARCHAR(20),
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT,
    project_id BIGINT NOT NULL,
    sprint_id BIGINT,
    assignee_id BIGINT,
    reporter_id BIGINT,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (sprint_id) REFERENCES sprints(id) ON DELETE SET NULL,
    FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE label (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE issue_label (
    issue_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    PRIMARY KEY (issue_id, label_id),
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (label_id) REFERENCES label(id) ON DELETE CASCADE
);

CREATE TABLE issue_comment (
    id BIGSERIAL PRIMARY KEY,
    content TEXT,
    issue_id BIGINT NOT NULL,
    author_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE issue_history (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20),
    old_assignee_name VARCHAR(100),
    new_assignee_name VARCHAR(100),
    changed_by_id BIGINT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_issue_project_id ON issue(project_id);
CREATE INDEX idx_issue_sprint_id ON issue(sprint_id);
CREATE INDEX idx_issue_assignee_id ON issue(assignee_id);
CREATE INDEX idx_issue_comment_issue_id ON issue_comment(issue_id);
CREATE INDEX idx_issue_history_issue_id ON issue_history(issue_id);
