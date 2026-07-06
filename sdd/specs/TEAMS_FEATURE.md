# Teams Feature Specification

## Feature Overview

**Feature Name:** Teams Display & List Management  
**Status:** ✅ Complete  
**Version:** 1.0  
**Priority:** HIGH (Core functionality)  

---

## Feature Description

The Teams feature displays a list of football teams after successful user login. It shows team information in a scrollable list using RecyclerView, with each team item displaying the team name and group information.

---

## Business Requirements

### BR1: Teams Display
- After successful login, user navigates to TeamsActivity
- Display comprehensive list of teams
- Show team details (name, group)
- Scrollable interface for large lists

### BR2: User Experience
- Smooth navigation from LoginActivity
- Display personalized greeting with user name
- Clear visual hierarchy of team information
- Loading states during data fetch

### BR3: Data Source
- Teams data comes from API or local mock data
- Consistent data format across the app
- Proper error handling for missing data

### BR4: Navigation
- Back button returns to login screen
- Maintains navigation stack
- Clean state management on return

---

## Functional Requirements

### FR1: UI Layout

```
┌───────────────────────────────┐
│  ← Back  | Teams  | ⋮ Menu   │  ← Action Bar
├───────────────────────────────┤
│  Hello, Leanne Graham!        │  ← User Greeting
├───────────────────────────────┤
│  Teams:                        │  ← Section Header
├───────────────────────────────┤
│  ┌──────────────────────────┐  │
│  │ ⚽ Team Name             │  │ ← Team Card Item 1
│  │    Group: Group A        │  │
│  └──────────────────────────┘  │
├───────────────────────────────┤
│  ┌──────────────────────────┐  │
│  │ ⚽ Team Name 2           │  │ ← Team Card Item 2
│  │    Group: Group B        │  │
│  └──────────────────────────┘  │
├───────────────────────────────┤
│  ┌──────────────────────────┐  │
│  │ ⚽ Team Name 3           │  │ ← Team Card Item 3
│  │    Group: Group A        │  │
│  └──────────────────────────┘  │
│                                 │
│  [Scrollable area...]           │
│                                 │
└───────────────────────────────┘
```

### FR2: Activity Components

| Component | Type | Purpose |
|-----------|------|---------|
| AppBar | Toolbar | Display title, menu, back button |
| User Greeting | TextView | Show logged-in user name |
| Teams List | RecyclerView | Display teams in scrollable list |
| Team Item | CardView | Individual team display |
| Team Name | TextView | Name of the team |
| Team Group | TextView | Group assignment |
| Loading | ProgressBar | Show during data load |
| Empty State | TextView | Show when no teams available |

### FR3: Data Display

#### Team Card Item Layout
```xml
<CardView>
  <LinearLayout>
    <ImageView src="@drawable/ic_soccer_ball" />
    <LinearLayout orientation="vertical">
      <TextView text="Team Name" 
                 textSize="16sp" 
                 textStyle="bold" />
      <TextView text="Group: Group A" 
                 textSize="12sp" 
                 textColor="#666" />
    </LinearLayout>
  </LinearLayout>
</CardView>
```

#### Data Model

```kotlin
data class TeamModel(
    val id: Int = 0,
    val name: String,
    val group: String
)

data class TeamResponse(
    val name: String?,
    val group: String?
)
```

### FR4: RecyclerView Adapter

```kotlin
class TeamsAdapter(
    private val items: List<TeamModel>
) : RecyclerView.Adapter<TeamsAdapter.TeamViewHolder>() {
    
    override fun onCreateViewHolder(
        parent: ViewGroup, 
        viewType: Int
    ): TeamViewHolder {
        // Create view holder
    }
    
    override fun onBindViewHolder(
        holder: TeamViewHolder, 
        position: Int
    ) {
        // Bind team data to view
    }
    
    override fun getItemCount(): Int = items.size
    
    inner class TeamViewHolder(itemView: View) : 
        RecyclerView.ViewHolder(itemView) {
        fun bind(team: TeamModel) {
            // Populate UI with team data
        }
    }
}
```

### FR5: State Management

**UI States:**

```kotlin
sealed class TeamUIState {
    // Initial state
    object Idle : TeamUIState()
    
    // Loading teams
    object Loading : TeamUIState()
    
    // Teams loaded successfully
    data class Success(val teams: List<TeamModel>) : TeamUIState()
    
    // Error loading teams
    data class Error(val message: String) : TeamUIState()
}
```

**State Transitions:**

| Current | Event | Next | Action |
|---------|-------|------|--------|
| Idle | Activity created | Loading | Fetch teams |
| Loading | API responds | Success | Display teams, hide spinner |
| Loading | API error | Error | Show error message |
| Error | Retry clicked | Loading | Re-fetch teams |
| Success | User navigates back | Idle | Clear state |

### FR6: Passing Data Between Activities

```kotlin
// LoginActivity → TeamsActivity
val intent = Intent(this, TeamsActivity::class.java).apply {
    putExtra("user_name", "Leanne Graham")
}
startActivity(intent)

// TeamsActivity retrieves
val userName = intent.getStringExtra("user_name") ?: "User"
```

### FR7: ViewModel Integration

```kotlin
class TeamViewModel(
    private val useCase: TeamUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TeamUIState>(
        TeamUIState.Idle
    )
    val uiState: StateFlow<TeamUIState> = _uiState.asStateFlow()
    
    fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = TeamUIState.Loading
            
            val result = withContext(Dispatchers.IO) {
                useCase.getTeams()
            }
            
            _uiState.value = result.fold(
                onSuccess = { teams -> TeamUIState.Success(teams) },
                onFailure = { error -> 
                    TeamUIState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}
```

---

## Non-Functional Requirements

### NFR1: Performance
- **Teams List Load Time:** < 2 seconds
- **Item Rendering:** < 60ms per item
- **Scroll Smoothness:** 60 FPS (jank-free)
- **Memory Usage:** < 50MB for list of 100 teams

### NFR2: Usability
- **Item Touch Target:** ≥ 48dp height (Material Design)
- **Typography:** Clear hierarchy (16sp names, 12sp details)
- **Spacing:** Consistent padding (16dp, 8dp)
- **Colors:** High contrast for readability

### NFR3: Accessibility
- **Content Descriptions:** All images have descriptive text
- **Text Size:** Scalable (SP units, not DP)
- **Focus Management:** Clear focus order
- **Color Contrast:** WCAG AA minimum

### NFR4: Reliability
- **Empty State:** Handle no teams gracefully
- **Error Handling:** User-friendly error messages
- **Graceful Degradation:** Show cached data if API fails
- **Lifecycle Safety:** Prevent memory leaks

### NFR5: Compatibility
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 14)
- **Screen Sizes:** Support phones (320dp-600dp+)
- **Orientations:** Portrait and landscape

---

## Technical Specifications

### Dependencies

```gradle
implementation(libs.androidx.recyclerview)
implementation(libs.androidx.cardview)
implementation(libs.material)
implementation(libs.androidx.lifecycle.runtime.ktx)
testImplementation(libs.junit)
testImplementation(libs.mockk)
```

### Key Classes

```
com.teams.presentation.view.TeamsActivity
├── Uses: TeamViewModel
├── Layout: activity_teams.xml
├── Components: RecyclerView, Toolbar, TextView
└── Lifecycle: onCreate(), onBackPressed()

com.teams.presentation.adapter.TeamsAdapter
├── Extends: RecyclerView.Adapter
├── ViewHolder: TeamViewHolder
└── Items: List<TeamModel>

com.teams.presentation.viewmodel.TeamViewModel
├── Uses: TeamUseCase
├── State: MutableStateFlow<TeamUIState>
└── Method: loadTeams()

com.teams.domain.usecase.TeamUseCase
├── Uses: TeamRepository
└── Method: getTeams() → Result<List<TeamModel>>

com.teams.domain.repository.TeamRepository
├── Interface: Repository pattern
└── Method: getTeams() → Result<List<TeamModel>>

com.teams.data.model.TeamModel
├── Properties: id, name, group
└── Role: Domain entity for team
```

### Coroutine Scopes

```kotlin
// TeamsActivity
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state ->
            when (state) {
                is TeamUIState.Loading -> showLoading()
                is TeamUIState.Success -> displayTeams(state.teams)
                is TeamUIState.Error -> showError(state.message)
                else -> {}
            }
        }
    }
}

// TeamViewModel
viewModelScope.launch {
    _uiState.value = TeamUIState.Loading
    val result = withContext(Dispatchers.IO) {
        useCase.getTeams()
    }
    _uiState.value = // Update based on result
}
```

---

## Mock Data (For Testing)

```kotlin
object MockTeams {
    fun getMockTeams() = listOf(
        TeamModel(1, "Brazil", "Group A"),
        TeamModel(2, "France", "Group A"),
        TeamModel(3, "Germany", "Group B"),
        TeamModel(4, "Spain", "Group B"),
        TeamModel(5, "Argentina", "Group C"),
        TeamModel(6, "England", "Group C"),
        TeamModel(7, "Italy", "Group D"),
        TeamModel(8, "Netherlands", "Group D")
    )
}
```

---

## Testing Requirements

### Unit Tests

- ✅ TeamViewModel.loadTeams() updates state correctly
- ✅ TeamViewModel handles success response
- ✅ TeamViewModel handles error response
- ✅ TeamsAdapter calculates item count correctly
- ✅ TeamsAdapter binds data to views correctly
- ✅ TeamUseCase calls repository
- ✅ TeamRepository returns correct data

### Integration Tests

- ✅ TeamsActivity displays initial loading state
- ✅ TeamsActivity observes ViewModel state changes
- ✅ TeamsActivity displays team list on success
- ✅ TeamsActivity shows error message on failure
- ✅ RecyclerView renders all team items
- ✅ Team items are clickable (optional for enhancement)
- ✅ Back button returns to LoginActivity

### Manual Testing Scenarios

**Scenario 1: Successful Load**
1. Login successfully
2. Navigate to TeamsActivity
3. ✅ User greeting displayed
4. ✅ Teams list loads
5. ✅ All team cards render correctly
6. ✅ Scroll works smoothly

**Scenario 2: Loading State**
1. Navigate to TeamsActivity
2. ✅ Loading spinner visible initially
3. ✅ After data loads, spinner hidden
4. ✅ Teams appear smoothly

**Scenario 3: Empty State**
1. If teams API returns empty list
2. ✅ Show "No teams found" message
3. ✅ Show retry button

**Scenario 4: Error State**
1. Disconnect network
2. Try to load teams
3. ✅ Error message displayed
4. ✅ Show retry button
5. ✅ Re-enable network and retry succeeds

**Scenario 5: Rotation & Configuration Changes**
1. Load teams successfully
2. Rotate device
3. ✅ State preserved (no data reload)
4. ✅ Scroll position maintained (optional)

---

## Success Criteria

- ✅ Teams display correctly in RecyclerView
- ✅ All UI states handled appropriately
- ✅ Navigation from LoginActivity works
- ✅ Back navigation returns to LoginActivity
- ✅ Smooth scrolling performance
- ✅ Error states handled gracefully
- ✅ All tests pass
- ✅ Accessible to users with disabilities

---

## Future Enhancements

1. **Team Details:** Click team to see more info
2. **Favorite Teams:** Mark teams as favorites
3. **Search & Filter:** Filter teams by group or name
4. **Sorting:** Sort by name, group, or custom order
5. **Team Stats:** Display player count, rankings
6. **Push Notifications:** Notify of team updates
7. **Offline Mode:** Cache teams locally
8. **Team Comparison:** Compare multiple teams
9. **Live Updates:** Real-time score updates
10. **Share:** Share team information

---

**Last Updated:** July 6, 2026  
**Created By:** Development Team

