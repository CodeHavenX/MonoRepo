package com.cramsan.flyerboard.client.lib.filepicker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Browser implementation — shows an `<input type="file">` and reads the chosen file via the File API. */
actual class FilePicker actual constructor(backgroundDispatcher: CoroutineDispatcher) {
    /**
     * Suspending function to open the file picker.
     */
    @OptIn(ExperimentalEncodingApi::class)
    actual suspend fun pickFile(): PickedFile? =
        suspendCancellableCoroutine { continuation ->
            jsPickFile(
                onSuccess = { base64, name, mime ->
                    val bytes = Base64.decode(base64)
                    continuation.resume(PickedFile(bytes = bytes, name = name, mimeType = mime))
                },
                onCancel = {
                    continuation.resume(null)
                },
            )
        }
}

@JsFun(
    """
    (onSuccess, onCancel) => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*,application/pdf';
        let handled = false;
        input.addEventListener('change', async (e) => {
            if (handled) return;
            handled = true;
            const file = e.target.files && e.target.files[0];
            if (!file) { onCancel(); return; }
            try {
                const buffer = await file.arrayBuffer();
                const bytes = new Uint8Array(buffer);
                let binary = '';
                const chunk = 8192;
                for (let i = 0; i < bytes.length; i += chunk) {
                    binary += String.fromCharCode.apply(null, bytes.subarray(i, i + chunk));
                }
                onSuccess(btoa(binary), file.name, file.type);
            } catch (err) {
                onCancel();
            }
        });
        input.addEventListener('cancel', () => {
            if (handled) return;
            handled = true;
            onCancel();
        });
        input.click();
    }
    """,
)
private external fun jsPickFile(onSuccess: (String, String, String) -> Unit, onCancel: () -> Unit)
