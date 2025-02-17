-- Criação da tabela conquistas
CREATE TABLE conquistas (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    img_conquista BYTEA
);

-- Criação da tabela de relacionamento entre usuários e conquistas
CREATE TABLE usuario_conquistas (
    usuario_id UUID NOT NULL,
    conquista_id UUID NOT NULL,
    PRIMARY KEY (usuario_id, conquista_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (conquista_id) REFERENCES conquistas(id) ON DELETE CASCADE
);