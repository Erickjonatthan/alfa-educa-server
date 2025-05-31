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

## 🐳 Configuração Docker

### Arquitetura dos Containers

O projeto utiliza Docker Compose para orquestrar dois serviços principais:

#### 1. Container PostgreSQL (`postgres_alfaeduca`)
- **Imagem**: `postgres:latest`
- **Nome do Container**: `postgres_alfaeducadb`
- **Porta**: `5434:5432` (porta externa:interna)
- **Database**: `pg_alfaeduca`
- **Volume**: `postgres_data` para persistência de dados

**Configurações de Ambiente:**
- `POSTGRES_USER`: Usuário do banco de dados
- `POSTGRES_PASSWORD`: Senha do banco de dados
- `POSTGRES_DB`: Nome do banco de dados

#### 2. Container da Aplicação (`alfaeduca_app`)
- **Imagem**: Construída via Dockerfile personalizado
- **Nome do Container**: `alfaeduca_app`
- **Porta**: `8081:8081`
- **Dependência**: `postgres_alfaeduca`

### Dockerfile - Análise Detalhada

O Dockerfile utiliza uma abordagem multi-stage build para otimizar o tamanho final da imagem:

#### Stage 1 - Build
```dockerfile
FROM ubuntu:latest AS build
```
- **Base**: Ubuntu latest para compilação
- **Ferramentas instaladas**:
  - OpenJDK 17
  - Tesseract OCR com suporte ao português
  - Maven para build do projeto

#### Stage 2 - Runtime
```dockerfile
FROM openjdk:17-jdk-slim
```
- **Base**: OpenJDK 17 slim (imagem otimizada)
- **Funcionalidades**:
  - Copia apenas o JAR compilado
  - Instala Tesseract OCR para produção
  - Configura variáveis de ambiente via arquivo `.env`

### Componentes e Configurações

#### 1. Banco de Dados
- **Sistema**: PostgreSQL
- **Versão**: Latest
- **Persistência**: Volume Docker para dados
- **Migrações**: Flyway para versionamento do schema

#### 2. Aplicação Spring Boot
- **Framework**: Spring Boot 3.3.5
- **Java**: OpenJDK 17
- **Build Tool**: Maven
- **OCR**: Tesseract com suporte ao português

#### 3. Variáveis de Ambiente Necessárias
```env
DATABASE_USER=seu_usuario
DATABASE_PASSWORD=sua_senha
MAIL_PASSWORD=senha_do_email
JWT_SECRET=seu_jwt_secret
ADMIN_EMAILS=admin@email.com
CORS_ORIGIN=http://localhost:8082
```

## 🎥 Vídeo de Instalação

📺 **Tutorial Completo de Instalação do Container DO ZERO**

🔗 **Link do YouTube**: [Tutorial Alfa Educa Docker - Instalação Completa](https://www.youtube.com/watch?v=SEU_VIDEO_ID)

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
docker-compose up -d
```

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
docker-compose up -d
```

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
3. Executar `docker-compose up -d`

### Adaptações Mínimas
- ✅ Variáveis de ambiente centralizadas no arquivo `.env`
- ✅ Ports não conflitantes (5434 para PostgreSQL, 8081 para aplicação)
- ✅ Volumes automáticos para persistência
- ✅ Dependencies configuradas no docker-compose
- ✅ Build automático da aplicação

### Troubleshooting

#### Container não inicia
```bash
# Verificar logs
docker-compose logs

# Rebuild completo
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

#### Banco de dados não conecta
```bash
# Verificar se o container PostgreSQL está rodando
docker-compose ps

# Verificar logs do banco
docker-compose logs postgres_alfaeduca
```

---

## 📞 Contato e Suporte

Para dúvidas sobre a configuração Docker ou problemas na instalação, entre em contato com a equipe responsável.

**Projeto Alfa Educa** - Sistema de Educação e Alfabetização  
**Universidade**: UFRPE - UABJ 
**Disciplina**: Projeto Interdisciplinar 4
**Semestre**: 2025.1
