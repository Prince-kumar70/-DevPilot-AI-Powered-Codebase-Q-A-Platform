# 🚀 DevPilot

> **AI-Powered GitHub Repository Assistant & Code Intelligence Engine**  
> Connect your GitHub repositories, index your codebase with vector search, and chat intelligently with your code with source citations in real time.

---

## 🌟 Overview

**DevPilot** is a full-stack, AI-native developer companion designed to make exploring, understanding, and debugging complex codebases effortless. By combining **Spring AI**, **Google Gemini**, and **PostgreSQL with pgvector**, DevPilot builds a semantic index of your repository and provides context-aware answers directly tied to your source files.

---

## ✨ Features

- 🔐 **GitHub OAuth 2.0 Authentication**  
  Authenticate directly with your GitHub account to access both public and authorized private repositories.
- ⚡ **Intelligent Code Ingestion & Chunking**  
  Scans repository file trees, excludes non-essential assets/binaries, and splits source code into semantically coherent chunks.
- 🧠 **Vector Embeddings & Semantic Search (RAG)**  
  Generates 1536-dimensional embeddings using Google Gemini (`gemini-embedding-001`) and stores them in PostgreSQL using `pgvector` with HNSW cosine-distance indexing.
- 💬 **Context-Grounded Code Chat**  
  Leverages `gemini-3.6-flash` via Spring AI to synthesize accurate answers based strictly on retrieved code context.
- 📌 **Source Citations & Deep Linking**  
  Every answer includes clickable citation chips linking to the exact files and lines used during generation.
- 🌊 **Real-Time SSE Streaming**  
  Server-Sent Events (SSE) power instant token-by-token streaming responses with rich syntax highlighting.
- 🎨 **Modern, Responsive UI**  
  Built with Next.js 16 (App Router), React 19, Tailwind CSS v4, and Radix/Shadcn primitives with full dark/light theme support.

---

## 🛠️ Tech Stack

### **Backend**
- **Language**: Java 21
- **Framework**: Spring Boot 4.1.0
- **AI Integration**: Spring AI 2.0.0 (OpenAI-compatible client for Gemini + pgvector vector store)
- **Security**: Spring Security & Spring OAuth2 Client
- **Database Access**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL 16 + `pgvector` extension

### **Frontend**
- **Framework**: Next.js 16 (App Router, Turbopack)
- **UI Library**: React 19, TypeScript
- **Styling**: Tailwind CSS v4, Lucide Icons, Shadcn UI
- **State & Data Fetching**: TanStack React Query v5
- **Markdown & Streaming**: `streamdown`, `@streamdown/code`

### **Infrastructure**
- **Containerization**: Docker & Docker Compose

---

## 📁 Repository Structure

```text
devPilot/
├── backend/                         # Spring Boot backend application
│   ├── src/main/java/devPilot/backend/
│   │   ├── config/                  # Security, CORS, AI & Crypto configuration
│   │   ├── controllers/             # REST controllers (Auth, Chat, Repos)
│   │   ├── dto/                     # Data transfer objects & schemas
│   │   ├── entity/                  # JPA entities (User, Repository, ChatSession, etc.)
│   │   ├── exceptions/              # Global error handling
│   │   ├── repository/              # Spring Data JPA repositories
│   │   ├── security/                # OAuth2 user services and token principals
│   │   └── services/
│   │       ├── ai/                  # RAG context retriever, prompt builder & streaming
│   │       ├── github/              # GitHub API client with rate limiting
│   │       └── indexing/            # Code chunking, file filtering & vector indexing
│   ├── src/main/resources/
│   │   └── application.yml          # Core Spring Boot configuration
│   ├── pom.xml                      # Maven dependencies and build setup
│   └── mvnw / mvnw.cmd              # Maven wrappers
├── client/                          # Next.js 16 frontend application
│   ├── app/                         # Next.js App Router (auth, chat, dashboard, login)
│   ├── components/                  # UI components, chat interfaces & sidebar
│   ├── hooks/                       # Custom React hooks (useChat, useRepos, useAuth)
│   ├── lib/                         # API utilities & query client configuration
│   ├── package.json                 # Node dependencies & scripts
│   └── tsconfig.json                # TypeScript configuration
├── docker/
│   └── postgres/
│       └── init-extensions.sql      # PostgreSQL extension initializer (vector, hstore, uuid)
├── docker-compose.yml               # PostgreSQL + pgvector container definition
├── .env.example                     # Root environment variable template for Docker
└── README.md                        # Project documentation
```

---

## 📋 Prerequisites

Ensure you have installed:
- [Git](https://git-scm.com/)
- [Java 21 JDK](https://adoptium.net/)
- [Node.js 20+ & npm](https://nodejs.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

You will also need:
1. **GitHub OAuth App**:
   - Go to [GitHub Developer Settings](https://github.com/settings/developers) -> **OAuth Apps** -> **New OAuth App**.
   - **Application Name**: `DevPilot`
   - **Homepage URL**: `http://localhost:3000`
   - **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`
   - Save your **Client ID** and generate a **Client Secret**.
2. **Google Gemini API Key**:
   - Generate an API key from [Google AI Studio](https://aistudio.google.com/).

---

## ⚙️ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/<your-username>/devpilot.git
cd devpilot
```

### 2. Configure Environment Variables

#### Root Docker Environment
Copy the example file to `.env`:
```bash
cp .env.example .env
```
*(Optionally change `POSTGRES_PASSWORD` or leave defaults for local development).*

#### Backend Environment
In `backend/`, copy `.env.example` or set the following system environment variables:
```bash
# GitHub OAuth
export GITHUB_CLIENT_ID="your_github_client_id"
export GITHUB_CLIENT_SECRET="your_github_client_secret"

# Google Gemini
export GEMINI_API_KEY="your_gemini_api_key"

# Security & Encryption (choose secure arbitrary strings)
export TOKEN_ENCRYPTOR_PASSWORD="your_custom_encryption_password"
export TOKEN_ENCRYPTOR_SALT="your_16_char_salt"
```

*(On Windows PowerShell, use `$env:GEMINI_API_KEY="your_key"`, etc.)*

---

### 3. Start PostgreSQL with pgvector

Launch the database container:
```bash
docker compose up -d
```

Verify that the database is healthy:
```bash
docker compose ps
```
The container exposes PostgreSQL on port `5433` (mapped from container port `5432`).

---

### 4. Start the Backend Service

Navigate to the `backend` directory and run:

```bash
cd backend
# Windows:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```

The backend server will start on `http://localhost:8080`.

---

### 5. Start the Frontend Client

In a new terminal window, navigate to `client`:

```bash
cd client
npm install
npm run dev
```

Open your browser and navigate to **[http://localhost:3000](http://localhost:3000)**.

---

## 🔑 Environment Variables Reference

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `POSTGRES_DB` | PostgreSQL database name | `devpilot` |
| `POSTGRES_USER` | PostgreSQL user | `postgres` |
| `POSTGRES_PASSWORD` | PostgreSQL password | `postgres` |
| `GITHUB_CLIENT_ID` | GitHub OAuth application Client ID | *Required* |
| `GITHUB_CLIENT_SECRET`| GitHub OAuth application Client Secret | *Required* |
| `GEMINI_API_KEY` | Google Gemini API Key | *Required* |
| `TOKEN_ENCRYPTOR_PASSWORD` | Encryption key for securing GitHub OAuth tokens | `devpilot_default_dev_password` |
| `TOKEN_ENCRYPTOR_SALT` | Salt used in token encryption | `a1b2c3d4e5f67890` |

---

## 📡 API Overview

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/auth/me` | Fetch authenticated user profile & session |
| `GET` | `/api/repos` | List connected repositories |
| `POST` | `/api/repos/sync` | Sync and index a repository into the vector database |
| `GET` | `/api/repos/{id}/status` | Get indexing progress and document count |
| `POST` | `/api/chat/session` | Create or fetch a chat session for a repository |
| `POST` | `/api/chat/{sessionId}/stream` | Stream chat query with RAG context & citations via SSE |

---

## 🔒 Security & Best Practices

- **Never Commit Secrets**: Keep `.env` files untracked. Only commit template files like `.env.example`.
- **Token Protection**: User OAuth tokens are encrypted at rest using AES-based password and salt encryption before storing in the database.
- **Port Mapping**: Docker uses port `5433` to prevent collision with locally installed PostgreSQL instances.

---

## 🤝 Contributing

Contributions are welcome!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
