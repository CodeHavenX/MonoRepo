# Screen: Edit Flyer

## Overview

Allows the uploader to update the title, description, expiry date, or replace
the file. Saving resets the flyer to **Pending** and triggers re-moderation.
The form is pre-populated with the existing flyer data loaded from the API.

**Route:** `MainDestination.FlyerEditDestination(flyerId: String)`

**Auth required:** Yes (uploader or admin)

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Save success | back (My Flyers) | — |
| Back / Cancel | back | — |

---

## Route Arguments

`FlyerEditDestination` carries `flyerId: String` — the raw UUID string passed
to `viewModel.loadFlyer(destination.flyerId)` on `ON_CREATE`.

---

## Files

| File | Path | Status |
|---|---|---|
| `FlyerEditScreen.kt` | `features/main/flyer_edit/` | Needs file picker section |
| `FlyerEditViewModel.kt` | `features/main/flyer_edit/` | Needs `onFileSelected()` + update `saveFlyer()` |
| `FlyerEditUIState.kt` | `features/main/flyer_edit/` | Needs file fields |
| `FlyerEditEvent.kt` | `features/main/flyer_edit/` | Complete |
| `FlyerEditPreview.kt` | `features/main/flyer_edit/` | Needs update |

---

## UI Layer

### Shared components used

| Component | From |
|---|---|
| `LoadingStateBox` | `shared-ui` (Phase 1.5) — while loading flyer data |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("Edit Flyer")
      navigationIcon: IconButton(ArrowBack) → viewModel.navigateBack()
  content:
    Box(fillMaxSize, contentAlignment = Center)
      when isLoading   → LoadingStateBox()
      else             → FlyerEditForm(uiState, viewModel)

FlyerEditForm:
  Column(fillMaxSize, verticalScroll, padding = Padding.MEDIUM, spacedBy(Padding.MEDIUM))

    OutlinedTextField        ← title (required, max 200 chars)
      label = "Title"
      enabled = !uiState.isSaving
      value = uiState.title
      onValueChange = viewModel::onTitleChanged
      supportingText = "n / 200"

    OutlinedTextField        ← description (required, max 2000 chars, minLines = 4)
      label = "Description"
      enabled = !uiState.isSaving
      value = uiState.description
      onValueChange = viewModel::onDescriptionChanged
      supportingText = "n / 2000"

    OutlinedTextField        ← expiresAt (optional, ISO-8601)
      label = "Event / Expiry Date (optional)"
      placeholder = "YYYY-MM-DD"
      enabled = !uiState.isSaving
      value = uiState.expiresAt ?: ""
      onValueChange = viewModel::onExpiresAtChanged

    ── File picker section (new) ──
    Text("Replace File (optional)", style = labelMedium)
    Row(verticalAlignment = CenterVertically, spacedBy(Padding.SMALL))
      OutlinedButton(
        "Choose File",
        enabled = !uiState.isSaving,
      ) → onPickFile()
      if uiState.selectedFileName != null:
        Text(uiState.selectedFileName, style = bodySmall, maxLines = 1, overflow = Ellipsis)
      else:
        Text("No replacement file selected", style = bodySmall, color = outline)

    if uiState.errorMessage != null:
      Text(uiState.errorMessage, color = error)

    Button(
      onClick = { viewModel.saveFlyer(destination.flyerId) },
      modifier = Modifier.fillMaxWidth(),
      enabled = !uiState.isSaving,
    )
      if uiState.isSaving: CircularProgressIndicator(Modifier.size(20.dp))
      else: Text("Save")
```

The file picker section is labelled "Replace File (optional)" because the
file is only replaced if a new one is chosen. Leaving the picker empty keeps
the original file.

---

## UIState

### Current

```kotlin
data class FlyerEditUIState(
    val isLoading: Boolean,
    val isSaving: Boolean,
    val title: String,
    val description: String,
    val expiresAt: String?,
    val errorMessage: String?,
) : ViewModelUIState
```

### Target

```kotlin
data class FlyerEditUIState(
    val isLoading: Boolean,
    val isSaving: Boolean,
    val title: String,
    val description: String,
    val expiresAt: String?,
    val selectedFileName: String?,     // display name of a replacement file (null = no replacement)
    val selectedFileBytes: ByteArray?, // raw bytes (null = keep original)
    val selectedMimeType: String?,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerEditUIState(
            isLoading = false,
            isSaving = false,
            title = "",
            description = "",
            expiresAt = null,
            selectedFileName = null,
            selectedFileBytes = null,
            selectedMimeType = null,
            errorMessage = null,
        )
    }
}
```

---

## Event

```kotlin
sealed class FlyerEditEvent : ViewModelEvent {
    data object Noop : FlyerEditEvent()
}
```

No changes needed.

---

## ViewModel

**Class:** `FlyerEditViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

### Existing methods (no changes needed)

#### `loadFlyer(flyerIdValue: String)`
Fetches `flyerManager.getFlyer(FlyerId(flyerIdValue))` and populates `title`,
`description`, `expiresAt` in the state. Shows snackbar if not found or on error.

#### `onTitleChanged(title: String)` / `onDescriptionChanged` / `onExpiresAtChanged`
Standard field update helpers. No changes.

#### `navigateBack()`
Emits `NavigateBack`.

### Method to add

#### `onFileSelected(bytes: ByteArray, name: String, mimeType: String)`
```kotlin
fun onFileSelected(bytes: ByteArray, name: String, mimeType: String) {
    viewModelCoroutineScope.launch {
        updateUiState {
            it.copy(
                selectedFileBytes = bytes,
                selectedFileName = name,
                selectedMimeType = mimeType,
            )
        }
    }
}
```

### Method to change

#### `saveFlyer(flyerIdValue: String)`
Pass file data to `flyerManager.updateFlyer` when a replacement file was selected:

```kotlin
fun saveFlyer(flyerIdValue: String) {
    viewModelCoroutineScope.launch {
        val state = uiState.value
        updateUiState { it.copy(isSaving = true, errorMessage = null) }
        flyerManager.updateFlyer(
            flyerId = FlyerId(flyerIdValue),
            title = state.title,
            description = state.description,
            expiresAt = state.expiresAt,
            fileBytes = state.selectedFileBytes,     // null → keeps original file
            fileName = state.selectedFileName,
            mimeType = state.selectedMimeType,
        ).onSuccess {
            updateUiState { it.copy(isSaving = false) }
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }.onFailure { error ->
            updateUiState { it.copy(isSaving = false, errorMessage = error.message) }
            emitWindowEvent(
                FlyerBoardWindowsEvent.ShowSnackbar("Failed to save flyer: ${error.message}")
            )
        }
    }
}
```

The service layer's `updateFlyer` already accepts nullable file parameters and
sends the file part only when non-null.

---

## Manager Layer

**Class:** `FlyerManager`

Two methods are used:

### `getFlyer(flyerId: FlyerId): Result<FlyerModel?>`
Used in `loadFlyer()` to pre-populate the form. No changes.

### `updateFlyer(flyerId, title, description, expiresAt, fileBytes, fileName, mimeType)`
Used in `saveFlyer()`. Already accepts nullable file parameters. No changes.

---

## Service Layer

**Interface:** `FlyerService`

### `getFlyer` — no changes
```
GET /api/v1/flyers/{id}
No auth required.
```

### `updateFlyer` — no changes
```
PUT /api/v1/flyers/{id}
Content-Type: multipart/form-data
Authorization: Bearer <supabase-jwt>

Fields (all optional — omit to keep current value):
  title
  description
  expires_at
  file  (JPEG / PNG / WebP / PDF, ≤ 10 MB — only sent when a replacement is chosen)
```

**Response 200** — updated `FlyerObject` with `status: "pending"`
**Response 401** — not authenticated
**Response 403** — caller is not the uploader (and not an admin)
**Response 404** — flyer not found

The multipart body is built in `FlyerServiceImpl.updateFlyer`:
file parts are only appended when `fileBytes != null && fileName != null && mimeType != null`.

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Flyer not found on load | Snackbar "Flyer not found."; `isLoading = false`; empty form |
| 403 on save | Snackbar "Failed to save flyer: Forbidden" |
| 401 on save | Snackbar "Failed to save flyer: Unauthorized" |
| Network error | Snackbar with error message; `isSaving = false` |
| Success | Navigate back; flyer status resets to **Pending** on the server |

---

## Previews

**`FlyerEditPreview.kt`** — three previews:

1. `FlyerEditScreenLoadingPreview` — `isLoading = true` (already exists).
2. `FlyerEditScreenPreview` — form populated with sample data, no replacement file (already exists).
3. `FlyerEditScreenWithFilePreview` *(new)* — form populated, replacement file selected (`selectedFileName = "banner.jpg"`).
