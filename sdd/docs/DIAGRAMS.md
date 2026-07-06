# Architecture & Flow Diagrams

## 1. Layer Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                              │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ LoginActivity                                    TeamsActivity│  │
│  │  ┌─────────────────────────────────────────────────────┐     │  │
│  │  │ Observes: ViewModels, StateFlows                    │     │  │
│  │  │ Triggers: User interactions (click, input)          │     │  │
│  │  │ Renders: UI components, layouts                     │     │  │
│  │  └─────────────────────────────────────────────────────┘     │  │
│  │                                                               │  │
│  │  ┌──────────────────────┐        ┌──────────────────────┐   │  │
│  │  │ LoginViewModel       │        │ TeamViewModel        │   │  │
│  │  │ - _uiState: MutableSF        │ - _uiState: MutableSF   │  │
│  │  │ - login()            │        │ - fetchTeams()       │   │  │
│  │  └──────────────────────┘        └──────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────┬───────────────────────────────────────────────┘
                     │ (Dependency: UseCase)
┌────────────────────▼───────────────────────────────────────────────┐
│                       DOMAIN LAYER                                   │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ UseCases                                                      │  │
│  │ ┌────────────────┐              ┌────────────────┐           │  │
│  │ │ LoginUseCase   │              │ TeamUseCase    │           │  │
│  │ │ - login()      │              │ - getTeams()   │           │  │
│  │ └────────────────┘              └────────────────┘           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ Repository Interfaces                                         │  │
│  │ ┌────────────────────────┐    ┌────────────────────────┐    │  │
│  │ │ LoginRepository        │    │ TeamRepository         │    │  │
│  │ │ - login()              │    │ - getTeams()           │    │  │
│  │ └────────────────────────┘    └────────────────────────┘    │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────┬───────────────────────────────────────────────┘
                     │ (Dependency: Repository Implementation)
┌────────────────────▼───────────────────────────────────────────────┐
│                         DATA LAYER                                   │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ Repository Implementations                                   │  │
│  │ ┌────────────────────────┐    ┌────────────────────────┐    │  │
│  │ │ LoginRepositoryImpl     │    │ TeamRepositoryImpl      │    │  │
│  │ │ - login()              │    │ - getTeams()           │    │  │
│  │ └────────────────────────┘    └────────────────────────┘    │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ API Clients & Network                                        │  │
│  │ ┌────────────────────────────────────────────────────────┐  │  │
│  │ │ LoginApi (Retrofit Interface)                          │  │  │
│  │ │ @GET("users") - suspend fun getUsers()                 │  │  │
│  │ └────────────────────────────────────────────────────────┘  │  │
│  │ ┌────────────────────────────────────────────────────────┐  │  │
│  │ │ RetrofitFactory                                        │  │  │
│  │ │ - Base URL: https://jsonplaceholder.typicode.com/     │  │  │
│  │ │ - OkHttpClient, Logging Interceptor                   │  │  │
│  │ │ - GsonConverterFactory                                │  │  │
│  │ └────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ Mappers & Models                                             │  │
│  │ ┌────────────────┐           ┌────────────────┐             │  │
│  │ │ LoginMapper    │           │ TeamMapper     │             │  │
│  │ │ UserResponse → │           │ TeamResponse → │             │  │
│  │ │ LoginModel     │           │ TeamModel      │             │  │
│  │ └────────────────┘           └────────────────┘             │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
```

---

## 2. Login Flow Sequence Diagram

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐     ┌──────────┐     ┌──────────┐
│  Activity   │     │  ViewModel   │     │   UseCase   │     │Repository│     │    API   │
└─────────────┘     └──────────────┘     └─────────────┘     └──────────┘     └──────────┘
       │                   │                     │                   │                │
       │ login()           │                     │                   │                │
       │──────────────────→│                     │                   │                │
       │                   │                     │                   │                │
       │         _uiState.value = Loading        │                   │                │
       │←──────────────────│                     │                   │                │
       │                   │                     │                   │                │
       │                   │ login()             │                   │                │
       │                   │────────────────────→│                   │                │
       │                   │                     │                   │                │
       │                   │                     │ login()           │                │
       │                   │                     │──────────────────→│                │
       │                   │                     │                   │                │
       │                   │                     │                   │ getUsers()     │
       │                   │                     │                   │───────────────→│
       │                   │                     │                   │                │
       │                   │                     │                   │        HTTP GET
       │                   │                     │                   │      /users
       │                   │                     │                   │                │
       │                   │                     │                   │  [Network Call]
       │                   │                     │                   │                │
       │                   │                     │                   │ List<UserResponse>
       │                   │                     │                   │←───────────────│
       │                   │                     │                   │                │
       │                   │                     │ Result<LoginModel>│                │
       │                   │                     │←──────────────────│                │
       │                   │                     │                   │                │
       │ _uiState.value = Success(name)          │                   │                │
       │←──────────────────│←────────────────────│                   │                │
       │                   │                     │                   │                │
    Navigate to
    TeamsActivity
       │                   │                     │                   │                │
```

---

## 3. UI State Machine

```
                    ┌──────────┐
                    │  Idle    │
                    └────┬─────┘
                         │ login() called
                         │
                         ↓
                    ┌──────────┐
                    │ Loading  │
                    └────┬─────┘
                         │
            ┌────────────┴────────────┐
            │                         │
            ↓ (Success)               ↓ (Error)
        ┌──────────┐            ┌──────────┐
        │ Success  │            │  Error   │
        └────┬─────┘            └────┬─────┘
             │                       │
             │ navigate()            │ refresh UI
             │                       │
             ↓                       ↓
        [Navigate]              ┌──────────┐
                                │  Idle    │
                                └──────────┘
```

---

## 4. Package Structure Diagram

```
com/
│
├── example/
│   └── worldcuplogin/
│       ├── MainActivity.kt
│       └── ui/theme/
│           ├── Color.kt
│           ├── Type.kt
│           └── Theme.kt
│
├── login/ ─────────────────── LOGIN FEATURE
│   ├── data/
│   │   ├── model/
│   │   │   └── LoginModel.kt
│   │   └── network/
│   │       ├── LoginApi.kt (Retrofit Interface)
│   │       └── RetrofitFactory.kt (Singleton)
│   │
│   ├── domain/
│   │   ├── repository/
│   │   │   ├── LoginRepository.kt (Interface)
│   │   │   ├── LoginRepositoryImpl.kt
│   │   │   └── response/
│   │   │       ├── UserResponse.kt
│   │   │       └── LoginMapper.kt
│   │   └── usecase/
│   │       └── LoginUseCase.kt
│   │
│   └── presentation/
│       ├── view/
│       │   └── LoginActivity.kt
│       └── viewmodel/
│           ├── LoginViewModel.kt
│           ├── LoginViewModelFactory.kt
│           └── state/
│               └── LoginUIState.kt
│
├── teams/ ─────────────────── TEAMS FEATURE
│   ├── data/
│   │   └── model/
│   │       └── TeamModel.kt
│   │
│   ├── domain/
│   │   ├── repository/
│   │   │   ├── TeamRepository.kt
│   │   │   ├── TeamRepositoryImpl.kt
│   │   │   └── response/
│   │   │       ├── TeamResponse.kt
│   │   │       └── TeamMapper.kt
│   │   └── usecase/
│   │       └── TeamUseCase.kt
│   │
│   └── presentation/
│       ├── view/
│       │   └── TeamsActivity.kt
│       ├── adapter/
│       │   └── TeamsAdapter.kt
│       └── viewmodel/
│           ├── TeamViewModel.kt
│           └── state/
│               └── TeamUIState.kt
│
└── commons/ ───────────────── SHARED UTILITIES
    └── JsonReader.kt
```

---

## 5. Activity Navigation Flow

```
                    ┌────────────────────┐
                    │   App Start        │
                    │ (App Manifest)     │
                    └──────────┬─────────┘
                               │
                               ↓
                    ┌────────────────────┐
                    │  LoginActivity     │
                    │  (MAIN / LAUNCHER) │
                    └──────────┬─────────┘
                               │
                    ┌──────────┴──────────┐
                    │                     │
              [LOGIN SUCCESS]       [LOGIN ERROR]
                    │                     │
                    ↓                     ↓
          ┌─────────────────┐      ┌────────────────────┐
          │ TeamsActivity   │      │ Show Error Toast   │
          │ (List of Teams) │      │ Stay on LoginPage  │
          └─────────────────┘      └────────────────────┘
                    │
              [BACK BUTTON]
                    │
                    ↓
          ┌─────────────────┐
          │  LoginActivity  │ (Resume)
          └─────────────────┘
```

---

## 6. Data Transformation Pipeline

```
┌──────────────────────┐
│  API Response JSON   │
└──────────┬───────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ Retrofit + Gson                      │
│ (Deserialization)                    │
└──────────┬───────────────────────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ List<UserResponse>                   │
│ (Data Layer Model)                   │
└──────────┬───────────────────────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ LoginMapper                          │
│ (Transform to Domain Model)          │
└──────────┬───────────────────────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ LoginModel                           │
│ (Domain Entity)                      │
└──────────┬───────────────────────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ Result<LoginModel>                   │
│ (Success / Failure)                  │
└──────────┬───────────────────────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ LoginUIState                         │
│ (Presentation Layer State)           │
└──────────┬───────────────────────────┘
           │
           ↓
┌──────────────────────────────────────┐
│ UI Update                            │
│ (Activity Rendering)                 │
└──────────────────────────────────────┘
```

---

## 7. Coroutine Execution Flow

```
UI Thread (Main)
    │
    ├─→ Activity calls viewModel.login()
    │
    ├─→ viewModelScope.launch {  ◄─ Main Thread
    │   │
    │   ├─→ _uiState.value = Loading  ◄─ Main Thread
    │   │
    │   ├─→ withContext(Dispatchers.IO) {  ◄─ Switch to IO Thread
    │   │   │
    │   │   ├─→ loginUseCase.login()
    │   │   │   ├─→ repository.login()
    │   │   │   │   ├─→ loginApi.getUsers()  ◄─ Network call on IO
    │   │   │   │   └─→ LoginMapper.map()
    │   │   │   └─→ Result
    │   │   │
    │   │   └─→ return Result
    │   │
    │   ├─→ Back to Main Thread (implicit)  ◄─ Main Thread
    │   │
    │   ├─→ result.onSuccess { ... }  ◄─ Main Thread
    │   │   └─→ _uiState.value = Success
    │   │
    │   └─→ result.onFailure { ... }  ◄─ Main Thread
    │       └─→ _uiState.value = Error
    │
    └─→ Activity collects state changes
        └─→ Update UI Components
```

---

**Last Updated:** July 6, 2026

