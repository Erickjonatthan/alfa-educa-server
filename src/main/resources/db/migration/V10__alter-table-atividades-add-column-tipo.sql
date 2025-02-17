ALTER TABLE atividades ADD COLUMN tipo VARCHAR(255) NOT NULL DEFAULT 'AUDIO';

-- Atualizar as atividades existentes para definir um valor padrão, se necessário
UPDATE atividades SET tipo = 'AUDIO' WHERE tipo IS NULL;