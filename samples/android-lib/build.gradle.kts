plugins {
    id("com.cramsan.kotlin-mpp-common")
    id("com.cramsan.kotlin-mpp-android-lib")
}

kotlin {
    androidLibrary {
        namespace = "com.cramsan.samples.android.lib"
    }
}
