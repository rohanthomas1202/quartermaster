CREATE TABLE org_settings (
    org_id    VARCHAR(100) PRIMARY KEY,
    timezone  VARCHAR(50) NOT NULL DEFAULT 'America/New_York'
);

CREATE TABLE rally_cries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    org_id          VARCHAR(100) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE defining_objectives (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rally_cry_id    UUID NOT NULL REFERENCES rally_cries(id),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    owner_id        VARCHAR(100) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE outcomes (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    defining_objective_id UUID NOT NULL REFERENCES defining_objectives(id),
    title                 VARCHAR(255) NOT NULL,
    description           TEXT,
    measurable_target     VARCHAR(255),
    current_value         VARCHAR(255),
    owner_id              VARCHAR(100) NOT NULL,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE weekly_commits (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         VARCHAR(100) NOT NULL,
    org_id          VARCHAR(100) NOT NULL,
    week_start_date DATE NOT NULL,
    week_end_date   DATE NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                    CHECK (status IN ('DRAFT', 'LOCKED', 'RECONCILING', 'RECONCILED')),
    locked_at       TIMESTAMPTZ,
    reconciled_at   TIMESTAMPTZ,
    version         INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, week_start_date)
);

CREATE TABLE commit_items (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    weekly_commit_id  UUID NOT NULL REFERENCES weekly_commits(id),
    title             VARCHAR(500) NOT NULL,
    description       TEXT,
    outcome_id        UUID NOT NULL REFERENCES outcomes(id),
    urgency           VARCHAR(4) NOT NULL CHECK (urgency IN ('HIGH', 'LOW')),
    importance        VARCHAR(4) NOT NULL CHECK (importance IN ('HIGH', 'LOW')),
    sort_order        INTEGER NOT NULL DEFAULT 0,
    completion_status VARCHAR(10) NOT NULL DEFAULT 'PENDING'
                      CHECK (completion_status IN ('PENDING', 'COMPLETED', 'PARTIAL', 'NOT_DONE')),
    completion_notes  TEXT,
    carried_from_id   UUID REFERENCES commit_items(id),
    version           INTEGER NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_rally_cries_org ON rally_cries(org_id);
CREATE INDEX idx_weekly_commits_user_week ON weekly_commits(user_id, week_start_date);
CREATE INDEX idx_weekly_commits_org_status ON weekly_commits(org_id, status);
CREATE INDEX idx_commit_items_weekly ON commit_items(weekly_commit_id);
