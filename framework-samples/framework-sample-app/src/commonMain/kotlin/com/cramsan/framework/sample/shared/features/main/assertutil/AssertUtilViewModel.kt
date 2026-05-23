package com.cramsan.framework.sample.shared.features.main.assertutil

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import kotlinx.coroutines.launch

/**
 * ViewModel for the AssertUtil screen.
 */
@FrontendViewModel
class AssertUtilViewModel(dependencies: ViewModelDependencies, private val assertUtil: AssertUtilInterface) :
    BaseViewModel<AssertUtilEvent, AssertUtilUIState>(
        dependencies,
        AssertUtilUIState.Initial,
        TAG,
    ) {
    /**
     * Call assert() with a passing condition (true).
     */
    fun assertTrue() {
        assertUtil.assert(true, TAG, "Assert true — should pass")
    }

    /**
     * Call assert() with a failing condition (false) — logs an error.
     */
    fun assertFalse() {
        assertUtil.assert(false, TAG, "Assert false — should fail (logged)")
    }

    /**
     * Call assertFalse() with a passing condition (false).
     */
    fun assertFalsePasses() {
        assertUtil.assertFalse(false, TAG, "AssertFalse false — should pass")
    }

    /**
     * Call assertFalse() with a failing condition (true) — logs an error.
     */
    fun assertFalseFails() {
        assertUtil.assertFalse(true, TAG, "AssertFalse true — should fail (logged)")
    }

    /**
     * Call assertNull() with a null value — should pass.
     */
    fun assertNullPasses() {
        assertUtil.assertNull(null, TAG, "AssertNull null — should pass")
    }

    /**
     * Call assertNotNull() with a non-null value — should pass.
     */
    fun assertNotNullPasses() {
        assertUtil.assertNotNull("value", TAG, "AssertNotNull value — should pass")
    }

    /**
     * Call assertFailure() — always triggers an error log.
     */
    fun assertFailure() {
        assertUtil.assertFailure(TAG, "AssertFailure — always fails (logged)")
    }

    /**
     * Navigate back to the main menu.
     */
    fun navigateBack() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(SampleWindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "AssertUtilViewModel"
    }
}
