ALTER TABLE atividades 
ADD COLUMN subtitulo VARCHAR(255);

-- Atualize os registros existentes com um valor padrão
UPDATE atividades
SET subtitulo = 'Valor Padrão'
WHERE subtitulo IS NULL;

-- Alterar a coluna para NOT NULL
ALTER TABLE atividades
ALTER COLUMN subtitulo SET NOT NULL;