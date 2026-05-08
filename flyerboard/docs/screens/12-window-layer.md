# Cross-Cutting: Window Layer

## Overview

The window layer owns the top-level navigation host, the bottom navigation
bar, the global snackbar, and the session state that all screens read. It
consists of:

- `FlyerBoardWindowScreen.kt` — the root Composable
- `FlyerBoardWindowViewModel.kt` — session and event orchestration
- `FlyerBoardWindowUIState.kt` — `isAuthenticated`, `isAdmin`
- `FlyerBoardWindowsEvent.kt` — sealed event type (already complete)
- `FlyerBoardWindowNavGraphDestination.kt` — graph-level destinations
- `FlyerBoardWindowDelegatedEvent.kt` — snackbar result forwarding

This document covers all changes needed to support:
1. Admin role detection and gating the Moderation tab
2. Session restoration on app launch
3. Sign-out behaviour

---

## Files to Change

| File | Change |
|---|---|
| `FlyerBoardWindowUIState.kt` | Add `isAdmin: Boolean` |
| `FlyerBoardWindowViewModel.kt` | Inject `FlyerManager`; add admin check |
| `FlyerBoardWindowScreen.kt` | Pass `isAdmin` to nav bar; gate Moderation tab |
| `ViewModelModule.kt` | Update `FlyerBoardWindowViewModel` registration |

---

## UIState

### Current

```kotlin
data class FlyerBoardWindowUIState(val isAuthenticated: Boolean) : ViewModelUIState {
    companion object {
        val Initial = FlyerBoardWindowUIState(isAuthenticated = false)
    }
}
```

### Target

```kotlin
data class FlyerBoardWindowUIState(
    val isAuthenticated: Boolean,
    val isAdmin: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerBoardWindowUIState(isAuthenticated = false, isAdmin = false)
    }
}
```

---

## ViewModel

**Class:** `FlyerBoardWindowViewModel`

### Current dependencies

```kotlin
class FlyerBoardWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEvents: EventReceiver<FlyerBoardWindowDelegatedEvent>,
    private val authManager: AuthManager,
)
```

### Target dependencies

```kotlin
class FlyerBoardWindowViewModel(
    dependencies: ViewModelDependencies,
    private val windowEventEmitter: EventEmitter<WindowEvent>,
    private val delegatedEvents: EventReceiver<FlyerBoardWindowDelegatedEvent>,
    private val authManager: AuthManager,
    private val flyerManager: FlyerManager,   // ← add
)
```

### Init block changes

Replace the current `activeUser()` observer with one that also triggers an
admin check:

```kotlin
init {
    viewModelCoroutineScope.launch {
        windowEventEmitter.events.collect { event ->
            emitEvent(
                FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper(
                    event as FlyerBoardWindowsEvent,
                )
            )
        }
    }

    viewModelCoroutineScope.launch {
        authManager.activeUser().collect { userId ->
            logI(TAG, "Auth state changed: ${if (userId != null) "authenticated" else "unauthenticated"}")
            if (userId != null) {
                updateUiState { it.copy(isAuthenticated = true) }
                checkAdminStatus()
            } else {
                updateUiState { it.copy(isAuthenticated = false, isAdmin = false) }
            }
        }
    }
}
```

### Admin check

```kotlin
private fun checkAdminStatus() {
    viewModelCoroutineScope.launch {
        val isAdmin = flyerManager
            .listPendingFlyers(limit = 1)
            .map { true }
            .getOrDefault(false)
        logI(TAG, "Admin status: $isAdmin")
        updateUiState { it.copy(isAdmin = isAdmin) }
    }
}
```

**Strategy:** Call `listPendingFlyers` with `limit = 1`. A 200 response means
the user has admin access; a 403 means they do not. This is a lightweight
probe — the single-item response is not used for display.

**Alternative:** If the Supabase JWT contains a custom `role` claim (set
server-side when creating the user profile), it can be decoded from the token
without a network round-trip:

```kotlin
private fun checkAdminStatus() {
    val token = authManager.getAccessToken() ?: return
    val isAdmin = decodeRoleFromJwt(token) == "admin"
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(isAdmin = isAdmin) }
    }
}

private fun decodeRoleFromJwt(token: String): String? {
    // JWT is three Base64url segments: header.payload.signature
    val payloadBase64 = token.split(".").getOrNull(1) ?: return null
    val payloadJson = payloadBase64
        .replace('-', '+')
        .replace('_', '/')
        .let { base64Decode(it) }          // platform expect/actual or common impl
    return Json.parseToJsonElement(payloadJson)
        .jsonObject["role"]
        ?.jsonPrimitive?.content
}
```

The JWT approach avoids a network call but couples the client to a specific
JWT claim format. Use whichever approach matches the Supabase project's
configuration.

### `signOut()` — no changes needed

```kotlin
fun signOut() {
    viewModelCoroutineScope.launch {
        authManager.signOut()
        emitEvent(
            FlyerBoardWindowViewModelEvent.FlyerBoardWindowEventWrapper(
                FlyerBoardWindowsEvent.NavigateToNavGraph(
                    destination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                    clearStack = true,
                )
            )
        )
    }
}
```

The `activeUser()` collector sets `isAdmin = false` as soon as sign-out clears
the session — no extra cleanup is needed.

### DI registration

**`ViewModelModule.kt`** — update the `FlyerBoardWindowViewModel` factory to
include `FlyerManager`. Koin resolves it automatically from `ManagerModule`.

---

## Screen: Bottom Navigation Bar

### Current `FlyerBoardBottomNavBar` signature

```kotlin
@Composable
private fun FlyerBoardBottomNavBar(
    isAuthenticated: Boolean,
    currentDestination: NavDestination?,
    onBrowse: () -> Unit,
    onArchive: () -> Unit,
    onMyFlyers: () -> Unit,
    onModeration: () -> Unit,
)
```

### Target signature

```kotlin
@Composable
private fun FlyerBoardBottomNavBar(
    isAuthenticated: Boolean,
    isAdmin: Boolean,              // ← add
    currentDestination: NavDestination?,
    onBrowse: () -> Unit,
    onArchive: () -> Unit,
    onMyFlyers: () -> Unit,
    onModeration: () -> Unit,
)
```

### Moderation tab gating

```kotlin
// Before (gates on isAuthenticated):
if (isAuthenticated) {
    NavigationBarItem( /* Moderation */ )
}

// After (gates on isAdmin):
if (isAdmin) {
    NavigationBarItem( /* Moderation */ )
}
```

Pass `isAdmin = uiState.isAdmin` from `WindowsContent` to `FlyerBoardBottomNavBar`.

### Moderation bottom-nav tap while unauthenticated

The Moderation tab is only rendered when `isAdmin == true`, which implies the
user is authenticated. No extra guard is needed in the `onModeration` lambda.

---

## Session Restoration on Launch

The Supabase Kotlin client persists the session (JWT + refresh token) in the
platform's secure storage. On the next app launch, the client restores the
session automatically. `authManager.activeUser()` emits the restored `UserId`
without requiring a sign-in, so `FlyerBoardWindowViewModel` receives the
event in its `init` block and calls `checkAdminStatus()`.

The splash screen's 1-second delay gives the session restore time to complete
before the main graph is shown. If this races on slow devices, the
`isAuthenticated` value updates reactively and the bottom bar re-renders when
the session is confirmed.

---

## Navigation Event Reference

All navigation events emitted by ViewModels flow through
`FlyerBoardWindowsEvent` and are handled in `handleWindowEvent` in
`FlyerBoardWindowScreen.kt`. No changes to the event types are required.

| Event | Effect |
|---|---|
| `NavigateToNavGraph(dest, clearStack = true)` | Clears the entire back stack and navigates to the target graph |
| `NavigateToNavGraph(dest, clearStack = false)` | Pushes the graph onto the stack |
| `NavigateToScreen(dest)` | Pushes a screen destination onto the current graph |
| `NavigateBack` | Pops the top entry from the back stack |
| `CloseNavGraph` | Pops the entire current nav graph (used after auth) |
| `ShowSnackbar(message)` | Displays a short-duration snackbar |
| `ShareContent(text, uri)` | Delegates to platform share sheet |

---

## Snackbar Result Forwarding

`FlyerBoardWindowViewModel.handleSnackbarResult(result)` pushes the result
into `FlyerBoardWindowDelegatedEvent.HandleSnackbarResult`. Any ViewModel
that needs to react to a snackbar action button can observe
`delegatedEvents`. This mechanism is not used by any current screen but is
available for future use (e.g., "Undo" on a delete action).

---

## Summary of Changes

| File | What changes |
|---|---|
| `FlyerBoardWindowUIState.kt` | Add `isAdmin: Boolean` |
| `FlyerBoardWindowViewModel.kt` | Inject `FlyerManager`; add `checkAdminStatus()`; react to auth changes |
| `FlyerBoardWindowScreen.kt` | Pass `isAdmin` to `FlyerBoardBottomNavBar`; gate Moderation tab on `isAdmin` |
| `ViewModelModule.kt` | Add `FlyerManager` to `FlyerBoardWindowViewModel` factory |
