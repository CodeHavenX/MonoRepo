package com.cramsan.framework.test

import kotlin.test.BeforeTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Perform some basic coroutine tests on an Android ViewModel.
 */
class AndroidViewModelTests : CoroutineTest() {

    lateinit var viewModel: AndroidViewModel
    lateinit var repository: Repository

    @BeforeTest
    fun setupTest() {
        repository = Repository()
        viewModel = AndroidViewModel(testCoroutineScope, repository)
    }

    @Test
    fun `Test simple assert`() {
        assertTrue(true)
        assertFalse(false)
        assertEquals("word", "word")
        assertNull(null)
        assertNotEquals<String?>("word", null)
    }

    @Test
    fun `Test for LiveData to be update in suspending function`() = runCoroutineTest {
        assertNull(viewModel.observableInt.value)

        viewModel.updateWithCoroutine()

        assertEquals(100, viewModel.observableInt.value)
    }

    @Test
    fun `Test for LiveData to be update in suspending function and blocking wait`() = runCoroutineTest {
        assertNull(viewModel.observableInt.value)

        viewModel.updateWithCoroutineAndBlockingWait()

        assertEquals(100, viewModel.observableInt.value)
    }

    @Test
    fun `Test for LiveData to be updated with IO dispatch`() = runCoroutineTest {
        assertNull(viewModel.observableInt.value)

        viewModel.updateWithIODispatch()
        advanceUntilIdle()

        assertEquals(100, viewModel.observableInt.value)
    }

    @Test
    fun `Test for LiveData to be updated with IO dispatch and blocking wait`() = runCoroutineTest {
        assertNull(viewModel.observableInt.value)

        viewModel.updateWithIODispatchAndBlockingWait()
        advanceUntilIdle()

        assertEquals(100, viewModel.observableInt.value)
    }

    @Test
    fun `Test for LiveData to be update in scope launch`() = runCoroutineTest {
        assertNull(viewModel.observableInt.value)

        viewModel.updateWithScopeLaunch()
        advanceUntilIdle()

        assertEquals(100, viewModel.observableInt.value)
    }

    @Test
    fun `Test for LiveData to be update in scope launch and blocking wait`() = runCoroutineTest {
        assertNull(viewModel.observableInt.value)

        viewModel.updateWithScopeLaunchAndBlockingWait()
        advanceUntilIdle()

        assertEquals(100, viewModel.observableInt.value)
    }
}
