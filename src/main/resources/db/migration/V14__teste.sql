ALTER TABLE respostas DROP CONSTRAINT respostas_usuario_id_fkey;
ALTER TABLE respostas
ADD CONSTRAINT respostas_usuario_id_fkey 
FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;
