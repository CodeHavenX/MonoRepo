package com.cramsan.edifikana.client.android.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class CameraContract : ActivityResultContract<String, Uri?>() {

    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(context, CameraActivity::class.java)
        intent.putExtra(OUTPUT_FILENAME, input)
        return intent
    }


    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return when (resultCode) {
            //Transforming our result to required format before returning it
            // TODO: Add some more checks here
            Activity.RESULT_OK -> {
                Uri.parse(intent?.getStringExtra(RESULT_FILEPATH) ?: "")
            }
            else -> null
        }
    }
}

