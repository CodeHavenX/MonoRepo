# Screen: Sign In

## Overview

Lets an existing user authenticate with email and password. On success the
auth graph is replaced by the main graph (full stack clear). Failed attempts
surface a snackbar error without clearing the form.

**Route:** `AuthDestination.SignInDestination`
(start destination of `AuthNavGraphDestination`)

**Auth required:** No

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Sign In success | Main graph (`MainNavGraphDestination`, clear stack) | — |
| "Create Account" tap | `SignUpDestination` | — |

---

## Files

| File | Path | Status |
|---|---|---|
| `SignInScreen.kt` | `features/auth/sign_in/` | Complete |
| `SignInViewModel.kt` | `features/auth/sign_in/` | Complete |
| `SignInUIState.kt` | `features/auth/sign_in/` | Complete |
| `SignInEvent.kt` | `features/auth/sign_in/` | Complete |
| `SignInPreview.kt` | `features/auth/sign_in/` | Complete |

---

## UI Layer

### Shared components used

| Component | From |
|---|---|
| `ScreenLayout` | `ui-catalog` (already used) |
| `LoadingAnimationOverlay` | `ui-catalog` (already used) |

No new ui-components components are needed for this screen.

### Layout

```
Scaffold
  Box(fillMaxSize)
    ScreenLayout(contentAlignment = Center)
      sectionContent:
        OutlinedTextField  ← email (KeyboardType.Email, ImeAction.Next)
        OutlinedTextField  ← password (PasswordVisualTransformation, ImeAction.Done)
      buttonContent:
        Button             ← "Sign In" → viewModel.signIn()
        TextButton         ← "Create Account" → viewModel.navigateToSignUp()
      overlay:
        LoadingAnimationOverlay(uiState.isLoading)
```

`ScreenLayout` is a ui-catalog composable that stacks the section and button
slots in a column, with the overlay rendered on top.

### Keyboard & input behaviour

- Email field uses `KeyboardType.Email` and `ImeAction.Next` to move focus to
  the password field automatically.
- Password field uses `PasswordVisualTransformation` and `ImeAction.Done` to
  trigger sign-in when the user presses the keyboard's Done/Go button.

### Disabled state

All inputs and buttons should be disabled (or the overlay should block
interaction) while `uiState.isLoading == true`. `LoadingAnimationOverlay`
handles the blocking visual; ensure the overlay intercepts pointer events.

---

## UIState

```kotlin
data class SignInUIState(
    val email: String,
    val password: String,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = SignInUIState(email = "", password = "", isLoading = false)
    }
}
```

No changes needed.

---

## Event

```kotlin
sealed class SignInEvent : ViewModelEvent {
    data object Noop : SignInEvent()
}
```

No changes needed. Errors are surfaced via `FlyerBoardWindowsEvent.ShowSnackbar`.

---

## ViewModel

**Class:** `SignInViewModel`
**Dependencies:** `ViewModelDependencies`, `AuthManager`

### Methods

#### `onEmailChanged(email: String)`
Updates `uiState.email`.

#### `onPasswordChanged(password: String)`
Updates `uiState.password`.

#### `signIn()`
1. Sets `isLoading = true`.
2. Calls `authManager.signIn(email, password)`.
3. On **failure**: sets `isLoading = false`, emits `ShowSnackbar("Sign in failed: …")`.
4. On **success**: sets `isLoading = false`, emits
   `NavigateToNavGraph(MainNavGraphDestination, clearStack = true)`.

#### `navigateToSignUp()`
Emits `NavigateToScreen(AuthDestination.SignUpDestination)`.

### No changes needed.

---

## Manager Layer

**Class:** `AuthManager`
**Method used:** `signIn(email: String, password: String): Result<Unit>`

```kotlin
suspend fun signIn(email: String, password: String): Result<Unit> =
    dependencies.getOrCatch(TAG) {
        authService.signIn(email, password).getOrThrow()
    }
```

Delegates directly to `AuthService`. No business logic beyond error wrapping.

### No changes needed.

---

## Service Layer

**Interface:** `AuthService`
**Implementation:** `AuthServiceImpl` (Supabase)
**Method:** `signIn(email: String, password: String): Result<Unit>`

Calls Supabase Auth's email/password sign-in. On success, Supabase stores a
session (JWT + refresh token) internally. Subsequent calls to
`getAccessToken()` return the JWT from the active session.

The session is persisted by the Supabase Kotlin client across app restarts.
`activeUser()` emits a non-null `UserId` as soon as the session is restored,
which `FlyerBoardWindowViewModel` observes to set `isAuthenticated = true`.

### No changes needed.

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Wrong credentials | Snackbar: "Sign in failed: Invalid login credentials" |
| Network error | Snackbar: "Sign in failed: …" (exception message) |
| Success | Navigate to Main graph, clear Auth graph from stack |

The form is not cleared on failure — this matches the spec ("Failed attempts
surface a snackbar error without clearing the form").
