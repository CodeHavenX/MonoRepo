# Screen: Flyer List (Public Feed)

## Overview

The home screen. Publicly accessible. Shows a paginated list of approved
flyers. The top-right header toggles between **Sign In** and **Sign Out**
depending on auth state, and shows a **Submit** button when the user is signed
in.

**Route:** `MainDestination.FlyerListDestination`
(start destination of `MainNavGraphDestination`)

**Auth required:** No (public)

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Tap flyer card | `FlyerDetailDestination` | `flyerId: String` |
| "Submit" tap (authenticated) | `FlyerSubmitDestination` | — |
| "Sign In" tap (unauthenticated) | Auth graph | — |
| "Sign Out" tap | Sign-out → stays on screen | — |

Sign-out is handled by the window ViewModel (`FlyerBoardWindowViewModel.signOut()`),
which is passed to this screen as `onSignOut`.

---

## Files

| File | Path | Status |
|---|---|---|
| `FlyerListScreen.kt` | `features/main/flyer_list/` | Needs Submit button + shared components |
| `FlyerListViewModel.kt` | `features/main/flyer_list/` | Needs `onSubmitFlyer()` |
| `FlyerListUIState.kt` | `features/main/flyer_list/` | Complete |
| `FlyerListEvent.kt` | `features/main/flyer_list/` | Complete |
| `FlyerListPreview.kt` | `features/main/flyer_list/` | Needs update |

---

## UI Layer

### Shared components used

| Component | From | Replaces |
|---|---|---|
| `FlyerCard` | `ui-components` (Phase 1.2) | Private `FlyerCard` composable |
| `LoadingStateBox` | `ui-components` (Phase 1.5) | Inline `CircularProgressIndicator` |
| `EmptyStateBox` | `ui-components` (Phase 1.6) | Inline `Text` |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("Browse")
      actions:
        TextButton("Submit")       ← visible only when isAuthenticated
        TextButton("Sign In" / "Sign Out")
        IconButton(Refresh icon)   → viewModel.refresh()
  content:
    Box(fillMaxSize, contentAlignment = Center)
      when isLoading  → LoadingStateBox()
      when list empty → EmptyStateBox("No flyers yet.")
      else            →
        LazyColumn(contentPadding = Padding.MEDIUM, verticalArrangement = spacedBy(Padding.SMALL))
          items(flyers) { flyer →
            FlyerCard(
              title = flyer.title,
              description = flyer.description,
              expiresAt = flyer.expiresAt,
              onClick = { viewModel.onFlyerSelected(flyer.id) },
            )
          }
```

### TopAppBar actions order (left → right)

1. **Submit** `TextButton` — shown only when `isAuthenticated == true`
2. **Sign In** or **Sign Out** `TextButton` — toggled by `isAuthenticated`
3. **Refresh** `IconButton`

---

## UIState

```kotlin
data class FlyerListUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerListUIState(isLoading = false, flyers = emptyList(), errorMessage = null)
    }
}
```

No changes needed. `isAuthenticated` is passed in as a parameter from the
window/nav layer — it is not part of this screen's own state.

---

## Event

```kotlin
sealed class FlyerListEvent : ViewModelEvent {
    data object Noop : FlyerListEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `FlyerListViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

### Existing methods (no changes)

#### `loadFlyers()`
Called on `ON_CREATE`. Fetches `flyerManager.listFlyers()` (approved, page 0).
Sets `isLoading = true` while in-flight, then updates `flyers` or shows a
snackbar on failure.

#### `refresh()`
Delegates to `loadFlyers()`.

#### `onFlyerSelected(flyerId: FlyerId)`
Emits `NavigateToScreen(FlyerDetailDestination(flyerId.flyerId))`.

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

### Screen wiring change

In `FlyerListScreen.kt`, add `onSubmitFlyer: () -> Unit` parameter and wire
it to the Submit `TextButton`. In `MainActivityScreen.kt`, pass
`onSubmitFlyer = { viewModel.onSubmitFlyer() }` (the ViewModel is obtained
via `koinViewModel()` inside `FlyerListScreen` so `MainActivityScreen` does
not need access to it — the ViewModel method can be called from inside the
screen directly, as the other callbacks are).

---

## Manager Layer

**Class:** `FlyerManager`
**Method:** `listFlyers(offset, limit, status, query): Result<PaginatedFlyerModel>`

```kotlin
suspend fun listFlyers(
    offset: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    status: FlyerStatus? = null,
    query: String? = null,
): Result<PaginatedFlyerModel>
```

Called with default arguments — returns approved flyers (the backend defaults
to `status = approved` when no status filter is provided). No changes needed.

---

## Service Layer

**Interface:** `FlyerService`
**Implementation:** `FlyerServiceImpl`
**Method:** `listFlyers(offset, limit, status, query): Result<PaginatedFlyerModel>`

```
GET /api/v1/flyers
Query params: offset=0&limit=20
```

No auth header required. Maps the response using `FlyerListNetworkResponse.toPaginatedFlyerModel()`.

No changes needed.

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Network error | `isLoading = false`, snackbar "Failed to load flyers: …" |
| Empty result | `EmptyStateBox("No flyers yet.")` |
| Success | Renders flyer list |

---

## Pagination (future work)

The current implementation fetches only the first page (`offset = 0, limit = 20`).
Adding infinite scroll requires:
1. Track `offset` and `hasMore` in `FlyerListUIState`.
2. Add `loadNextPage()` to the ViewModel.
3. Use `LazyListState` in the screen to detect when the user reaches the bottom.
