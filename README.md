# 🎓 Alfa Educa - Sistema de Educação e Alfabetização

Um sistema completo de educação e alfabetização desenvolvido em Spring Boot com PostgreSQL, utilizando Docker para containerização e deploy.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [🐳 Configuração Docker](#-configuração-docker)
- [🎥 Vídeo de Instalação](#-vídeo-de-instalação)
- [⚙️ Pré-requisitos](#️-pré-requisitos)
- [🚀 Instalação e Execução](#-instalação-e-execução)
- [📊 Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [👥 Equipe Responsável](#-equipe-responsável)

## Sobre o Projeto

O Alfa Educa é uma plataforma de educação e alfabetização que oferece atividades interativas, sistema de conquistas e acompanhamento de progresso dos alunos. O sistema inclui funcionalidades de OCR (Reconhecimento Óptico de Caracteres) para análise de atividades escritas.

## 🐳 Configuração Docker - Análise Detalhada

### Arquitetura Completa dos Containers

O projeto utiliza Docker Compose para orquestrar dois serviços principais com uma arquitetura robusta e otimizada:

#### 1. Container PostgreSQL (`postgres_alfaeduca`)
- **Imagem**: `postgres:latest`
- **Nome do Container**: `postgres_alfaeducadb`
- **Porta**: `5432:5432` (interna do Docker)
- **Database**: `pg_alfaeduca`
- **Volume**: `postgres_data` para persistência de dados
- **Rede**: Rede interna do Docker Compose

**Configurações de Ambiente (via .env):**
- `POSTGRES_USER`: Usuário do banco de dados
- `POSTGRES_PASSWORD`: Senha do banco de dados
- `POSTGRES_DB`: Nome do banco de dados (`pg_alfaeduca`)

**Funcionamento:**
- O container PostgreSQL é iniciado primeiro
- Cria automaticamente o banco `pg_alfaeduca`
- Os dados são persistidos no volume `postgres_data`
- Fica disponível apenas internamente na rede Docker

#### 2. Container da Aplicação (`alfaeduca_app`)
- **Imagem**: Construída via Dockerfile personalizado (Multi-stage)
- **Nome do Container**: `alfaeduca_app`
- **Porta**: `8081:8081` (externa:interna)
- **Dependência**: `postgres_alfaeduca` (aguarda o banco estar pronto)
- **Profile Spring**: `prod` (produção)

### Dockerfile - Análise Completa e Detalhada

O Dockerfile utiliza **Multi-stage Build** para criar uma imagem otimizada e segura:

#### 🔨 STAGE 1 - BUILD (Compilação)
```dockerfile
FROM maven:3.8-openjdk-17 AS build
```

**Base:** Imagem completa do Maven com OpenJDK 17

**Processo de Build Otimizado:**

1. **Configuração do Workspace:**
   ```dockerfile
   WORKDIR /app
   ```
   - Define `/app` como diretório de trabalho

2. **Cache Otimizado de Dependências:**
   ```dockerfile
   COPY pom.xml .
   COPY .mvn/ .mvn/
   COPY mvnw mvnw
   COPY mvnw.cmd mvnw.cmd
   ```
   - Copia apenas arquivos de configuração Maven primeiro
   - Permite cache das dependências quando código muda

3. **Configuração Maven:**
   ```dockerfile
   ENV MAVEN_OPTS="-Dmaven.repo.local=/root/.m2/repository -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN"
   ```
   - Define repositório local Maven
   - Reduz logs verbosos de transfer

4. **Download de Dependências com Cache:**
   ```dockerfile
   RUN --mount=type=cache,target=/root/.m2/repository mvn dependency:go-offline -B
   ```
   - `--mount=type=cache`: Cache persistente entre builds
   - `dependency:go-offline`: Baixa todas as dependências
   - `-B`: Modo batch (não interativo)

5. **Cópia do Código Fonte:**
   ```dockerfile
   COPY src ./src
   ```
   - Copia código apenas após dependências (cache otimizado)

6. **Build e Testes:**
   ```dockerfile
   RUN --mount=type=cache,target=/root/.m2/repository mvn clean verify \
       -B \
       -Dspring.profiles.active=test \
       -Dmaven.test.failure.ignore=false \
       --no-transfer-progress
   ```
   - `clean verify`: Limpa e compila com testes
   - `spring.profiles.active=test`: Usa profile de teste
   - `maven.test.failure.ignore=false`: Falha se testes falharem
   - `--no-transfer-progress`: Reduz logs de download

#### 🚀 STAGE 2 - RUNTIME (Execução)
```dockerfile
FROM openjdk:17-jdk-slim
```

**Base:** OpenJDK 17 slim (imagem minimalista para produção)

**Configuração de Produção:**

1. **Workspace:**
   ```dockerfile
   WORKDIR /app
   ```

2. **Instalação do Tesseract OCR:**
   ```dockerfile
   RUN apt-get update && \
       apt-get install -y tesseract-ocr tesseract-ocr-por && \
       apt-get clean && \
       rm -rf /var/lib/apt/lists/*
   ```
   - `tesseract-ocr`: Engine de OCR para reconhecimento de texto
   - `tesseract-ocr-por`: Pacote de idioma português
   - `apt-get clean`: Remove cache de pacotes
   - `rm -rf /var/lib/apt/lists/*`: Remove listas de repositórios

3. **Cópia de Artefatos:**
   ```dockerfile
   COPY --from=build /app/target/alfaeduca-0.0.1-SNAPSHOT.jar app.jar
   COPY .env .env
   ```
   - `--from=build`: Copia do stage anterior
   - Copia apenas o JAR compilado (imagem final menor)
   - Copia arquivo de configuração `.env`

4. **Exposição de Porta:**
   ```dockerfile
   EXPOSE 8081
   ```
   - Documenta que aplicação usa porta 8081

5. **Comando de Inicialização:**
   ```dockerfile
   ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]
   ```
   - `spring.profiles.active=prod`: Usa configurações de produção
   - Executa o JAR da aplicação

**Funcionamento Detalhado:**

1. **Rede Interna Automática:**
   - Docker Compose cria rede privada
   - Containers se comunicam por nome do serviço
   - `alfaeduca_app` conecta em `postgres_alfaeduca:5432`

2. **Ordem de Inicialização:**
   - `depends_on` garante que PostgreSQL inicie primeiro
   - Aplicação aguarda banco estar disponível

3. **Persistência de Dados:**
   - Volume `postgres_data` persiste dados do banco
   - Dados sobrevivem a restart/recreação de containers

4. **Variáveis de Ambiente:**
   - Lidas do arquivo `.env` no diretório raiz
   - Injetadas automaticamente nos containers

#### Comunicação Entre Containers:

```
┌─────────────────┐    Rede Docker     ┌──────────────────┐
│   alfaeduca_app │ ──────────────────▶ │ postgres_alfaeduca│
│   (porta 8081)  │                     │   (porta 5432)   │
└─────────────────┘                     └──────────────────┘
        │                                        │
        ▼                                        ▼
    Host:8081                              Volume: postgres_data
```

## 🎥 Vídeo de Instalação

📺 **Tutorial Completo de Instalação do Container DO ZERO**

🔗 **Link do YouTube**: [Tutorial Alfa Educa Docker - Instalação Completa](https://youtu.be/NoWvlvgSqmI)

> **Nota**: O vídeo demonstra todo o processo de instalação desde a configuração inicial até a execução completa do sistema, incluindo:
> - Configuração das variáveis de ambiente
> - Build dos containers
> - Verificação dos serviços
> - Teste da aplicação

## ⚙️ Pré-requisitos

- Docker Desktop (Windows/Mac) ou Docker Engine (Linux)
- Docker Compose
- Git para clonar o repositório
- 4GB de RAM disponível
- 2GB de espaço em disco

## 🚀 Instalação e Execução

### Passo 1: Clone o Repositório
```bash
git clone https://github.com/seu-usuario/alfa-educa-server.git
cd alfa-educa-server
```

### Passo 2: Configure as Variáveis de Ambiente
Copie o arquivo de exemplo e configure as variáveis:
```bash
cp ./.env.example ./.env
nano ./.env
```
Edite o arquivo `.env` com suas configurações específicas (senhas, secrets, etc.)

### Passo 3: Execute o Docker Compose
```bash
docker-compose down --rmi all --volumes && docker-compose build --no-cache && docker-compose up --build```

### Passo 4: Verifique os Containers
```bash
docker-compose ps
```

### Passo 5: Acesse a Aplicação
- **API**: http://localhost:8081
- **Banco de Dados**: localhost:5434

### Comandos Úteis

#### Parar os containers
```bash
docker-compose down
```

#### Ver logs da aplicação
```bash
docker-compose logs alfaeduca_app
```

#### Ver logs do banco de dados
```bash
docker-compose logs postgres_alfaeduca
```

#### Rebuild da aplicação
```bash
docker-compose build --no-cache alfaeduca_app
docker-compose down --rmi all --volumes && docker-compose build --no-cache && docker-compose up --build```

## 📊 Tecnologias Utilizadas

### Backend
- **Spring Boot 3.3.5**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL** (Banco de dados)
- **Flyway** (Migrações de banco)
- **Maven** (Gerenciamento de dependências)

### DevOps
- **Docker & Docker Compose**
- **Multi-stage Dockerfile**
- **Tesseract OCR**

### Funcionalidades
- **Sistema de Autenticação JWT**
- **OCR para análise de atividades**
- **Sistema de conquistas**
- **Envio de emails**
- **API RESTful**

## 👥 Equipe Responsável

### 🐳 Responsável pela Configuração Docker
**Erick Jonathan Macedo dos Santos** - *Desenvolvedor DevOps*

**Responsabilidades:**
- Criação e configuração do Dockerfile
- Setup do docker-compose.yml
- Configuração do ambiente de produção
- Otimização das imagens Docker
- Documentação do processo de deploy
- Criação do vídeo tutorial

### Configurações Implementadas:
✅ Multi-stage build para otimização  
✅ Configuração de volumes para persistência  
✅ Network entre containers  
✅ Variáveis de ambiente seguras  
✅ OCR Tesseract integrado  
✅ Health checks configurados  

## 📝 Notas de Instalação

### Zero Configuração Necessária
Este projeto foi configurado para funcionar **OUT OF THE BOX** com o mínimo de configuração manual necessária. O professor precisa apenas:

1. Clonar o repositório
2. Criar o arquivo `.env` com as variáveis necessárias
3. Executar `docker-compose up --build`

### Adaptações Mínimas
- ✅ Variáveis de ambiente centralizadas no arquivo `.env`
- ✅ Ports não conflitantes (5434 para PostgreSQL, 8081 para aplicação)
- ✅ Volumes automáticos para persistência
- ✅ Dependencies configuradas no docker-compose
- ✅ Build automático da aplicação

## 📞 Contato e Suporte

Para dúvidas sobre a configuração Docker ou problemas na instalação, entre em contato com a equipe responsável.

**Projeto Alfa Educa** - Sistema de Educação e Alfabetização  
**Universidade**: UFRPE - UABJ 
**Disciplina**: Projeto Interdisciplinar 4
**Semestre**: 2025.1
