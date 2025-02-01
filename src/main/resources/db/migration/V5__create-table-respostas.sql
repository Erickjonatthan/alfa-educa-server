CREATE TABLE respostas (
    id UUID PRIMARY KEY,
    resposta TEXT NOT NULL,
    atividade_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    FOREIGN KEY (atividade_id) REFERENCES atividades(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);