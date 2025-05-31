-- Adicionando colunas à tabela conquistas
ALTER TABLE conquistas ADD nivel_requerido INTEGER NULL;
ALTER TABLE conquistas ADD pontos_requeridos INTEGER NULL;
ALTER TABLE conquistas ADD atividades_requeridas INTEGER NULL;
ALTER TABLE conquistas ADD primeira_resposta_correta BOOLEAN NULL;
ALTER TABLE conquistas ADD dias_consecutivos_requeridos INTEGER NULL;