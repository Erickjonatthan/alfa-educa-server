ALTER TABLE usuario_roles
ADD CONSTRAINT fk_usuario
FOREIGN KEY (usuario_id)
REFERENCES usuarios(id)
ON DELETE CASCADE;