CREATE TABLE atividades (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    nivel INT NOT NULL,
    pontos INT NOT NULL,
    resposta_correta VARCHAR(255) NOT NULL
);