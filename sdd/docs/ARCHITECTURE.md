# Architecture Overview

## Clean Architecture Pattern

The project follows **Clean Architecture** principles with clear separation of concerns across three main layers:

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                            │
│            (Activities, Fragments, ViewModels, UI State)         │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                    (UseCase Dependency)
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                      DOMAIN LAYER                                │
│         (Business Logic, UseCases, Repository Interfaces)        │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                    (Repository Dependency)
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                       DATA LAYER                                 │
│    (Repository Implementation, API Clients, Local Storage)       │
└─────────────────────────────────────────────────────────────────┘
```

## Layers Description

### 1. **Presentation Layer**

**Responsibility:** Handle user interaction and display data  
**Components:**
- **Activities:** `LoginActivity`, `TeamsActivity`
- **ViewModels:** `LoginViewModel`, `TeamViewModel`
- **UI State:** `LoginUIState`, `TeamUIState` (sealed classes)
- **Adapters:** `TeamsAdapter` (RecyclerView binding)

**Key Features:**
- UI state management via `StateFlow`
- Lifecycle-aware coroutine scopes
- User input validation
- Navigation between screens

**Example:**
```kotlin
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }
    
    private fun loginObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginUIState.Loading -> showLoading()
                        is LoginUIState.Success -> navigateToTeams(state.userNamer)
                        is LoginUIState.Error -> showError(state.message)
                        else -> {}
                    }
                }
            }
        }
    }
}
```

---

### 2. **Domain Layer**

**Responsibility:** Encapsulate business logic and define contracts  
**Components:**
- **UseCases:** `LoginUseCase`, `TeamUseCase`
- **Repository Interfaces:** `LoginRepository`, `TeamRepository`
- **Entities:** Domain models (separate from data models)

**Key Features:**
- Framework-independent
- Pure Kotlin code
- Business logic rules
- Repository contract definitions

**Example:**
```kotlin
class LoginUseCase(private val repository: LoginRepository) {
    suspend fun login(user: String, password: String): Result<LoginModel> {
        // Business logic
        val response = repository.login(user, password)
        return response
    }
}

interface LoginRepository {
    suspend fun login(user: String, password: String): Result<LoginModel>
}
```

---

### 3. **Data Layer**

**Responsibility:** Handle data sources and persistence  
**Components:**
- **Repository Implementations:** `LoginRepositoryImpl`, `TeamRepositoryImpl`
- **API Clients:** `LoginApi` (Retrofit interface)
- **Network Factory:** `RetrofitFactory`
- **Data Models:** `LoginModel`, `TeamModel`, Response classes
- **Mappers:** Convert API responses to domain models

**Key Features:**
- API integration (Retrofit)
- Response mapping
- Error handling
- Data source abstraction

**Example:**
```kotlin
class LoginRepositoryImpl(private val loginApi: LoginApi) : LoginRepository {
    override suspend fun login(user: String, password: String): Result<LoginModel> {
        return try {
            val response = loginApi.getUsers()
            val loginModel = LoginMapper.mapToLoginModel(response)
            Result.success(loginModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

interface LoginApi {
    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}
```

---

## Dependency Flow

```
Activity (Presentation)
    ↓
ViewModel (Presentation)
    ↓
UseCase (Domain)
    ↓
Repository Interface (Domain)
    ↓
Repository Implementation (Data)
    ↓
API Client / Local Storage (Data)
```

**Dependency Injection Pattern:**
- Uses constructor injection
- ViewModelFactory for ViewModel creation
- Singleton pattern for API instances

---

## Data Flow (Example: Login)

```
┌──────────────────────┐
│  LoginActivity       │
│  (User Input)        │
└──────────┬───────────┘
           │ user.login()
           ↓
┌──────────────────────────────┐
│  LoginViewModel              │
│  (State Management)          │
│  _uiState: StateFlow         │
└──────────┬───────────────────┘
           │ loginUseCase.login()
           ↓
┌──────────────────────────────┐
│  LoginUseCase                │
│  (Business Logic)            │
└──────────┬───────────────────┘
           │ repository.login()
           ↓
┌──────────────────────────────┐
│  LoginRepositoryImpl          │
│  (Data Coordination)         │
└──────────┬───────────────────┘
           │ loginApi.getUsers()
           ↓
┌──────────────────────────────┐
│  LoginApi (Retrofit)         │
│  (Network Call)              │
│  GET /users                  │
└──────────┬───────────────────┘
           │ HTTP Request
           ↓
    [Network]
           ↓
        Response
           ↓
┌──────────────────────────────┐
│  LoginMapper                 │
│  (Response → Domain Model)   │
└──────────┬───────────────────┘
           │ LoginModel
           ↓
┌──────────────────────────────┐
│  Result<LoginModel>          │
│  Success / Failure           │
└──────────┬───────────────────┘
           │ _uiState update
           ↓
┌──────────────────────────────┐
│  LoginActivity               │
│  (Observe State Change)      │
│  Navigate / Show Error       │
└──────────────────────────────┘
```

---

## State Management

### UI State Pattern (StateFlow + MutableStateFlow)

```kotlin
sealed class LoginUIState {
    object Idle : LoginUIState()
    object Loading : LoginUIState()
    data class Success(val userNamer: String) : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    
    fun login(user: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading
            
            val result = withContext(Dispatchers.IO) {
                loginUseCase.login(user, password)
            }
            
            result.onSuccess { loginModel ->
                _uiState.value = LoginUIState.Success(loginModel.user.name)
            }.onFailure { exception ->
                _uiState.value = LoginUIState.Error(exception.message ?: "Unknown error")
            }
        }
    }
}
```

---

## Error Handling Strategy

### Layered Error Handling

```
API Layer: Network errors, HTTP exceptions
    ↓
Repository Layer: Map to domain exceptions
    ↓
UseCase Layer: Apply business rules
    ↓
ViewModel Layer: Convert to UI state
    ↓
Activity Layer: Display to user
```

### Example Error Flow

```kotlin
try {
    val response = loginApi.getUsers()  // Can throw IOException, HttpException
} catch (e: Exception) {
    Result.failure(e)  // Data layer
}
// Repository catches and wraps: Result.failure(e)
// ViewModel observes and sets: _uiState.value = Error(e.message)
// Activity shows: Toast.makeText(context, message, LENGTH_SHORT)
```

---

## Coroutine Usage

### Scopes

- **viewModelScope:** Lifecycle-aware, lives with ViewModel
- **lifecycleScope:** Activity/Fragment lifecycle aware
- **Dispatchers.IO:** Network operations
- **Dispatchers.Main:** UI updates (implicit in StateFlow)

### Example

```kotlin
fun login(user: String, password: String) {
    viewModelScope.launch {  // Lifecycle-aware
        _uiState.value = LoginUIState.Loading
        
        val result = withContext(Dispatchers.IO) {  // Background thread
            loginUseCase.login(user, password)
        }
        
        // Back to Main thread automatically
        _uiState.value = // Update UI state
    }
}
```

---

## Feature Modules Structure

### Login Feature
```
com.login/
├── data/
│   ├── model/
│   │   └── LoginModel.kt
│   └── network/
│       ├── LoginApi.kt
│       └── RetrofitFactory.kt
├── domain/
│   ├── repository/
│   │   ├── LoginRepository.kt
│   │   ├── LoginRepositoryImpl.kt
│   │   └── response/
│   │       ├── UserResponse.kt
│   │       └── LoginMapper.kt
│   └── usecase/
│       └── LoginUseCase.kt
└── presentation/
    ├── view/
    │   └── LoginActivity.kt
    └── viewmodel/
        ├── LoginViewModel.kt
        ├── LoginViewModelFactory.kt
        └── state/
            └── LoginUIState.kt
```

### Teams Feature
```
com.teams/
├── data/
│   └── model/
│       └── TeamModel.kt
├── domain/
│   ├── repository/
│   │   ├── TeamRepository.kt
│   │   ├── TeamRepositoryImpl.kt
│   │   └── response/
│   │       ├── TeamResponse.kt
│   │       └── TeamMapper.kt
│   └── usecase/
│       └── TeamUseCase.kt
└── presentation/
    ├── view/
    │   └── TeamsActivity.kt
    ├── adapter/
    │   └── TeamsAdapter.kt
    └── viewmodel/
        ├── TeamViewModel.kt
        └── state/
            └── TeamUIState.kt
```

---

## Design Patterns Used

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Clean Architecture** | Entire app | Separation of concerns |
| **MVVM** | Presentation + Domain | UI state management |
| **Repository** | Data layer | Data source abstraction |
| **Singleton** | RetrofitFactory | Single API instance |
| **Factory** | ViewModelFactory | ViewModel creation |
| **Mapper** | Data layer | Response to domain conversion |
| **Sealed Classes** | UI States | Type-safe state management |

---

## Benefits of This Architecture

✅ **Testability:** Each layer can be tested independently  
✅ **Maintainability:** Clear separation of concerns  
✅ **Scalability:** Easy to add new features  
✅ **Flexibility:** Swap implementations (e.g., mock API for testing)  
✅ **Reusability:** Layers are independent of frameworks  
✅ **Documentation:** Self-documenting through structure  

---

**Last Updated:** July 6, 2026

