# Screen: Archive

## Overview

Publicly browsable list of flyers whose event date has passed. Supports
full-text search via the `q` query parameter. Layout and card style mirror
the public feed.

**Route:** `MainDestination.ArchiveDestination`

**Auth required:** No (public)

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Tap flyer card | `FlyerDetailDestination` | `flyerId: String` |
| "Sign In" tap (unauthenticated) | Auth graph | — |
| "Sign Out" tap | Sign-out → stays on screen | — |

---

## Files

| File | Path | Status |
|---|---|---|
| `ArchiveScreen.kt` | `features/main/archive/` | Needs search bar + shared components |
| `ArchiveViewModel.kt` | `features/main/archive/` | Needs `onQueryChanged()` |
| `ArchiveUIState.kt` | `features/main/archive/` | Needs `query` field |
| `ArchiveEvent.kt` | `features/main/archive/` | Complete |
| `ArchivePreview.kt` | `features/main/archive/` | Needs update |

---

## UI Layer

### Shared components used

| Component | From | Replaces |
|---|---|---|
| `FlyerBoardSearchBar` | `ui-components` (Phase 1.7) | Not yet implemented |
| `FlyerCard` | `ui-components` (Phase 1.2) | Private `ArchivedFlyerCard` |
| `LoadingStateBox` | `ui-components` (Phase 1.5) | Inline `CircularProgressIndicator` |
| `EmptyStateBox` | `ui-components` (Phase 1.6) | Inline `Text` |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("Archive")
      navigationIcon: IconButton(ArrowBack) → viewModel.navigateBack()
      actions:
        TextButton("Sign In" / "Sign Out")
        IconButton(Refresh icon) → viewModel.refresh()
  content:
    Column(fillMaxSize)
      FlyerBoardSearchBar(
        query = uiState.query,
        onQueryChange = viewModel::onQueryChanged,
        placeholder = "Search archive…",
        modifier = Modifier.fillMaxWidth().padding(Padding.MEDIUM),
      )
      Box(Modifier.weight(1f), contentAlignment = Center)
        when isLoading  → LoadingStateBox()
        when list empty → EmptyStateBox("No archived flyers found.")
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

### Search behaviour

- The search bar is always visible (not behind a toggle).
- Typing into the search bar immediately calls `viewModel.onQueryChanged(q)`.
- `onQueryChanged` debounces the request in the ViewModel (300 ms) to avoid
  firing a network call on every keystroke.
- An empty query loads the full unfiltered archive.
- The **Refresh** button re-fetches using the current query value.

---

## UIState

### Current

```kotlin
data class ArchiveUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val errorMessage: String?,
) : ViewModelUIState
```

### Target

```kotlin
data class ArchiveUIState(
    val isLoading: Boolean,
    val flyers: List<FlyerModel>,
    val query: String,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = ArchiveUIState(
            isLoading = false,
            flyers = emptyList(),
            query = "",
            errorMessage = null,
        )
    }
}
```

---

## Event

```kotlin
sealed class ArchiveEvent : ViewModelEvent {
    data object Noop : ArchiveEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `ArchiveViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

### Methods to add

#### `onQueryChanged(q: String)`
```kotlin
private var searchJob: Job? = null

fun onQueryChanged(q: String) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(query = q) }
    }
    searchJob?.cancel()
    searchJob = viewModelCoroutineScope.launch {
        delay(SEARCH_DEBOUNCE_MS)
        loadFlyers(query = q.takeIf { it.isNotBlank() })
    }
}

companion object {
    private const val SEARCH_DEBOUNCE_MS = 300L
}
```

### Methods to change

#### `loadFlyers()` — add optional `query` parameter
```kotlin
fun loadFlyers(query: String? = null) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(isLoading = true, errorMessage = null) }
        flyerManager
            .listArchived(query = query)
            .onSuccess { paginated ->
                updateUiState { it.copy(isLoading = false, flyers = paginated.flyers) }
            }.onFailure { error ->
                updateUiState { it.copy(isLoading = false, errorMessage = error.message) }
                emitWindowEvent(ShowSnackbar("Failed to load archive: ${error.message}"))
            }
    }
}
```

#### `refresh()` — pass current query
```kotlin
fun refresh() {
    loadFlyers(query = uiState.value.query.takeIf { it.isNotBlank() })
}
```

---

## Manager Layer

**Class:** `FlyerManager`
**Method:** `listArchived` — add `query` parameter

### Current signature

```kotlin
suspend fun listArchived(
    offset: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
): Result<PaginatedFlyerModel>
```

### Target signature

```kotlin
suspend fun listArchived(
    offset: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    query: String? = null,
): Result<PaginatedFlyerModel> =
    dependencies.getOrCatch(TAG) {
        flyerService.listArchived(offset, limit, query).getOrThrow()
    }
```

---

## Service Layer

**Interface:** `FlyerService`
**Method:** `listArchived` — add `query` parameter

### Current signature

```kotlin
suspend fun listArchived(
    offset: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
): Result<PaginatedFlyerModel>
```

### Target signature

```kotlin
suspend fun listArchived(
    offset: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    query: String? = null,
): Result<PaginatedFlyerModel>
```

### `FlyerServiceImpl` change

The archive endpoint supports the same `ListFlyersQueryParams` as the public
listing. Update `listArchived` to pass the query:

```kotlin
override suspend fun listArchived(
    offset: Int,
    limit: Int,
    query: String?,
): Result<PaginatedFlyerModel> =
    runSuspendCatching(TAG) {
        FlyerApi.listArchived
            .buildRequest(
                ListFlyersQueryParams(offset = offset, limit = limit, q = query),
            ).execute(http)
            .toPaginatedFlyerModel()
    }
```

API endpoint:
```
GET /api/v1/flyers/archive?offset=0&limit=20&q=<query>
No auth required.
```

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Network error | `isLoading = false`, snackbar "Failed to load archive: …" |
| No results for query | `EmptyStateBox("No archived flyers found.")` |
| No results (no query) | `EmptyStateBox("No archived flyers yet.")` |
| Success | Flyer list |
