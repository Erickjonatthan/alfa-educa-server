-- Adicionando colunas à tabela conquistas
ALTER TABLE conquistas
ADD COLUMN nivel_requerido INTEGER NULL,
ADD COLUMN pontos_requeridos INTEGER NULL,
ADD COLUMN atividades_requeridas INTEGER NULL,
ADD COLUMN primeira_resposta_correta BOOLEAN NULL,
ADD COLUMN dias_consecutivos_requeridos INTEGER NULL;