# Git Workflow & Version Control

## Branch Strategy

### Main Branches

**`main` (Production)**
- Stable, production-ready code
- Protected branch (requires PR review)
- Tag releases with version numbers
- Only accepts merges from `release` or `hotfix` branches

**`develop` (Development)**
- Integration branch for features
- Base for feature branches
- Tested but may have new features
- Starting point for release branches

### Supporting Branches

**Feature Branches:** `feature/*`
```bash
git checkout -b feature/login-authentication

# Feature complete
git checkout develop
git pull origin develop
git merge --no-ff feature/login-authentication
git branch -d feature/login-authentication
git push origin develop
```

**Bugfix Branches:** `bugfix/*`
```bash
git checkout -b bugfix/login-null-pointer
# Fix the bug
git checkout develop
git merge --no-ff bugfix/login-null-pointer
git push origin develop
```

**Release Branches:** `release/*`
```bash
git checkout -b release/1.0 develop
# Version bump, final testing
git checkout main
git merge --no-ff release/1.0
git tag -a v1.0 -m "Release version 1.0"
git checkout develop
git merge --no-ff release/1.0
git push origin main develop --tags
```

**Hotfix Branches:** `hotfix/*`
```bash
git checkout -b hotfix/critical-crash main
# Fix critical issue
git checkout main
git merge --no-ff hotfix/critical-crash
git tag -a v1.0.1 -m "Hotfix v1.0.1"
git checkout develop
git merge --no-ff hotfix/critical-crash
git push origin main develop --tags
```

---

## Commit Messages

### Format

```
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

### Type

- **feat** - A new feature
- **fix** - A bug fix
- **refactor** - Code refactoring without feature changes
- **perf** - Performance improvement
- **test** - Adding or updating tests
- **docs** - Documentation changes
- **style** - Code style changes (formatting, semicolons, etc.)
- **chore** - Build config, dependencies
- **ci** - CI/CD configuration

### Scope

The area of the codebase affected:
- `login` - Login feature
- `teams` - Teams feature
- `network` - Network/API layer
- `ui` - User interface
- `arch` - Architecture changes

### Subject

- Use imperative mood ("add" not "added")
- Don't capitalize first letter
- No period (.) at the end
- Maximum 50 characters

### Body (Optional)

- Explain **what** and **why**, not how
- Wrap at 72 characters
- Separate from subject with blank line
- Use bullet points for multiple changes

### Footer (Optional)

- Reference issue numbers: `Fixes #123`
- Breaking changes: `BREAKING CHANGE: description`

---

## Commit Examples

### Feature Commit

```
feat(login): implement authentication flow

- Create LoginViewModel with StateFlow for UI state management
- Add LoginActivity with input validation
- Implement LoginUseCase for business logic
- Setup Retrofit API client with OkHttp interceptor
- Add proper error handling and user feedback

This implements the complete login feature as per the specification.
Users can now authenticate and navigate to the Teams screen.
```

### Bug Fix Commit

```
fix(login): handle null pointer exception in mapper

- Add null safety checks in LoginMapper.mapToLoginModel()
- Return empty list instead of throwing exception
- Add test case for null response handling

Fixes #42 - LoginActivity crashes when API returns unexpected response
```

### Test Commit

```
test(login): add comprehensive unit tests

- Add LoginViewModelTest with state transition tests
- Add LoginRepositoryImplTest with mock API
- Add LoginMapperTest for response transformation
- Achieve 85% code coverage for login feature

All tests pass with 100% success rate.
```

### Docs Commit

```
docs(architecture): update SDD with detailed diagrams

- Add layer architecture diagram
- Add login flow sequence diagram
- Add package structure documentation
- Add ViewModel state machine diagram

This helps new team members understand the codebase structure.
```

### Refactor Commit

```
refactor(network): extract Retrofit factory to singleton

- Move HTTP client creation to RetrofitFactory
- Remove duplicate Retrofit setup code
- Improve logging interceptor configuration
- Add BuildConfig.DEBUG check for log levels

No functional changes, improves code organization.
```

---

## Pull Request Process

### Before Creating PR

1. **Create feature branch** from `develop`
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/new-feature
   ```

2. **Make commits** with clear messages
   ```bash
   git add .
   git commit -m "feat(feature): add specific functionality"
   ```

3. **Keep branch updated**
   ```bash
   git fetch origin
   git rebase origin/develop
   ```

4. **Run tests locally**
   ```bash
   ./gradlew testDebugUnitTest
   ./gradlew connectedAndroidTest
   ```

5. **Run code quality checks**
   ```bash
   ./gradlew detekt
   ```

### Creating PR

**PR Title Format:**
```
[FEATURE] Short description
[BUGFIX] Short description
[REFACTOR] Short description
```

**PR Description Template:**
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Refactoring
- [ ] Documentation

## Related Issue
Fixes #123

## How Has This Been Tested?
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing complete

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex areas
- [ ] Documentation updated
- [ ] No new warnings generated
```

### PR Review Checklist

**Code Quality:**
- ✅ Follows Kotlin style guide
- ✅ No code duplication
- ✅ Proper naming conventions
- ✅ Clean Architecture patterns followed

**Testing:**
- ✅ Tests are comprehensive
- ✅ Edge cases covered
- ✅ Test coverage >= 70%
- ✅ Tests pass locally

**Documentation:**
- ✅ Code is documented
- ✅ Comments explain "why", not "what"
- ✅ README updated if needed
- ✅ Specs documented

**Performance:**
- ✅ No performance regression
- ✅ Memory leaks checked
- ✅ Network calls optimized

**Security:**
- ✅ No sensitive data exposed
- ✅ Input validation present
- ✅ Error messages don't leak info

---

## Typical Development Workflow

### Day 1: Start Feature

```bash
# 1. Update local repository
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/ssd_implementation

# 3. Create initial commit with branch setup
git add .
git commit -m "feat(ssd): initialize SSD documentation structure

- Create sdd/ directory with docs, skills, specs, tasks
- Add comprehensive README with architecture overview
- Document API endpoints and diagrams
- Outline development process and Git workflow"

git push -u origin feature/ssd_implementation
```

### During Development

```bash
# 1. Make changes and commit
git add app/src/main/java/com/login/presentation/viewmodel/LoginViewModel.kt
git commit -m "feat(login): implement StateFlow for UI state management

- Add LoginUIState sealed class with Loading, Success, Error states
- Implement LoginViewModel with _uiState MutableStateFlow
- Add observer pattern for Activity integration"

# 2. Add tests
git add app/src/test/java/com/login/presentation/viewmodel/LoginViewModelTest.kt
git commit -m "test(login): add comprehensive ViewModel tests

- Test state transitions on successful login
- Test error handling and state updates
- Mock LoginUseCase dependency"

# 3. Keep updated with develop
git fetch origin
git rebase origin/develop

# 4. Push changes
git push origin feature/ssd_implementation
```

### Feature Complete

```bash
# 1. Final update
git fetch origin
git rebase origin/develop

# 2. Run all tests
./gradlew testDebugUnitTest connectedAndroidTest

# 3. Final commit if needed
git add .
git commit -m "docs(ssd): finalize SDD documentation

- Complete all specification documents
- Add task breakdown and skill documentation
- Ready for team review"

# 4. Create Pull Request on GitHub
# Use PR template and reference related issues

# 5. After review approval, merge
git checkout develop
git pull origin develop
git merge --no-ff feature/ssd_implementation
git push origin develop

# 6. Clean up local branch
git branch -d feature/ssd_implementation
```

---

## Handling Conflicts

### Resolve Merge Conflict

```bash
# 1. Start merge
git merge feature/other-feature

# 2. Check conflicts
git status

# 3. Edit conflicted file
# Look for: <<<<<<<  =======  >>>>>>>

# 4. Resolve and mark as resolved
git add conflicted-file.kt

# 5. Complete merge
git commit -m "merge: resolve conflicts with feature/other-feature"
```

### Rebase Onto Develop

```bash
# 1. Get latest develop
git fetch origin
git rebase origin/develop

# 2. If conflicts occur, resolve them
# Edit conflicted files

# 3. Continue rebase
git add .
git rebase --continue

# 4. Force push (only for feature branches!)
git push -f origin feature/my-feature
```

---

## Viewing History

### View Commits

```bash
# View commit history
git log --oneline

# View with authors
git log --format="%h %an %ad %s" --date=short

# View branch history
git log feature/my-feature --oneline

# View differences
git diff main feature/my-feature
```

### View Branches

```bash
# Local branches
git branch

# Remote branches
git branch -r

# All branches with tracking
git branch -vv

# Delete merged branches
git branch --merged | grep -v main | xargs git branch -d
```

---

## Tag Management

### Create Tags

```bash
# Create lightweight tag
git tag v1.0

# Create annotated tag
git tag -a v1.0 -m "Version 1.0 Release"

# Push tags
git push origin v1.0
git push origin --tags

# List tags
git tag -l
```

---

## Common Commands Reference

```bash
# Clone repository
git clone <repository-url>

# Fetch and merge
git pull origin develop

# Stage changes
git add .
git add specific-file.kt

# Commit changes
git commit -m "message"

# Amend last commit
git commit --amend --no-edit

# Undo last commit (keep changes)
git reset --soft HEAD~1

# View changes
git diff
git diff --staged

# Stash changes
git stash
git stash pop

# Create branch
git checkout -b feature/name
git switch -c feature/name  # newer syntax

# Switch branch
git checkout develop
git switch develop  # newer syntax

# Delete branch
git branch -d feature/name
git branch -D feature/name  # force

# Push branch
git push -u origin feature/name

# Pull with rebase
git pull --rebase origin develop
```

---

## Best Practices

1. **Commit often, push regularly**
   - Small, atomic commits
   - Push to origin at end of day

2. **Write meaningful commit messages**
   - Future developers will thank you
   - Helps in code review

3. **Keep branches short-lived**
   - Max 2-3 days before merging
   - Reduces merge conflicts

4. **Review your own code first**
   - Read entire PR before asking review
   - Catch obvious issues early

5. **Run tests before pushing**
   - Save CI/CD pipeline time
   - Catch regressions early

6. **Communicate with team**
   - Share branch names
   - Discuss complex changes early

7. **Use meaningful branch names**
   - `feature/user-authentication`
   - `bugfix/login-crash`
   - `refactor/networking-layer`

8. **Protect main branches**
   - Require PR reviews
   - Require status checks to pass
   - Enforce commit signatures (optional)

---

**Last Updated:** July 6, 2026  
**Created By:** Development Team

