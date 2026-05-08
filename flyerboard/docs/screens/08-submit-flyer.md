# Screen: Submit Flyer

## Overview

Form for submitting a new flyer. Accepts a title, description, optional
expiry date, and a file (JPEG, PNG, WebP, or PDF ≤ 10 MB). On submit the
flyer enters the **Pending** queue awaiting admin approval.

This screen does not exist yet — all files must be created from scratch.

**Route:** `MainDestination.FlyerSubmitDestination` *(new — no args)*

**Auth required:** Yes

---

## Navigation

| Trigger | Destination | Args |
|---|---|---|
| Submit success | back (My Flyers) | — |
| Cancel / system back | back | — |

---

## Files to Create

| File | Path |
|---|---|
| `FlyerSubmitScreen.kt` | `features/main/flyer_submit/` |
| `FlyerSubmitViewModel.kt` | `features/main/flyer_submit/` |
| `FlyerSubmitUIState.kt` | `features/main/flyer_submit/` |
| `FlyerSubmitEvent.kt` | `features/main/flyer_submit/` |
| `FlyerSubmitPreview.kt` | `features/main/flyer_submit/` |

### Files to change

| File | Change |
|---|---|
| `MainDestination.kt` | Add `FlyerSubmitDestination` |
| `MainActivityScreen.kt` | Register the route |
| `ViewModelModule.kt` | Register `FlyerSubmitViewModel` |

---

## Destination

**`MainDestination.kt`** — add:

```kotlin
@Serializable
data object FlyerSubmitDestination : MainDestination()
```

No arguments — the form always starts empty.

---

## UI Layer

### Shared components used

| Component | From |
|---|---|
| `LoadingStateBox` | `shared-ui` (Phase 1.5) — shown while submitting |

### Layout

```
Scaffold
  topBar:
    TopAppBar
      title: Text("Submit Flyer")
      navigationIcon: IconButton(ArrowBack) → viewModel.navigateBack()
  content:
    Box(fillMaxSize, contentAlignment = Center)
      when isSubmitting → LoadingStateBox()
      else              → FlyerSubmitForm(...)

FlyerSubmitForm:
  Column(fillMaxSize, verticalScroll, padding = Padding.MEDIUM, spacedBy(Padding.MEDIUM))

    OutlinedTextField          ← title     (required, max 200 chars)
      label = "Title"
      enabled = !uiState.isSubmitting
      value = uiState.title
      onValueChange = viewModel::onTitleChanged
      supportingText = char count "n / 200" when focused

    OutlinedTextField          ← description (required, max 2000 chars, minLines = 4)
      label = "Description"
      enabled = !uiState.isSubmitting
      value = uiState.description
      onValueChange = viewModel::onDescriptionChanged
      supportingText = char count "n / 2000" when focused

    OutlinedTextField          ← expiresAt (optional, ISO-8601 date string)
      label = "Event / Expiry Date (optional)"
      placeholder = "YYYY-MM-DD"
      enabled = !uiState.isSubmitting
      value = uiState.expiresAt ?: ""
      onValueChange = viewModel::onExpiresAtChanged

    ── File picker section ──
    Text("Flyer File", style = labelMedium)
    Row(verticalAlignment = CenterVertically, spacedBy(Padding.SMALL))
      OutlinedButton("Choose File") → viewModel.onPickFileRequested()  ← triggers FilePicker
      if uiState.selectedFileName != null:
        Text(uiState.selectedFileName, style = bodySmall, maxLines = 1, overflow = Ellipsis)
      else:
        Text("No file selected", style = bodySmall, color = outline)

    if uiState.errorMessage != null:
      Text(uiState.errorMessage, style = bodyMedium, color = error)

    Button(
      onClick = viewModel::submit,
      modifier = Modifier.fillMaxWidth(),
      enabled = !uiState.isSubmitting && uiState.isFormValid,
    )
      Text("Submit")
```

### File picker integration

File picking is platform-specific. The ViewModel exposes `onPickFileRequested()`,
which tells the Screen to invoke the platform `FilePicker`. The screen
handles the platform call and passes the result back to the ViewModel via
`onFileSelected(bytes, name, mime)`.

In Compose, a clean pattern is to use a `LaunchedEffect` triggered by a flag
in UIState, or to pass a `FilePicker` lambda into the screen composable from
the call site (the nav graph). The recommended approach:

```kotlin
// In MainActivityScreen.kt, create a platform-specific FilePicker and pass it:
composable(MainDestination.FlyerSubmitDestination::class) {
    val filePicker = rememberFilePicker()   // platform expect/actual
    FlyerSubmitScreen(
        onPickFile = {
            val result = filePicker.pick()
            result?.let { viewModel.onFileSelected(it.bytes, it.name, it.mimeType) }
        }
    )
}
```

See `FilePicker` expect/actual design in `implementation-plan.md` Phase 4.7.

---

## UIState

```kotlin
data class FlyerSubmitUIState(
    val title: String,
    val description: String,
    val expiresAt: String?,
    val selectedFileName: String?,         // display name of the chosen file
    val selectedFileBytes: ByteArray?,     // raw bytes to upload
    val selectedMimeType: String?,         // MIME type of the chosen file
    val isSubmitting: Boolean,
    val errorMessage: String?,
) : ViewModelUIState {
    val isFormValid: Boolean
        get() = title.isNotBlank()
            && description.isNotBlank()
            && selectedFileBytes != null

    companion object {
        val Initial = FlyerSubmitUIState(
            title = "",
            description = "",
            expiresAt = null,
            selectedFileName = null,
            selectedFileBytes = null,
            selectedMimeType = null,
            isSubmitting = false,
            errorMessage = null,
        )
    }
}
```

`isFormValid` is a computed property used to enable/disable the Submit button.
`selectedFileBytes` is in UIState for simplicity; on large files consider
storing only the path and reading bytes at submission time.

---

## Event

```kotlin
sealed class FlyerSubmitEvent : ViewModelEvent {
    data object Noop : FlyerSubmitEvent()
}
```

Errors and navigation are surfaced via `FlyerBoardWindowsEvent`. Start with
`Noop` only.

---

## ViewModel

**Class:** `FlyerSubmitViewModel`
**Dependencies:** `ViewModelDependencies`, `FlyerManager`

```kotlin
@FrontendViewModel
class FlyerSubmitViewModel(
    dependencies: ViewModelDependencies,
    private val flyerManager: FlyerManager,
) : BaseViewModel<FlyerSubmitEvent, FlyerSubmitUIState>(
    dependencies, FlyerSubmitUIState.Initial, TAG,
)
```

### Methods

#### `onTitleChanged(title: String)`
```kotlin
fun onTitleChanged(title: String) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(title = title.take(200)) }
    }
}
```
Enforces the 200-character limit client-side.

#### `onDescriptionChanged(description: String)`
```kotlin
fun onDescriptionChanged(description: String) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(description = description.take(2000)) }
    }
}
```

#### `onExpiresAtChanged(expiresAt: String)`
```kotlin
fun onExpiresAtChanged(expiresAt: String) {
    viewModelCoroutineScope.launch {
        updateUiState { it.copy(expiresAt = expiresAt.takeIf { it.isNotBlank() }) }
    }
}
```

#### `onFileSelected(bytes: ByteArray, name: String, mimeType: String)`
```kotlin
fun onFileSelected(bytes: ByteArray, name: String, mimeType: String) {
    viewModelCoroutineScope.launch {
        updateUiState {
            it.copy(
                selectedFileBytes = bytes,
                selectedFileName = name,
                selectedMimeType = mimeType,
                errorMessage = null,
            )
        }
    }
}
```

#### `submit()`
```kotlin
fun submit() {
    val state = uiState.value
    if (!state.isFormValid) return

    viewModelCoroutineScope.launch {
        updateUiState { it.copy(isSubmitting = true, errorMessage = null) }
        flyerManager.createFlyer(
            title = state.title,
            description = state.description,
            expiresAt = state.expiresAt,
            fileBytes = state.selectedFileBytes!!,
            fileName = state.selectedFileName!!,
            mimeType = state.selectedMimeType!!,
        ).onSuccess {
            updateUiState { it.copy(isSubmitting = false) }
            emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
        }.onFailure { error ->
            updateUiState { it.copy(isSubmitting = false, errorMessage = error.message) }
            emitWindowEvent(
                FlyerBoardWindowsEvent.ShowSnackbar("Failed to submit flyer: ${error.message}")
            )
        }
    }
}
```

#### `navigateBack()`
```kotlin
fun navigateBack() {
    viewModelCoroutineScope.launch {
        emitWindowEvent(FlyerBoardWindowsEvent.NavigateBack)
    }
}
```

### DI registration

**`ViewModelModule.kt`** — add:
```kotlin
viewModelOf(::FlyerSubmitViewModel)
```

---

## Manager Layer

**Class:** `FlyerManager`
**Method:** `createFlyer(...)` — already implemented, no changes needed.

```kotlin
suspend fun createFlyer(
    title: String,
    description: String,
    expiresAt: String?,
    fileBytes: ByteArray,
    fileName: String,
    mimeType: String,
): Result<FlyerModel>
```

---

## Service Layer

**Interface:** `FlyerService`
**Method:** `createFlyer(...)` — already implemented, no changes needed.

```
POST /api/v1/flyers
Content-Type: multipart/form-data
Authorization: Bearer <supabase-jwt>

Fields:
  title        (string, required)
  description  (string, required)
  expires_at   (ISO-8601 string, optional)
  file         (binary, required — JPEG / PNG / WebP / PDF, ≤ 10 MB)
```

**Response 200** — `FlyerObject` with `status: "pending"`
**Response 400** — unsupported file type or file too large
**Response 401** — not authenticated

The response is mapped to `FlyerModel` via `FlyerNetworkResponse.toFlyerModel()`.
The returned model is not used directly (the ViewModel navigates back on
success), but it could be used to show a confirmation snackbar with the
flyer's title.

---

## Error Handling

| Scenario | Behaviour |
|---|---|
| Blank title or description | Submit button disabled (`isFormValid = false`) |
| No file selected | Submit button disabled |
| 400 file too large | `errorMessage` set; snackbar shown |
| 400 unsupported type | `errorMessage` set; snackbar shown |
| 401 session expired | Snackbar; user should re-authenticate |
| Network error | `isSubmitting = false`, snackbar with message |
| Success | Navigate back to My Flyers |

---

## Previews

**`FlyerSubmitPreview.kt`** — two previews:

1. `FlyerSubmitEmptyPreview` — initial empty form, Submit button disabled.
2. `FlyerSubmitFilledPreview` — all fields filled, file selected, Submit button enabled.
