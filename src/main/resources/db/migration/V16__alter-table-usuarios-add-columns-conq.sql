-- Adicionando colunas à tabela usuarios
ALTER TABLE usuarios ADD ultimo_login DATE NULL;
ALTER TABLE usuarios ADD atividades_concluidas INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE usuarios ADD dias_consecutivos INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE usuarios ADD primeira_resposta_correta BOOLEAN DEFAULT FALSE NOT NULL;

-- Atualizando valores existentes para garantir consistência
UPDATE usuarios
SET atividades_concluidas = 0,
    dias_consecutivos = 0,
    primeira_resposta_correta = FALSE
WHERE atividades_concluidas IS NULL OR dias_consecutivos IS NULL OR primeira_resposta_correta IS NULL;