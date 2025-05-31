-- Re-add the foreign key with cascade delete
ALTER TABLE respostas 
ADD FOREIGN KEY (usuario_id) 
REFERENCES usuarios(id) 
ON DELETE CASCADE;
