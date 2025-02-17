ALTER TABLE respostas
ADD CONSTRAINT fk_usuario
FOREIGN KEY (usuario_id)
REFERENCES usuarios(id)
ON DELETE CASCADE;

ALTER TABLE respostas
ADD CONSTRAINT fk_atividade
FOREIGN KEY (atividade_id)
REFERENCES atividades(id)
ON DELETE CASCADE;