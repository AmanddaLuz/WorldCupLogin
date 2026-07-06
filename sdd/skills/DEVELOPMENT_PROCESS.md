# Development Process & Repeatable Skills

## Overview

This document outlines the development processes, patterns, and reusable skills used throughout the WorldCupLogin project development lifecycle.

---

## 1. Feature Development Workflow

### Phase 1: Planning & Analysis

**Steps:**
1. Define feature requirements
2. Create feature specification document
3. Identify data models needed
4. Plan API integration points
5. Design UI layout mockups

**Deliverables:**
- Feature Specification (`.md` file)
- API endpoint documentation
- Data model diagrams
- UI wireframes

**Example Commit:**
```
feat: initial login feature planning
- Add LOGIN_FEATURE.md specification
- Define LoginUIState sealed class
- Document API endpoints
- Plan Clean Architecture structure
```

### Phase 2: Backend Integration Setup

**Steps:**
1. Create data models (Response classes)
2. Create API interface (Retrofit)
3. Setup Retrofit factory
4. Configure HTTP client with interceptors
5. Create mappers for responses

**Deliverables:**
- `*Response.kt` classes
- `*Api.kt` Retrofit interface
- `RetrofitFactory.kt`
- `*Mapper.kt` classes

**Example Commits:**
```
feat: setup login API integration
- Add UserResponse data class
- Create LoginApi Retrofit interface
- Configure RetrofitFactory with OkHttp

feat: add response mapping
- Create LoginMapper for response transformation
- Handle null safety in mapping logic
```

### Phase 3: Domain Layer Implementation

**Steps:**
1. Define repository interface
2. Create use cases
3. Implement business logic
4. Add error handling

**Deliverables:**
- `*Repository.kt` interface
- `*RepositoryImpl.kt` implementation
- `*UseCase.kt` classes

**Example Commits:**
```
feat: implement login use case
- Create LoginRepository interface
- Add LoginRepositoryImpl with error handling
- Implement LoginUseCase with result pattern

feat: add exception handling
- Handle network errors gracefully
- Add Result<T> wrapper for type safety
```

### Phase 4: Presentation Layer (MVVM)

**Steps:**
1. Create UI State sealed class
2. Implement ViewModel with StateFlow
3. Build UI Activity/Fragment
4. Setup observers and data binding
5. Create ViewModelFactory

**Deliverables:**
- `*UIState.kt` sealed class
- `*ViewModel.kt` class
- `*Activity.kt` Activity
- `*ViewModelFactory.kt` factory

**Example Commits:**
```
feat: create login view model
- Add LoginUIState sealed class
- Implement LoginViewModel with StateFlow
- Setup coroutine scopes with Dispatchers

feat: build login activity ui
- Create activity_login.xml layout
- Implement LoginActivity observers
- Add input validation logic
- Setup button state management
```

### Phase 5: Testing

**Steps:**
1. Write unit tests for ViewModels
2. Write unit tests for UseCases
3. Write unit tests for Repositories
4. Add integration tests
5. Manual testing scenarios

**Deliverables:**
- `*ViewModelTest.kt`
- `*RepositoryTest.kt`
- `*UseCaseTest.kt`
- Test coverage report

**Example Commits:**
```
test: add login view model tests
- Test login state transitions
- Mock LoginUseCase
- Verify state updates on success/failure

test: add repository unit tests
- Mock LoginApi responses
- Test error handling
- Verify mapper is called
```

---

## 2. Repeatable Patterns

### Pattern 1: API Client Setup

**File Structure:**
```
data/
├── model/
│   └── *Model.kt
└── network/
    ├── *Api.kt
    ├── RetrofitFactory.kt
    └── response/
        ├── *Response.kt
        └── *Mapper.kt
```

**Implementation Steps:**

1. **Create Response Class:**
```kotlin
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String
)
```

2. **Create Retrofit Interface:**
```kotlin
interface LoginApi {
    @GET("users")
    suspend fun getUsers(): List<UserResponse>
}
```

3. **Setup Factory with Logging:**
```kotlin
object RetrofitFactory {
    private const val BASE_URL = "https://api.example.com/"
    
    fun create(): LoginApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }
}
```

4. **Create Mapper:**
```kotlin
object LoginMapper {
    fun mapToLoginModel(response: List<UserResponse>): LoginModel {
        return LoginModel(user = response.first())
    }
}
```

### Pattern 2: Clean Architecture Layer Implementation

**Repository Pattern:**

```kotlin
// Domain Layer - Interface
interface LoginRepository {
    suspend fun login(user: String, password: String): Result<LoginModel>
}

// Data Layer - Implementation
class LoginRepositoryImpl(private val api: LoginApi) : LoginRepository {
    override suspend fun login(user: String, password: String): Result<LoginModel> {
        return try {
            val response = api.getUsers()
            val model = LoginMapper.mapToLoginModel(response)
            Result.success(model)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Domain Layer - UseCase
class LoginUseCase(private val repository: LoginRepository) {
    suspend fun login(user: String, password: String): Result<LoginModel> {
        return repository.login(user, password)
    }
}
```

**ViewModel Pattern:**

```kotlin
sealed class LoginUIState {
    object Idle : LoginUIState()
    object Loading : LoginUIState()
    data class Success(val data: String) : LoginUIState()
    data class Error(val message: String) : LoginUIState()
}

class LoginViewModel(private val useCase: LoginUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUIState>(LoginUIState.Idle)
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()
    
    fun login(user: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUIState.Loading
            
            val result = withContext(Dispatchers.IO) {
                useCase.login(user, password)
            }
            
            result.onSuccess { model ->
                _uiState.value = LoginUIState.Success(model.user.name)
            }.onFailure { error ->
                _uiState.value = LoginUIState.Error(error.message ?: "Unknown error")
            }
        }
    }
}
```

**Activity Observer Pattern:**

```kotlin
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        observeViewModel()
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginUIState.Idle -> handleIdle()
                        is LoginUIState.Loading -> handleLoading()
                        is LoginUIState.Success -> handleSuccess(state.data)
                        is LoginUIState.Error -> handleError(state.message)
                    }
                }
            }
        }
    }
}
```

### Pattern 3: ViewModelFactory Pattern

```kotlin
class LoginViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val api = RetrofitFactory.create()
            val repository = LoginRepositoryImpl(api)
            val useCase = LoginUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            LoginViewModel(useCase) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// Usage in Activity
val viewModel: LoginViewModel by viewModels {
    LoginViewModelFactory()
}
```

### Pattern 4: RecyclerView Adapter with StateFlow

```kotlin
class TeamsAdapter(
    private val teams: List<TeamModel>
) : RecyclerView.Adapter<TeamsAdapter.TeamViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val binding = ItemTeamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TeamViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }
    
    override fun getItemCount() = teams.size
    
    inner class TeamViewHolder(private val binding: ItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(team: TeamModel) {
            binding.teamName.text = team.name
            binding.teamGroup.text = "Group: ${team.group}"
        }
    }
}
```

---

## 3. Testing Patterns

### Unit Test Pattern - ViewModel

```kotlin
class LoginViewModelTest {
    
    private lateinit var viewModel: LoginViewModel
    private val mockUseCase: LoginUseCase = mockk()
    
    @Before
    fun setup() {
        viewModel = LoginViewModel(mockUseCase)
    }
    
    @Test
    fun `login with valid credentials updates UI state to success`() = runTest {
        // Arrange
        val expectedModel = LoginModel(user = UserResponse(...))
        coEvery { mockUseCase.login(any(), any()) } returns Result.success(expectedModel)
        
        // Act
        viewModel.login("user", "pass")
        
        // Assert
        assertEquals(
            LoginUIState.Success(expectedModel.user.name),
            viewModel.uiState.value
        )
    }
    
    @Test
    fun `login with error updates UI state to error`() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { mockUseCase.login(any(), any()) } returns Result.failure(exception)
        
        // Act
        viewModel.login("user", "pass")
        
        // Assert
        assertTrue(viewModel.uiState.value is LoginUIState.Error)
    }
}
```

### Unit Test Pattern - Repository

```kotlin
class LoginRepositoryImplTest {
    
    private lateinit var repository: LoginRepositoryImpl
    private val mockApi: LoginApi = mockk()
    
    @Before
    fun setup() {
        repository = LoginRepositoryImpl(mockApi)
    }
    
    @Test
    fun `login calls API and returns success`() = runTest {
        // Arrange
        val response = listOf(UserResponse(...))
        coEvery { mockApi.getUsers() } returns response
        
        // Act
        val result = repository.login("user", "pass")
        
        // Assert
        assertTrue(result.isSuccess)
        coVerify { mockApi.getUsers() }
    }
    
    @Test
    fun `login handles API exception and returns failure`() = runTest {
        // Arrange
        coEvery { mockApi.getUsers() } throws IOException()
        
        // Act
        val result = repository.login("user", "pass")
        
        // Assert
        assertTrue(result.isFailure)
    }
}
```

---

## 4. Git Workflow & Commits

### Branch Strategy

**Branch Naming Convention:**
```
feature/feature-name          # New features
bugfix/bug-description        # Bug fixes
hotfix/urgent-fix             # Critical production fixes
refactor/refactoring-scope    # Code refactoring
test/test-scope               # Test additions
docs/documentation-topic      # Documentation
```

### Commit Message Pattern

**Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `test` - Test additions
- `refactor` - Code refactoring
- `docs` - Documentation
- `style` - Code style changes
- `perf` - Performance improvements

**Examples:**

```
feat(login): add user authentication flow
- Implement LoginViewModel with StateFlow
- Add LoginActivity with input validation
- Create LoginUseCase and LoginRepository

fix(login): handle null pointer in mapper
- Add null checks in LoginMapper
- Return empty list instead of throwing exception

test(login): add view model unit tests
- Test state transitions
- Test error handling
- Mock LoginUseCase

docs(architecture): update SDD documentation
- Add architecture diagrams
- Document data flow
- Add API endpoint reference
```

### Common Commit Sequences

**Feature Development:**
```
1. feat: initial feature planning
2. feat: setup API integration
3. feat: implement domain layer
4. feat: implement presentation layer
5. test: add unit tests
6. test: add integration tests
7. docs: add feature documentation
```

**Bug Fix:**
```
1. fix: describe the bug
2. test: add test case that reproduces bug
3. refactor: improve related code
4. docs: update documentation if needed
```

---

## 5. Code Quality Checkpoints

### Pre-Commit Checklist

- ✅ Code follows Kotlin style guide
- ✅ No compiler warnings
- ✅ Unit tests pass
- ✅ Integration tests pass
- ✅ Detekt passes (if applicable)
- ✅ No hardcoded strings (use strings.xml)
- ✅ Proper error handling
- ✅ No sensitive data in logs

### Code Review Checklist

- ✅ Follows Clean Architecture pattern
- ✅ SOLID principles applied
- ✅ Proper naming conventions
- ✅ Documentation complete
- ✅ Tests comprehensive
- ✅ Performance considered
- ✅ Accessibility addressed
- ✅ No code duplication

### Detekt Command

```bash
./gradlew detekt

# Check specific file
./gradlew detekt --include-build-dir
```

---

## 6. Common Tasks & Processes

### Add a New API Endpoint

1. Create response class in `data/network/response/`
2. Add method to `*Api.kt` interface
3. Create mapper if needed
4. Update repository interface
5. Update repository implementation
6. Add use case if new feature
7. Write tests
8. Update documentation

### Add a New ViewModel

1. Create `*UIState.kt` sealed class
2. Create `*ViewModel.kt` extending ViewModel
3. Create `*ViewModelFactory.kt`
4. Add state management logic
5. Write unit tests using Mockk
6. Document state transitions

### Add a New Feature Module

1. Create directory structure:
   ```
   com.feature/
   ├── data/
   ├── domain/
   └── presentation/
   ```
2. Implement data layer (API, models, mappers)
3. Implement domain layer (repository, use case)
4. Implement presentation layer (activity, viewmodel, ui state)
5. Add dependency injection (ViewModelFactory)
6. Write tests
7. Add to AndroidManifest.xml if activity
8. Create feature specification in SDD

### Debugging Network Issues

```bash
# 1. Check Retrofit logs (Debug build)
adb logcat | grep "okhttp"

# 2. Check response
adb logcat | grep "Response"

# 3. Enable verbose logging
// In RetrofitFactory
level = HttpLoggingInterceptor.Level.BODY

# 4. Test API in browser/postman
https://jsonplaceholder.typicode.com/users
```

---

## 7. Performance Optimization Skills

### Memory Management

- ✅ Use `viewModelScope` for coroutines (lifecycle-aware)
- ✅ Cancel long-running operations
- ✅ Avoid holding Activity references
- ✅ Use `WeakReference` for callbacks if needed

### Network Optimization

- ✅ Implement connection timeout (30s)
- ✅ Implement read timeout (30s)
- ✅ Cache responses where applicable
- ✅ Batch requests when possible

### UI Performance

- ✅ Use RecyclerView for lists (not ListView)
- ✅ Implement DiffUtil for efficient updates
- ✅ Use ViewHolder pattern
- ✅ Avoid blocking main thread
- ✅ Use Dispatchers.IO for network calls

---

## 8. Documentation Skills

### Creating Specification Documents

1. **Start with overview** - Brief feature description
2. **Add requirements** - Business and functional
3. **Document data models** - Show structure
4. **Add UI mockups** - Visual representation
5. **Include examples** - Code samples
6. **Document APIs** - Request/response
7. **Add test scenarios** - Manual testing
8. **Success criteria** - How to verify

### Documenting Code

```kotlin
/**
 * Authenticates user and fetches user information from API.
 * 
 * Updates UI state to reflect loading, success, or error states.
 * 
 * @param user Username or email
 * @param password User password
 * 
 * Example:
 * viewModel.login("user@example.com", "password123")
 * 
 * @throws IOException if network request fails
 */
fun login(user: String, password: String)
```

---

**Last Updated:** July 6, 2026  
**Created By:** Development Team

