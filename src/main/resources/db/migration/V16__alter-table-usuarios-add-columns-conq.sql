-- Adicionando colunas à tabela usuarios
ALTER TABLE usuarios
ADD COLUMN ultimo_login DATE NULL,
ADD COLUMN atividades_concluidas INTEGER DEFAULT 0 NOT NULL,
ADD COLUMN dias_consecutivos INTEGER DEFAULT 0 NOT NULL,
ADD COLUMN primeira_resposta_correta BOOLEAN DEFAULT FALSE NOT NULL;

-- Atualizando valores existentes para garantir consistência
UPDATE usuarios
SET atividades_concluidas = 0,
    dias_consecutivos = 0,
    primeira_resposta_correta = FALSE
WHERE atividades_concluidas IS NULL OR dias_consecutivos IS NULL OR primeira_resposta_correta IS NULL;