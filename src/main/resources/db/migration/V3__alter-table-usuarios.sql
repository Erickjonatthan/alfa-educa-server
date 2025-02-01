DELETE FROM flyway_schema_history WHERE version = '3';
ALTER TABLE usuarios
ADD COLUMN nivel INT DEFAULT 0,
ADD COLUMN pontos INT DEFAULT 0;