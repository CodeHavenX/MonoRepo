# Screen: Sign Up

## Overview

Registers a new Supabase Auth account. On success the user lands directly in
the main app. The email/password pair is validated client-side before
submission. After the auth account is created the backend `POST /user`
endpoint is called to register a display name.

**Route:** `AuthDestination.SignUpDestination`

**Auth required:** No

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Sign Up success | Main graph (`MainNavGraphDestination`, clear stack) | — |
| "Back to Sign In" tap | `SignInDestination` (pop back) | — |

---

## Files

| File | Path | Status |
|---|---|---|
| `SignUpScreen.kt` | `features/auth/sign_up/` | Needs name fields added |
| `SignUpViewModel.kt` | `features/auth/sign_up/` | Needs `createUser` call + name fields |
| `SignUpUIState.kt` | `features/auth/sign_up/` | Needs `firstName`, `lastName` fields |
| `SignUpEvent.kt` | `features/auth/sign_up/` | Complete |
| `SignUpPreview.kt` | `features/auth/sign_up/` | Needs update for new fields |

---

## UI Layer

### Shared components used

| Component | From |
|---|---|
| `ScreenLayout` | `ui-catalog` (already used) |
| `LoadingAnimationOverlay` | `ui-catalog` (already used) |

### Layout

```
Scaffold
  Box(fillMaxSize)
    ScreenLayout(contentAlignment = Center)
      sectionContent:
        OutlinedTextField  ← first name (KeyboardType.Text, ImeAction.Next)
        OutlinedTextField  ← last name  (KeyboardType.Text, ImeAction.Next)
        OutlinedTextField  ← email      (KeyboardType.Email, ImeAction.Next)
        OutlinedTextField  ← password   (PasswordVisualTransformation, ImeAction.Done)
      buttonContent:
        Button             ← "Sign Up" → viewModel.signUp()
        TextButton         ← "Back to Sign In" → viewModel.navigateToSignIn()
      overlay:
        LoadingAnimationOverlay(uiState.isLoading)
```

### Field ordering rationale

First name and last name come first so the form reads naturally top-to-bottom
(identity → account credentials). All fields are required; the **Sign Up**
button should be disabled if any field is blank to provide early feedback.

### Keyboard & input behaviour

- First name: `ImeAction.Next` → focus moves to last name.
- Last name: `ImeAction.Next` → focus moves to email.
- Email: `KeyboardType.Email`, `ImeAction.Next` → focus moves to password.
- Password: `PasswordVisualTransformation`, `ImeAction.Done` → triggers sign-up.

---

## UIState

### Current

```kotlin
data class SignUpUIState(
    val email: String,
    val password: String,
    val isLoading: Boolean,
) : ViewModelUIState
```

### Target

```kotlin
data class SignUpUIState(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = SignUpUIState(
            firstName = "",
            lastName = "",
            email = "",
            password = "",
            isLoading = false,
        )
    }
}
```

---

## Event

```kotlin
sealed class SignUpEvent : ViewModelEvent {
    data object Noop : SignUpEvent()
}
```

No changes needed. Errors are surfaced via `FlyerBoardWindowsEvent.ShowSnackbar`.

---

## ViewModel

**Class:** `SignUpViewModel`
**Dependencies:** `ViewModelDependencies`, `AuthManager`, `UserManager` *(add)*

### Methods to add

#### `onFirstNameChanged(firstName: String)`
Updates `uiState.firstName`.

#### `onLastNameChanged(lastName: String)`
Updates `uiState.lastName`.

### Methods to change

#### `signUp()` — update

Current flow:
1. Sets `isLoading = true`.
2. Calls `authManager.signUp(email, password)`.
3. On failure → snackbar. On success → navigate to Main.

**New flow:**
1. Sets `isLoading = true`.
2. Client-side validation: all four fields must be non-blank. If any are
   empty, show snackbar "Please fill in all fields." and return.
3. Calls `authManager.signUp(email, password)`.
4. On **failure**: sets `isLoading = false`, emits `ShowSnackbar("Sign up failed: …")`.
5. On **success**:
   - Calls `userManager.createUser(firstName, lastName)`.
   - If `createUser` fails: log the error (non-fatal — the user is already
     authenticated) and continue.
   - Sets `isLoading = false`.
   - Emits `NavigateToNavGraph(MainNavGraphDestination, clearStack = true)`.

#### `navigateToSignIn()` — unchanged
Emits `NavigateBack`.

### DI change

Add `UserManager` as a constructor parameter and register the updated
`SignUpViewModel` in the DI module:

```kotlin
// ViewModelModule.kt
viewModelOf(::SignUpViewModel)
```

Koin resolves `UserManager` automatically if it is already declared in
`ManagerModule`.

---

## Manager Layer

### `AuthManager` — no changes

```kotlin
suspend fun signUp(email: String, password: String): Result<Unit>
```

### `UserManager` — no changes

```kotlin
suspend fun createUser(firstName: String, lastName: String): Result<UserModel>
```

Both managers are already complete and only need to be wired together in the
ViewModel.

---

## Service Layer

### `AuthService.signUp` — no changes

Calls Supabase Auth's email/password sign-up. On success, Supabase creates an
`auth.users` row and starts a session. The `activeUser()` flow emits the new
`UserId` automatically.

### `UserService.createUser` — no changes

```
POST /user
Body: { "first_name": "…", "last_name": "…" }
Authorization: Bearer <supabase-jwt>
```

The backend creates a `users` row linking the Supabase Auth UUID to a display
name. This endpoint must be called with the token that is active immediately
after sign-up (the Supabase client retains the session, so `getAccessToken()`
returns a valid token at this point).

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Blank field(s) | Snackbar: "Please fill in all fields." — do not call auth |
| Auth account already exists | Snackbar: "Sign up failed: User already registered" |
| Network error during sign-up | Snackbar: "Sign up failed: …" |
| `createUser` fails | Log warning; continue to Main graph silently |
| Success | Navigate to Main graph, clear Auth graph from stack |
