# Software Design Document (SDD) - WorldCupLogin

## 📋 Visão Geral do Projeto

**Nome do Projeto:** WorldCupLogin  
**Versão:** 1.0  
**Data de Criação:** 2026  
**Linguagem Principal:** Kotlin  
**Plataforma:** Android (minSdk: 24, targetSdk: 36)  

## 🏗️ Arquitetura

### Padrão Arquitetural: Clean Architecture (MVVM)

O projeto segue a Clean Architecture dividida em 3 camadas principais:

```
┌─────────────────────────────────────────┐
│      PRESENTATION LAYER (UI)            │
│  - Activities                           │
│  - ViewModels                           │
│  - UI State Management                  │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│      DOMAIN LAYER (Business Logic)      │
│  - UseCases                             │
│  - Repository Interfaces                │
│  - Entities                             │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│      DATA LAYER (Persistence)           │
│  - Repository Implementations           │
│  - API Clients (Retrofit)               │
│  - Models & Mappers                     │
└─────────────────────────────────────────┘
```

### Estrutura de Pacotes

```
com/
├── example/worldcuplogin/     # App-level utilities
├── login/                      # Login Feature
│   ├── data/
│   │   ├── model/             # LoginModel (local data)
│   │   └── network/           # LoginApi, RetrofitFactory
│   ├── domain/
│   │   ├── repository/        # LoginRepository interface & impl
│   │   └── usecase/           # LoginUseCase
│   └── presentation/
│       ├── view/              # LoginActivity
│       └── viewmodel/         # LoginViewModel, state
├── teams/                      # Teams Feature
│   ├── data/
│   ├── domain/
│   ├── presentation/
│   └── adapter/               # TeamsAdapter (RecyclerView)
└── commons/                    # Shared utilities
    └── JsonReader             # JSON parsing utilities
```

## 🔄 Fluxo de Dados

```
User Input (LoginActivity)
    ↓
LoginViewModel.login()
    ↓
LoginUseCase.login()
    ↓
LoginRepository.login()
    ↓
LoginApi.getUsers() [Network Call via Retrofit]
    ↓
Response Processing & Mapping
    ↓
UI State Update via StateFlow
    ↓
LoginActivity observes state changes
    ↓
Navigate to TeamsActivity on success
```

## 🧰 Stack Tecnológico

### Framework & Core
- **Android SDK:** API 24+ (minSdk: 24, targetSdk: 36)
- **Kotlin:** 1.9+
- **Jetpack Compose:** UI Modern (habilitado em buildFeatures)
- **AppCompat:** UI tradicional (ViewBinding)

### Networking & Data
- **Retrofit 2:** HTTP client para consumir APIs
- **OkHttp3 com Logging Interceptor:** Request/Response logging
- **Gson:** Serialização/Desserialização JSON

### Reactive & Async
- **Kotlin Coroutines:** Operações assíncronas
- **StateFlow:** State management reactivo
- **Lifecycle-aware Coroutines:** Integração com Android Lifecycle

### UI & Architecture
- **MVVM:** Model-View-ViewModel pattern
- **ViewModel:** Lifecycle-aware component para lógica de UI
- **ViewBinding:** Type-safe binding de layouts
- **Material Design 3:** Componentes UI modernos

### Testing
- **JUnit 4:** Framework de testes unitários
- **Mockk:** Mocking em Kotlin
- **Kotlinx Coroutines Test:** Testes com coroutines
- **Turbine:** Testing flows
- **Espresso:** Testes instrumentados (UI)

### Code Quality
- **Detekt:** Análise estática de código Kotlin
- **Proguard:** Obfuscação e shrinking de código

## 📱 Features Principais

### 1. **Login Feature**
- Validação de entrada (usuario e senha não-vazios)
- Requisição HTTP GET para `/users` endpoint
- Estados de UI: Idle, Loading, Success, Error
- Transição para TeamsActivity após login bem-sucedido

### 2. **Teams Feature**
- Exibição de lista de times
- RecyclerView com TeamsAdapter
- Dados capturados e mapeados de resposta da API

## 🔌 Endpoints da API

### Base URL
```
https://jsonplaceholder.typicode.com/
```

### Login Service

| Método | Endpoint | Descrição | Resposta |
|--------|----------|-----------|----------|
| GET | `/users` | Fetch all users | `List<UserResponse>` |

**Exemplo de Resposta:**
```json
[
  {
    "id": 1,
    "name": "Leanne Graham",
    "username": "Bret",
    "email": "Sincere@april.biz"
  }
]
```

**Modelo de Resposta (UserResponse):**
```kotlin
data class UserResponse(
    val id: Int,
    val name: String,
    val username: String,
    val email: String
)
```

## 🎯 Estados de UI (StateFlow)

### LoginUIState
```kotlin
sealed class LoginUIState {
    object Idle : LoginUIState()
    object Loading : LoginUIState()
    data class Success(val userNamer: String) : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}
```

| Estado | Descrição | Ação na UI |
|--------|-----------|-----------|
| **Idle** | Estado inicial, nenhuma ação em progresso | Botão habilitado |
| **Loading** | Requisição em progresso | Botão desabilitado, spinner visível |
| **Success** | Login bem-sucedido | Navigate para TeamsActivity |
| **Error** | Erro na requisição | Toast com mensagem de erro |

## 🔐 Permissões Android

### Requeridas
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Opcionais (para conectividade)
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 📊 Diagrama de Sequência - Login Flow

```
┌─────────────────────────────────────────────────────────────┐
│ LoginActivity                                               │
│ User: "user123"                                             │
│ Password: "pass123"                                         │
└────────────────────┬────────────────────────────────────────┘
                     │ click Login Button
                     ↓
┌────────────────────────────────────────────────────────────┐
│ LoginViewModel                                              │
│ login(user, password)                                       │
│ _uiState = Loading                                          │
└────────────┬───────────────────────────────────────────────┘
             │
             ↓
┌────────────────────────────────────────────────────────────┐
│ LoginUseCase                                                │
│ login(user, password)                                       │
└────────────┬───────────────────────────────────────────────┘
             │
             ↓
┌────────────────────────────────────────────────────────────┐
│ LoginRepository                                             │
│ login(user, password)                                       │
└────────────┬───────────────────────────────────────────────┘
             │
             ↓
┌────────────────────────────────────────────────────────────┐
│ LoginApi (Retrofit)                                         │
│ GET /users                                                  │
└────────────┬───────────────────────────────────────────────┘
             │
             ↓
    [Network Request]
             │
             ↓
┌────────────────────────────────────────────────────────────┐
│ Response: List<UserResponse>                               │
│ Success: take first user name                              │
│ Error: catch exception                                      │
└────────────┬───────────────────────────────────────────────┘
             │
             ↓
┌────────────────────────────────────────────────────────────┐
│ LoginViewModel                                              │
│ _uiState = Success(name) or Error(message)                 │
└────────────┬───────────────────────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────────────────────────┐
│ LoginActivity                                               │
│ Observes state change → Navigate or Show Error              │
└─────────────────────────────────────────────────────────────┘
```

## ✅ Versão e Build Configuration

```
compileSdk = 36
minSdk = 24
targetSdk = 36
versionCode = 1
versionName = "1.0"
Java Target = 11 (VERSION_11)
Kotlin JVM Target = 11
```

## 📝 Build Types

### Debug
- Logging habilitado (HttpLoggingInterceptor.Level.BODY)
- Minify desabilitado
- Símbolos completos

### Release
- Logging desabilitado
- ProGuard habilitado (obfuscação)

## 🚀 Como Executar

### Build e Run
```bash
./gradlew assembleDebug      # Build APK debug
./gradlew installDebug       # Instalar em dispositivo/emulador
./gradlew runDebugTests      # Rodar testes unitários
./gradlew connectedAndroidTest # Rodar testes instrumentados
```

### Linting & Code Quality
```bash
./gradlew detekt             # Análise estática com Detekt
```

## 📚 Estrutura de Diretórios SDD

```
sdd/
├── README.md              # Este arquivo
├── docs/                  # Documentação técnica
│   ├── API_ENDPOINTS.md
│   ├── ARCHITECTURE.md
│   └── DIAGRAMS.md
├── skills/                # Processos e commits
│   ├── DEVELOPMENT_PROCESS.md
│   └── GIT_WORKFLOW.md
├── specs/                 # Features especificações
│   ├── LOGIN_FEATURE.md
│   └── TEAMS_FEATURE.md
└── tasks/                 # Breakdown de tarefas
    └── TASK_BREAKDOWN.md
```

---

**Última Atualização:** July 6, 2026  
**Autor:** Development Team

