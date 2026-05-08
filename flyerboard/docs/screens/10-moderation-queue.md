# Screen: Moderation Queue

## Overview

Admins see all flyers in the **Pending** state. Each card exposes **Approve**
and **Reject** actions inline. Rejecting a flyer opens a dialog to enter an
optional reason; the associated file is deleted from storage by the backend.
Approving a flyer makes it publicly visible.

**Route:** `MainDestination.ModerationQueueDestination`

**Auth required:** Yes — admin role. Non-admin users are blocked by the
backend (403) and by the UI (the Moderation tab is only shown when
`isAdmin == true` in `FlyerBoardWindowUIState`).

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Approve / Reject | Refreshes list in place | — |
| "Sign Out" tap | Sign-out → stays on screen | — |

---

## Files

| File | Path | Status |
|---|---|---|
| `ModerationQueueScreen.kt` | `features/main/moderation_queue/` | Needs shared components + rejection dialog |
| `ModerationQueueViewModel.kt` | `features/main/moderation_queue/` | Needs rejection reason + dialog state |
| `ModerationQueueUIState.kt` | `features/main/moderation_queue/` | Needs dialog state fields |
| `ModerationQueueEvent.kt` | `features/main/moderation_queue/` | Complete |
| `ModerationQueuePreview.kt` | `features/main/moderation_queue/` | Needs update |

---

## UI Layer

### Shared components used

| Component | From | Replaces |
|---|---|---|
| `ModerationFlyerCard` | `shared-ui` (Phase 1.4) | Private `PendingFlyerCard` |
| `LoadingStateBox` | `shared-ui` (Phase 1.5) | Inline `CircularProgressIndicator` |
| `EmptyStateBox` | `shared-ui` (Phase 1.6) | Inline `Text` |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("Moderation Queue")
      navigationIcon: IconButton(ArrowBack) → viewModel.navigateBack()
      actions:
        TextButton("Sign Out") → onSignOut  (only when isAuthenticated)
        IconButton(Refresh icon) → viewModel.refresh()
  content:
    Box(fillMaxSize, contentAlignment = Center)
      when isLoading  → LoadingStateBox()
      when list empty → EmptyStateBox("No pending flyers.")
      else            →
        LazyColumn(contentPadding = Padding.MEDIUM, verticalArrangement = spacedBy(Padding.SMALL))
          items(pendingFlyers, key = { it.id.flyerId }) { flyer →
            ModerationFlyerCard(
              title = flyer.title,
              description = flyer.description,
              expiresAt = flyer.expiresAt,
              onApprove = { viewModel.approveFlyer(flyer.id) },
              onReject  = { viewModel.onRejectRequested(flyer.id) },
            )
          }

  ── Rejection dialog (shown when uiState.pendingRejectionFlyerId != null) ──
  if uiState.pendingRejectionFlyerId != null:
    AlertDialog(
      onDismissRequest = viewModel::onRejectDismissed,
      title   = { Text("Reject Flyer") },
      text    = {
        Column(spacedBy(Padding.SMALL))
          Text("Optionally provide a reason for the rejection:")
          OutlinedTextField(
            value = uiState.rejectionReason,
            onValueChange = viewModel::onRejectionReasonChanged,
            label = { Text("Reason (optional)") },
            minLines = 2,
          )
      },
      confirmButton = {
        Button(onClick = viewModel::confirmReject)
          Text("Reject")
      },
      dismissButton = {
        TextButton(onClick = viewModel::onRejectDismissed)
          Text("Cancel")
      },
    )
```

### Approve / Reject button styling (inside `ModerationFlyerCard`)

- **Reject**: `OutlinedButton` with `contentColor = MaterialTheme.colorScheme.error`
- **Approve**: filled `Button` (primary colour)

---

## UIState

### Current

```kotlin
data class ModerationQueueUIState(
    val isLoading: Boolean,
    val pendingFlyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState
```

### Target

```kotlin
data class ModerationQueueUIState(
    val isLoading: Boolean,
    val pendingFlyers: List<FlyerModel>,
    val pendingRejectionFlyerId: FlyerId?,  // non-null = rejection dialog visible
    val rejectionReason: String,            // bound to the dialog's TextField
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = ModerationQueueUIState(
            isLoading = false,
            pendingFlyers = emptyList(),
            pendingRejectionFlyerId = null,
            rejectionReason = "",
            errorMessage = null,
        )
    }
}
```

The dialog is driven entirely by `pendingRejectionFlyerId` — when it is
non-null the dialog is shown, when null it is hidden.

---

## Event

```kotlin
sealed class ModerationQueueEvent : ViewModelEvent {
    data object Noop : ModerationQueueEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `ModerationQueueViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

### Existing methods — keep, some changes

#### `loadPendingFlyers()` — no changes
Sets `isLoading = true`, calls `flyerManager.listPendingFlyers()`, updates list.

#### `refresh()` — no changes
Delegates to `loadPendingFlyers()`.

#### `approveFlyer(flyerId: FlyerId)` — no changes
Calls `flyerManager.moderate(flyerId, "approve")`, shows snackbar, reloads list.

#### `navigateBack()` — no changes
Emits `NavigateBack`.

### Methods to add

#### `onRejectRequested(flyerId: FlyerId)`
Opens the rejection dialog by storing the target flyer ID:
```kotlin
fun onRejectRequested(flyerId: FlyerId) {
    viewModelCoroutineScope.launch {
        updateUiState {
            it.copy(
                pendingRejectionFlyerId = flyerId,
                rejectionReason = "",
            )
        }
    }
}
```

#### `onRejectionReasonChanged(reason: String)`
```kotlin
fun onRejectionReasonChanged(reason: String) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(rejectionReason = reason) }
    }
}
```

#### `onRejectDismissed()`
Closes the dialog without taking action:
```kotlin
fun onRejectDismissed() {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(pendingRejectionFlyerId = null, rejectionReason = "") }
    }
}
```

#### `confirmReject()`
Reads the pending ID and reason from state, calls the manager, then closes the dialog:
```kotlin
fun confirmReject() {
    val flyerId = uiState.value.pendingRejectionFlyerId ?: return
    val reason = uiState.value.rejectionReason

    viewModelCoroutineScope.launch {
        updateUiState { it.copy(pendingRejectionFlyerId = null, rejectionReason = "") }
        flyerManager
            .moderate(flyerId, ACTION_REJECT, reason = reason.takeIf { it.isNotBlank() })
            .onSuccess {
                emitWindowEvent(FlyerBoardWindowsEvent.ShowSnackbar("Flyer rejected."))
                loadPendingFlyers()
            }.onFailure { error ->
                emitWindowEvent(
                    FlyerBoardWindowsEvent.ShowSnackbar("Failed to reject flyer: ${error.message}")
                )
            }
    }
}
```

### Method to remove

#### `rejectFlyer(flyerId: FlyerId)`
The current `rejectFlyer` that calls `moderate` directly is replaced by the
three-step dialog flow above (`onRejectRequested` → `onRejectionReasonChanged`
→ `confirmReject`). Remove the old method.

---

## Manager Layer

**Class:** `FlyerManager`

### Method to change: `moderate`

#### Current signature
```kotlin
suspend fun moderate(flyerId: FlyerId, action: String): Result<FlyerModel>
```

#### Target signature
```kotlin
suspend fun moderate(
    flyerId: FlyerId,
    action: String,
    reason: String? = null,
): Result<FlyerModel> =
    dependencies.getOrCatch(TAG) {
        flyerService.moderate(flyerId, action, reason).getOrThrow()
    }
```

---

## Service Layer

**Interface:** `FlyerService`

### Method to change: `moderate`

#### Current signature
```kotlin
suspend fun moderate(flyerId: FlyerId, action: String): Result<FlyerModel>
```

#### Target signature
```kotlin
suspend fun moderate(
    flyerId: FlyerId,
    action: String,
    reason: String? = null,
): Result<FlyerModel>
```

### `FlyerServiceImpl` change

Include `reason` in `ModerationActionNetworkRequest` when provided:

```kotlin
override suspend fun moderate(
    flyerId: FlyerId,
    action: String,
    reason: String?,
): Result<FlyerModel> =
    runSuspendCatching(TAG) {
        ModerationApi.moderate
            .buildRequest(
                flyerId,
                ModerationActionNetworkRequest(action = action, reason = reason),
            ).execute(http, authHeader())
            .toFlyerModel()
    }
```

API endpoint:
```
POST /api/v1/moderation/{id}
Authorization: Bearer <supabase-jwt>
Body (approve):
  { "action": "approve" }
Body (reject):
  { "action": "reject", "reason": "Does not meet community guidelines" }
```

**Note:** Verify that `ModerationActionNetworkRequest` in the shared models
already has a `reason: String?` field. If not, add it with `@SerialName("reason")`.

---

## Window Layer: Admin Gating

The Moderation tab must only appear for admin users. This requires changes to
the window layer (Phase 2.3 of `implementation-plan.md`).

### `FlyerBoardWindowUIState.kt`
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

### `FlyerBoardWindowViewModel.kt`
After the `activeUser()` flow emits a non-null `UserId`, attempt to determine
if the user is an admin. The simplest signal is to call `listPendingFlyers()`
speculatively:
- **200** → user is admin → `isAdmin = true`
- **403** → user is a regular user → `isAdmin = false`

```kotlin
viewModelCoroutineScope.launch {
    authManager.activeUser().collect { userId ->
        val authenticated = userId != null
        updateUiState { it.copy(isAuthenticated = authenticated) }
        if (authenticated) {
            checkAdminStatus()
        } else {
            updateUiState { it.copy(isAdmin = false) }
        }
    }
}

private suspend fun checkAdminStatus() {
    // A 200 response means the user is an admin; 403 means they are not.
    val isAdmin = flyerManager.listPendingFlyers(limit = 1)
        .map { true }
        .getOrDefault(false)
    updateUiState { it.copy(isAdmin = isAdmin) }
}
```

### `FlyerBoardWindowScreen.kt`
Pass `isAdmin = uiState.isAdmin` to `FlyerBoardBottomNavBar` and gate the
Moderation tab on `isAdmin` instead of `isAuthenticated`:

```kotlin
// In FlyerBoardBottomNavBar:
if (isAdmin) {
    NavigationBarItem(
        selected = ...,
        onClick = onModeration,
        icon = { Icon(Icons.Default.Gavel, contentDescription = null) },
        label = { Text(stringResource(Res.string.nav_moderation)) },
    )
}
```

**`FlyerBoardWindowViewModel` DI change:** inject `FlyerManager` in addition
to `AuthManager`.

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| 403 on list load | Snackbar "Failed to load moderation queue: Forbidden"; empty state shown |
| 403 on approve/reject | Snackbar with error message |
| Network error | Snackbar with error; list not refreshed |
| Approve success | Snackbar "Flyer approved."; list reloads |
| Reject success | Snackbar "Flyer rejected."; list reloads |
| Dialog dismissed | No action taken; dialog closes |

---

## Previews

**`ModerationQueuePreview.kt`** — add:

1. `ModerationQueueWithDialogPreview` *(new)* — one card visible, rejection
   dialog open with sample reason text.

Existing previews (loading, empty, with content) need updates to use the
new `ModerationQueueUIState` shape.
