package com.cramsan.edifikana.client.lib.features.admin.property

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class PropertyViewModelTest : TestBase() {

    private lateinit var viewModel: PropertyViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var staffManager: StaffManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = SharedFlowApplicationReceiver()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        propertyManager = mockk(relaxed = true)
        staffManager = mockk(relaxed = true)
        viewModel = PropertyViewModel(
            propertyManager = propertyManager,
            staffManager = staffManager,
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
            )
        )
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(EdifikanaApplicationEvent.NavigateBack, awaitItem())
            }
        }
        viewModel.navigateBack()
        verificationJob.join()
    }

    @Test
    fun `test saveChanges with valid data updates property`() = runBlockingTest {
        val propertyId = PropertyId("123")
        val name = "Updated Property"
        val address = "Updated Address"
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(
            PropertyModel(
                id = propertyId,
                name = "Test Property",
                address = "Test Address",
            )
        )
        coEvery { propertyManager.updateProperty(propertyId, name, address) } returns Result.success(mockk())
        coEvery { staffManager.getStaffList() } returns Result.success(emptyList())

        viewModel.loadContent(propertyId)
        viewModel.saveChanges(name, address)

        coVerify { propertyManager.updateProperty(propertyId, name, address) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test saveChanges with failure shows error snackbar`() = runBlockingTest {
        val propertyId = PropertyId("123")
        val name = "Updated Property"
        val address = "Updated Address"
        coEvery { propertyManager.getProperty(propertyId) } returns Result.success(
            PropertyModel(
                id = propertyId,
                name = "Test Property",
                address = "Test Address",
            )
        )
        coEvery { propertyManager.updateProperty(propertyId, name, address) } returns Result.failure(Exception("Error"))
        coEvery { staffManager.getStaffList() } returns Result.success(emptyList())


        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.ShowSnackbar("There was an unexpected error."),
                    awaitItem()
                )
            }
        }
        viewModel.loadContent(propertyId)
        viewModel.saveChanges(name, address)

        verificationJob.join()
    }

    @Test
    fun `test addManager with valid email updates UI state`() = runBlockingTest {
        val email = "test@example.com"
        viewModel.addManager(email)
        assertEquals(listOf(email), viewModel.uiState.value.managers)
        assertTrue(!viewModel.uiState.value.addManagerError)
    }

    @Test
    fun `test addManager with invalid email shows error`() = runBlockingTest {
        val email = "invalid-email"
        viewModel.addManager(email)
        assertTrue(viewModel.uiState.value.addManagerError)
        assertEquals(email, viewModel.uiState.value.addManagerEmail)
    }

    @Test
    fun `test removeManager updates UI state`() = runBlockingTest {
        val email = "test@example.com"
        viewModel.addManager(email)
        viewModel.removeManager(email)
        assertTrue(viewModel.uiState.value.managers.isEmpty())
    }

    @Test
    fun `test requestNewSuggestions with valid query updates suggestions`() = runBlockingTest {
        val staffList = listOf<StaffModel>(
            mockk(),
            mockk(),
        )
        val property = PropertyModel(
            id = PropertyId("123"),
            name = "Test Property",
            address = "Test Address",
        )
        coEvery { staffManager.getStaffList() } returns Result.success(staffList)
        coEvery { propertyManager.getProperty(PropertyId("123")) } returns Result.success(property)

        viewModel.loadContent(PropertyId("123"))
        viewModel.requestNewSuggestions("test")

        assertEquals(property.name, viewModel.uiState.value.propertyName)
        assertEquals(property.address, viewModel.uiState.value.address)
    }

    @Test
    fun `test requestNewSuggestions with short query clears suggestions`() = runBlockingTest {
        viewModel.requestNewSuggestions("te")
        assertTrue(viewModel.uiState.value.suggestions.isEmpty())
    }
}