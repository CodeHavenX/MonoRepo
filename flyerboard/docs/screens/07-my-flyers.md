# Screen: My Flyers

## Overview

Shows all flyers submitted by the authenticated user across all statuses. Each
card carries a colour-coded status badge. Non-archived flyers have an **Edit**
action. The top-bar contains a **Submit** button for creating a new flyer.

**Route:** `MainDestination.MyFlyersDestination`

**Auth required:** Yes — if the user is not authenticated, tapping the bottom
nav tab redirects to the Auth graph instead (handled in `FlyerBoardWindowScreen`).

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Tap flyer card | `FlyerDetailDestination` | `flyerId: String` |
| "Edit" button on card | `FlyerEditDestination` | `flyerId: String` |
| "Submit" header button | `FlyerSubmitDestination` | — |
| "Sign Out" tap | Sign-out → stays on screen | — |

---

## Files

| File | Path | Status |
|---|---|---|
| `MyFlyersScreen.kt` | `features/main/my_flyers/` | Needs Submit button + shared components |
| `MyFlyersViewModel.kt` | `features/main/my_flyers/` | Needs `onSubmitFlyer()` |
| `MyFlyersUIState.kt` | `features/main/my_flyers/` | Complete |
| `MyFlyersEvent.kt` | `features/main/my_flyers/` | Complete |
| `MyFlyersPreview.kt` | `features/main/my_flyers/` | Needs update |

---

## UI Layer

### Shared components used

| Component | From | Replaces |
|---|---|---|
| `FlyerCardWithStatus` | `shared-ui` (Phase 1.3) | Private `MyFlyerCard` |
| `LoadingStateBox` | `shared-ui` (Phase 1.5) | Inline `CircularProgressIndicator` |
| `EmptyStateBox` | `shared-ui` (Phase 1.6) | Inline `Text` |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("My Flyers")
      navigationIcon: IconButton(ArrowBack) → viewModel.navigateBack()
      actions:
        TextButton("Submit") → viewModel.onSubmitFlyer()
        TextButton("Sign Out") → onSignOut  (only when isAuthenticated)
        IconButton(Refresh icon) → viewModel.refresh()
  content:
    Box(fillMaxSize, contentAlignment = Center)
      when isLoading  → LoadingStateBox()
      when list empty → EmptyStateBox("You haven't submitted any flyers yet.")
      else            →
        LazyColumn(contentPadding = Padding.MEDIUM, verticalArrangement = spacedBy(Padding.SMALL))
          items(flyers, key = { it.id.flyerId }) { flyer →
            FlyerCardWithStatus(
              title = flyer.title,
              description = flyer.description,
              status = flyer.status,
              expiresAt = flyer.expiresAt,
              onClick = { viewModel.onFlyerSelected(flyer.id) },
              onEdit = if (flyer.status != FlyerStatus.ARCHIVED)
                           { { viewModel.onEditFlyer(flyer.id) } }
                       else null,
            )
          }
```

### StatusBadge colours (handled inside `FlyerCardWithStatus` → `StatusBadge`)

| Status | Badge colour |
|---|---|
| APPROVED | Lime (`#84CC16`) |
| PENDING | Coral (`#F43F5E`) |
| REJECTED | Red (`#DC2626`) |
| ARCHIVED | Grey (`#9CA3AF`) |

### Submit button visibility

The **Submit** button is always visible in this screen (the user is guaranteed
to be authenticated when they reach it). It is listed as an `actions` item in
the top bar, to the left of Sign Out and Refresh.

---

## UIState

```kotlin
data class MyFlyersUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = MyFlyersUIState(
            isLoading = false,
            flyers = emptyList(),
            errorMessage = null,
        )
    }
}
```

No changes needed.

---

## Event

```kotlin
sealed class MyFlyersEvent : ViewModelEvent {
    data object Noop : MyFlyersEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `MyFlyersViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

### Existing methods (no changes)

#### `loadFlyers()`
Sets `isLoading = true`, calls `flyerManager.listMyFlyers()`, populates
`flyers` on success, shows snackbar on failure.

#### `refresh()`
Delegates to `loadFlyers()`.

#### `onFlyerSelected(flyerId: FlyerId)`
Emits `NavigateToScreen(FlyerDetailDestination(flyerId.flyerId))`.

#### `onEditFlyer(flyerId: FlyerId)`
Emits `NavigateToScreen(FlyerEditDestination(flyerId.flyerId))`.

#### `navigateBack()`
Emits `NavigateBack`.

### Method to add

#### `onSubmitFlyer()`
```kotlin
fun onSubmitFlyer() {
    viewModelCoroutineScope.launch {
        emitWindowEvent(
            FlyerBoardWindowsEvent.NavigateToScreen(MainDestination.FlyerSubmitDestination)
        )
    }
}
```

---

## Manager Layer

**Class:** `FlyerManager`
**Method:** `listMyFlyers(offset, limit): Result<PaginatedFlyerModel>`

```kotlin
suspend fun listMyFlyers(
    offset: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
): Result<PaginatedFlyerModel> =
    dependencies.getOrCatch(TAG) {
        flyerService.listMyFlyers(offset, limit).getOrThrow()
    }
```

No changes needed.

---

## Service Layer

**Interface:** `FlyerService`
**Method:** `listMyFlyers(offset, limit): Result<PaginatedFlyerModel>`

```
GET /api/v1/flyers/mine
Authorization: Bearer <supabase-jwt>
Query params: offset=0&limit=20
```

Returns flyers for the authenticated user across all statuses. The service
attaches the auth header automatically via `authHeader()`.

No changes needed.

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| 401 Unauthorized | Snackbar "Failed to load your flyers: …" — screen shows empty state |
| Network error | Snackbar with error message |
| Empty result | `EmptyStateBox("You haven't submitted any flyers yet.")` |
| Success | Flyer list with status badges |
