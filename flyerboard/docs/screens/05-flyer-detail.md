# Screen: Flyer Detail

## Overview

Full-page view of a single flyer. Shows the flyer image (4:3 aspect ratio),
title, full description, and expiration date. If the flyer was rejected, the
rejection reason is shown. Reachable from the public feed, the archive, and
My Flyers.

**Route:** `MainDestination.FlyerDetailDestination(flyerId: String)`

**Auth required:** No (public)

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Back button / system back | Previous screen (pop) | — |

---

## Files

| File | Path | Status |
|---|---|---|
| `FlyerDetailScreen.kt` | `features/main/flyer_detail/` | Needs component swaps + rejection reason |
| `FlyerDetailViewModel.kt` | `features/main/flyer_detail/` | Complete |
| `FlyerDetailUIState.kt` | `features/main/flyer_detail/` | Complete |
| `FlyerDetailEvent.kt` | `features/main/flyer_detail/` | Complete |
| `FlyerDetailPreview.kt` | `features/main/flyer_detail/` | Needs update |

---

## Route Arguments

`FlyerDetailDestination` carries `flyerId: String` — the raw UUID string of
the target flyer. It is passed as `it.toRoute<FlyerDetailDestination>()` in
`MainActivityScreen` and forwarded to `viewModel.loadFlyer(destination.flyerId)`.

---

## UI Layer

### Shared components used

| Component | From | Replaces |
|---|---|---|
| `FlyerAsyncImage` | `ui-components` (Phase 1.8) | Inline `AsyncImage` in `FlyerDetailBody` |
| `LoadingStateBox` | `ui-components` (Phase 1.5) | Inline `CircularProgressIndicator` |
| `EmptyStateBox` | `ui-components` (Phase 1.6) | Inline "not found" `Text` |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("Flyer")
      navigationIcon: IconButton(ArrowBack) → viewModel.navigateBack()
  content:
    Box(fillMaxSize, contentAlignment = Center)
      when isLoading     → LoadingStateBox()
      when flyer == null → EmptyStateBox("Flyer not found.")
      else               → FlyerDetailBody(flyer)

FlyerDetailBody:
  Column(fillMaxSize, verticalScroll, padding = Padding.MEDIUM, spacedBy(Padding.MEDIUM))
    FlyerAsyncImage(url = flyer.fileUrl, contentDescription = flyer.title)
    Text(flyer.title, style = headlineSmall)
    Text(flyer.description, style = bodyLarge)
    if flyer.expiresAt != null:
      Text("Expires: ${flyer.expiresAt}", style = labelMedium, color = outline)
    if flyer.status == REJECTED && flyer.rejectionReason != null:
      Surface(color = errorContainer, shape = RoundedCornerShape(8.dp))
        Column(padding = Padding.MEDIUM)
          Text("Rejected", style = labelSmall, color = error)
          Text(flyer.rejectionReason, style = bodyMedium, color = onErrorContainer)
```

The rejection reason block uses `MaterialTheme.colorScheme.errorContainer` as
background and `onErrorContainer` for the reason text, keeping it visually
distinct without requiring a custom color.

---

## UIState

```kotlin
data class FlyerDetailUIState(
    val isLoading: Boolean,
    val flyer: FlyerModel?,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerDetailUIState(isLoading = false, flyer = null)
    }
}
```

No changes needed. `FlyerModel` gains `rejectionReason: String?` in Phase 2.2,
which the screen reads directly from the model.

---

## Event

```kotlin
sealed class FlyerDetailEvent : ViewModelEvent {
    data object Noop : FlyerDetailEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `FlyerDetailViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

### Methods (no changes needed)

#### `loadFlyer(flyerIdValue: String)`
1. Sets `isLoading = true`.
2. Calls `flyerManager.getFlyer(FlyerId(flyerIdValue))`.
3. On **success**:
   - If `flyer == null`: sets `isLoading = false`, emits snackbar "Flyer not found.".
   - If `flyer != null`: sets `isLoading = false, flyer = flyer`.
4. On **failure**: sets `isLoading = false`, emits snackbar with error message.

#### `navigateBack()`
Emits `FlyerBoardWindowsEvent.NavigateBack`.

---

## Manager Layer

**Class:** `FlyerManager`
**Method:** `getFlyer(flyerId: FlyerId): Result<FlyerModel?>`

```kotlin
suspend fun getFlyer(flyerId: FlyerId): Result<FlyerModel?> =
    dependencies.getOrCatch(TAG) {
        flyerService.getFlyer(flyerId).getOrThrow()
    }
```

Returns `null` when the backend responds with 404 (handled in `FlyerServiceImpl`
by catching `ClientRequestExceptions.NotFoundException`). No changes needed.

---

## Service Layer

**Interface:** `FlyerService`
**Method:** `getFlyer(flyerId: FlyerId): Result<FlyerModel?>`

```
GET /api/v1/flyers/{id}
No auth required.
```

**Response 200** — `FlyerObject` mapped via `FlyerNetworkResponse.toFlyerModel()`
**Response 404** — returns `null`

### Change required (Phase 2.2)

`FlyerNetworkResponse` has a `rejectionReason` field that is not yet mapped.

**`FlyerNetworkMapper.kt`:**
```kotlin
fun FlyerNetworkResponse.toFlyerModel(): FlyerModel = FlyerModel(
    id = id,
    title = title,
    description = description,
    fileUrl = fileUrl,
    status = status,
    expiresAt = expiresAt,
    rejectionReason = rejectionReason,   // ← add
    uploaderId = uploaderId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
```

**`FlyerModel.kt`:**
```kotlin
data class FlyerModel(
    val id: FlyerId,
    val title: String,
    val description: String,
    val fileUrl: String?,
    val status: FlyerStatus,
    val expiresAt: String?,
    val rejectionReason: String?,        // ← add
    val uploaderId: UserId,
    val createdAt: String,
    val updatedAt: String,
)
```

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Network error | `isLoading = false`, snackbar "Failed to load flyer: …" |
| Flyer not found (404) | `EmptyStateBox("Flyer not found.")` |
| Success, no image URL | `FlyerAsyncImage` shows placeholder |
| Success, rejected | Rejection reason block visible below description |
