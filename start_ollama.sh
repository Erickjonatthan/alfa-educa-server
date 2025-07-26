#!/bin/bash

echo "Iniciando serviço Ollama..."

# Baixar modelo se especificado
if [ ! -z "$GDRIVE_FILE_ID" ] && [ ! -z "$MODEL_NAME" ]; then
    echo "Baixando modelo do Google Drive..."
    /usr/local/bin/download_model.sh
fi

# Iniciar Ollama em background
ollama serve &

# Aguardar o Ollama inicializar
sleep 10

# Se um modelo foi baixado, importá-lo
if [ ! -z "$MODEL_NAME" ] && [ -f "/root/.ollama/models/$MODEL_NAME" ]; then
    echo "Importando modelo $MODEL_NAME..."
    ollama create "$MODEL_NAME" -f "/root/.ollama/models/$MODEL_NAME"
fi

# Se um modelo padrão foi especificado, baixá-lo
if [ ! -z "$DEFAULT_MODEL" ]; then
    echo "Baixando modelo padrão: $DEFAULT_MODEL"
    ollama pull "$DEFAULT_MODEL"
fi

# Manter o container rodando
wait