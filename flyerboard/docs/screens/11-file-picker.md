# Cross-Cutting: FilePicker

## Overview

File picking is required by two screens:
- **Submit Flyer** (Phase 3) — file is mandatory
- **Edit Flyer** (Phase 4.7) — file is optional (replaces the existing upload)

Because Compose Multiplatform targets WASM, Android, and JVM Desktop, the
file picker must use a Kotlin Multiplatform `expect`/`actual` pattern.
The service layer already accepts `ByteArray + fileName + mimeType`; the only
gap is collecting those values from the platform UI.

---

## Design

### Data class (commonMain)

```kotlin
// shared-app/src/commonMain/…/files/PickedFile.kt
data class PickedFile(
    val bytes: ByteArray,
    val name: String,
    val mimeType: String,
)
```

### Interface (commonMain)

Define the picker as a suspending function rather than a class so each
platform can implement it without inheritance complexity.

```kotlin
// shared-app/src/commonMain/…/files/FilePicker.kt
expect suspend fun pickFile(): PickedFile?
```

`pickFile()` returns `null` if the user cancels the picker without selecting
a file.

### Accepted MIME types

The backend accepts: `image/jpeg`, `image/png`, `image/webp`, `application/pdf`.
Each platform implementation must filter the file chooser to these types.

### Size guard

Validate the selected file is ≤ 10 MB **before** sending it to the ViewModel
so the error is surfaced immediately rather than at upload time:

```kotlin
private const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L  // 10 MB

// Call site example (inside screen or ViewModel):
val picked = pickFile() ?: return
if (picked.bytes.size > MAX_FILE_SIZE_BYTES) {
    // show error
    return
}
viewModel.onFileSelected(picked.bytes, picked.name, picked.mimeType)
```

---

## Platform Implementations

### wasmJs — Browser File API

```kotlin
// shared-app/src/wasmJsMain/…/files/FilePicker.wasmJs.kt

actual suspend fun pickFile(): PickedFile? = suspendCoroutine { cont ->
    val input = document.createElement("input").unsafeCast<HTMLInputElement>()
    input.type = "file"
    input.accept = "image/jpeg,image/png,image/webp,application/pdf"

    input.onchange = { _ ->
        val file = input.files?.item(0)
        if (file == null) {
            cont.resume(null)
        } else {
            val reader = FileReader()
            reader.onload = { event ->
                val arrayBuffer = event.target.unsafeCast<FileReader>().result
                    .unsafeCast<ArrayBuffer>()
                val bytes = Int8Array(arrayBuffer).unsafeCast<ByteArray>()
                cont.resume(
                    PickedFile(
                        bytes = bytes,
                        name = file.name,
                        mimeType = file.type.ifBlank { "application/octet-stream" },
                    )
                )
            }
            reader.readAsArrayBuffer(file)
        }
    }
    // Programmatic click triggers the native file dialog
    input.click()
}
```

**Notes:**
- `suspendCoroutine` bridges the callback-based browser API into a coroutine.
- The `<input>` element is created dynamically and never added to the DOM;
  this is the standard WASM/JS pattern for headless file pickers.
- MIME type falls back to `application/octet-stream` when the browser does
  not report one (rare but possible for certain PDF viewers).

---

### Android — ActivityResult API

```kotlin
// shared-app/src/androidMain/…/files/FilePicker.android.kt

actual suspend fun pickFile(): PickedFile? {
    // FilePicker on Android requires access to an Activity or Fragment context.
    // The recommended approach is to inject the picker through a platform
    // module that holds a reference to the current Activity via a WeakReference.
    //
    // Implementation outline:
    // 1. Create an AndroidFilePicker class that wraps ActivityResultLauncher.
    // 2. Declare `expect class PlatformFilePicker` in commonMain and provide
    //    the Android actual.
    // 3. Register the launcher in MainActivity.onCreate using
    //    registerForActivityResult(GetContent()).
    // 4. Bridge the callback to a CompletableDeferred<PickedFile?>.

    val launcher = LocalFileLauncher.current   // CompositionLocal injected by MainActivity
    return launcher.pick()
}
```

**Concrete steps:**

1. In `MainActivity.kt`, register a result launcher:
   ```kotlin
   val filePickerLauncher = rememberLauncherForActivityResult(
       contract = ActivityResultContracts.GetContent()
   ) { uri: Uri? ->
       filePickerDeferred.complete(uri)
   }
   ```

2. Expose the deferred via a `CompositionLocal`:
   ```kotlin
   val LocalFilePickerLauncher = staticCompositionLocalOf<suspend () -> PickedFile?> {
       error("No FilePicker provided")
   }
   ```

3. In the screen, read the launcher and pass a lambda to the ViewModel:
   ```kotlin
   val pickFile = LocalFilePickerLauncher.current
   FlyerSubmitScreen(onPickFile = { pickFile() })
   ```

4. Inside the lambda, convert the `Uri` to `ByteArray` using
   `contentResolver.openInputStream(uri)?.readBytes()` and resolve the
   MIME type with `contentResolver.getType(uri)`.

---

### JVM Desktop — Swing JFileChooser

```kotlin
// shared-app/src/jvmMain/…/files/FilePicker.jvm.kt

actual suspend fun pickFile(): PickedFile? = withContext(Dispatchers.IO) {
    val chooser = JFileChooser().apply {
        dialogTitle = "Select a flyer file"
        fileFilter = FileNameExtensionFilter(
            "Image or PDF (JPEG, PNG, WebP, PDF)",
            "jpg", "jpeg", "png", "webp", "pdf",
        )
        isMultiSelectionEnabled = false
    }

    // JFileChooser must be shown on the Event Dispatch Thread
    var result: PickedFile? = null
    withContext(Dispatchers.Main) {
        val returnCode = chooser.showOpenDialog(null)
        if (returnCode == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            result = PickedFile(
                bytes = file.readBytes(),
                name = file.name,
                mimeType = resolveMimeType(file),
            )
        }
    }
    result
}

private fun resolveMimeType(file: File): String =
    when (file.extension.lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png"         -> "image/png"
        "webp"        -> "image/webp"
        "pdf"         -> "application/pdf"
        else          -> "application/octet-stream"
    }
```

**Notes:**
- `JFileChooser` blocks its thread; wrapping it in `withContext(Dispatchers.Main)`
  ensures it runs on the Swing EDT without blocking the coroutine dispatcher.
- MIME type is resolved by file extension because the JVM does not have a
  universal MIME API; this is sufficient given the restricted file types.

---

## Integration in Screens

Both screens follow the same pattern. The `pickFile` lambda is passed into the
screen composable from the nav graph call site, keeping the screen composable
itself platform-agnostic.

### Nav graph registration (example for Submit)

```kotlin
// MainActivityScreen.kt
composable(MainDestination.FlyerSubmitDestination::class, typeMap = typeMap) {
    val viewModel: FlyerSubmitViewModel = koinViewModel()
    FlyerSubmitScreen(
        viewModel = viewModel,
        onPickFile = {
            val picked = pickFile()   // platform actual
            if (picked != null) {
                if (picked.bytes.size > MAX_FILE_SIZE_BYTES) {
                    // surface error — could emit a window event from outside the VM
                    // or pass an onError lambda into the screen
                } else {
                    viewModel.onFileSelected(picked.bytes, picked.name, picked.mimeType)
                }
            }
        },
    )
}
```

### Screen composable signature (Submit)

```kotlin
@Composable
fun FlyerSubmitScreen(
    modifier: Modifier = Modifier,
    viewModel: FlyerSubmitViewModel = koinViewModel(),
    onPickFile: suspend () -> Unit = {},
)
```

The `onPickFile` suspend lambda is called from a `rememberCoroutineScope`
inside the screen when the **Choose File** button is tapped:

```kotlin
val scope = rememberCoroutineScope()

OutlinedButton(
    onClick = { scope.launch { onPickFile() } },
    enabled = !uiState.isSubmitting,
) {
    Text("Choose File")
}
```

---

## Files to Create

| File | Target |
|---|---|
| `commonMain/…/files/PickedFile.kt` | Data class |
| `commonMain/…/files/FilePicker.kt` | `expect suspend fun pickFile()` |
| `wasmJsMain/…/files/FilePicker.wasmJs.kt` | Browser File API implementation |
| `androidMain/…/files/FilePicker.android.kt` | ActivityResult implementation |
| `jvmMain/…/files/FilePicker.jvm.kt` | JFileChooser implementation |

All files live under `shared-app/src/<sourceSet>/kotlin/com/cramsan/flyerboard/client/lib/files/`.

---

## Error Handling

| Scenario | Where handled | Behaviour |
|---|---|---|
| User cancels picker | `pickFile()` returns `null` | No-op in screen |
| File > 10 MB | Call site after `pickFile()` | Show error; do not call `onFileSelected` |
| Unsupported MIME type | Call site or ViewModel | Show error |
| Platform picker unavailable | Platform `actual` | Return `null` or throw; screen shows error |
