package com.cramsan.edifikana.client.lib.features.main.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * Camera contract.
 */
class CameraContract : ActivityResultContract<String, Uri?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, CameraActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return when (resultCode) {
            Activity.RESULT_OK -> {
                intent?.getStringExtra(RESULT_URI)?.let { Uri.parse(it) }
            }
            else -> null
        }
    }
}
