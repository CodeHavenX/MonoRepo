package com.cramsan.framework.core

import android.app.ActionBar
import android.app.Activity
import android.os.Bundle
import androidx.annotation.CallSuper
import com.cramsan.framework.logging.logD
import com.google.android.material.appbar.MaterialToolbar

/**
 * A base class that extends [BaseViewActivity] to extend it with View based logic.
 * A [contentViewLayout] is required and it should be a valid layout file. The [toolbarViewId] is
 * optional. If provided, this toolbar will be set as the [ActionBar] for this activity. [logTag] is
 * required so we can identify the source of the events.
 */
abstract class BaseViewActivity<T : BaseViewModel> : BaseActivity<T>() {

    /**
     * Id of the layout that will be automatically inflated by this [Activity].
     */
    protected abstract val contentViewLayout: Int

    /**
     * Id of the [MaterialToolbar] in the layout [contentViewLayout].
     */
    protected abstract val toolbarViewId: Int?

    /**
     * [MaterialToolbar] managed by the [BaseViewActivity]. The instance is set by providing the [toolbarViewId]. If the
     * layout does not contain a [MaterialToolbar] with id [toolbarViewId], then this instance will be null.
     */
    protected var toolbar: MaterialToolbar? = null
        private set

    @CallSuper
    @Suppress("UndocumentedPublicProperty")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD(logTag, "onCreate")
        setContentView(contentViewLayout)

        toolbar = toolbarViewId?.let { findViewById(it) }
        toolbar?.let { setSupportActionBar(it) }
    }
}
