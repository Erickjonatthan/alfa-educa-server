#!/bin/bash

# Função para baixar arquivo do Google Drive
download_gdrive_file() {
    local file_id="$1"
    local output_file="$2"
    
    echo "Baixando modelo do Google Drive..."
    
    # Primeira tentativa de download
    wget --load-cookies /tmp/cookies.txt "https://docs.google.com/uc?export=download&confirm=$(wget --quiet --save-cookies /tmp/cookies.txt --keep-session-cookies --no-check-certificate 'https://docs.google.com/uc?export=download&id='$file_id -O- | sed -rn 's/.*confirm=([0-9A-Za-z_]+).*/\1\n/p')&id=$file_id" -O "$output_file" && rm -rf /tmp/cookies.txt
    
    if [ $? -eq 0 ]; then
        echo "Download concluído com sucesso!"
        return 0
    else
        echo "Erro no download. Tentando método alternativo..."
        # Método alternativo usando gdown (se disponível)
        if command -v gdown &> /dev/null; then
            gdown "https://drive.google.com/uc?id=$file_id" -O "$output_file"
        else
            echo "Instalando gdown..."
            pip install gdown
            gdown "https://drive.google.com/uc?id=$file_id" -O "$output_file"
        fi
    fi
}

# Verificar se as variáveis de ambiente estão definidas
if [ -z "$GDRIVE_FILE_ID" ]; then
    echo "Erro: GDRIVE_FILE_ID não está definido"
    exit 1
fi

if [ -z "$MODEL_NAME" ]; then
    echo "Erro: MODEL_NAME não está definido"
    exit 1
fi

# Baixar o modelo
download_gdrive_file "$GDRIVE_FILE_ID" "/tmp/$MODEL_NAME"

# Verificar se o download foi bem-sucedido
if [ -f "/tmp/$MODEL_NAME" ]; then
    echo "Modelo baixado com sucesso: /tmp/$MODEL_NAME"
    # Mover para o diretório correto do Ollama
    mv "/tmp/$MODEL_NAME" "/root/.ollama/models/"
else
    echo "Erro: Falha no download do modelo"
    exit 1
fi