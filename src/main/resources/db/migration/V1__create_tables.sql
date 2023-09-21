CREATE TABLE IF NOT EXISTS "securities"
(
    "id"            SERIAL PRIMARY KEY,
    "secid"         VARCHAR(255) NOT NULL UNIQUE,
    "regnumber"     VARCHAR(255) NOT NULL DEFAULT '',
    "name"          VARCHAR(255),
    "emitent_title" VARCHAR(512) NOT NULL DEFAULT ''
);

COMMENT ON TABLE "securities" IS 'This table holds all securities data.';

CREATE TABLE IF NOT EXISTS "securities_history"
(
    "secid"     VARCHAR(255) NOT NULL,
    "tradedate" DATE         NOT NULL,
    "numtrades" BIGINT       NOT NULL DEFAULT 0,
    "open"      REAL,
    CONSTRAINT "securities_history_pk" PRIMARY KEY ("secid", "tradedate"),
    CONSTRAINT "securities_history_securities_id_fk" FOREIGN KEY ("secid") REFERENCES "securities" ("secid") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_securities_history_secid ON securities_history (secid);

COMMENT ON TABLE "securities_history" IS 'This table holds all translations history for all securities. Deletions and updates of securities are cascaded.';
