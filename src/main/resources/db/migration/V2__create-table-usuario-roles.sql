CREATE TABLE usuario_roles (
    usuario_id UUID,
    roles VARCHAR(255),
    PRIMARY KEY (usuario_id, roles),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);