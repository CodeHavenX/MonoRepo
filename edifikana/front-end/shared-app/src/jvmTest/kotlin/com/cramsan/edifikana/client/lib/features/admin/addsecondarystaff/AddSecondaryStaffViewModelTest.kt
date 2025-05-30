package com.cramsan.edifikana.client.lib.features.admin.addsecondarystaff

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEventBus
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import edifikana_lib.Res
import edifikana_lib.text_please_complete_fields
import edifikana_lib.text_there_was_an_error_processing_request
import edifikana_lib.title_timecard_add_staff
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddSecondaryStaffViewModelTest : TestBase() {

    private lateinit var viewModel: AddSecondaryStaffViewModel
    private lateinit var staffManager: StaffManager
    private lateinit var propertyManager: PropertyManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: ApplicationEventBus
    private lateinit var windowEventBus: WindowEventBus
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = ApplicationEventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        staffManager = mockk(relaxed = true)
        propertyManager = mockk(relaxed = true)
        stringProvider = mockk()
        windowEventBus = WindowEventBus()
        viewModel = AddSecondaryStaffViewModel(
            staffManager = staffManager,
            propertyManager = propertyManager,
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            stringProvider = stringProvider,
        )
    }

    @Test
    fun `test onBackSelected emits NavigateBack event`() = runBlockingTest {
        // Setup

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.onBackSelected()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test saveStaff with invalid data shows error snackbar`() = runBlockingTest {
        // Setup
        coEvery { stringProvider.getString(Res.string.text_please_complete_fields) } returns "Please complete all fields."

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Please complete all fields."),
                    awaitItem()
                )
            }
        }
        viewModel.saveStaff(null, null, null, null, null)

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test saveStaff with valid data saves staff and navigates back`() = runBlockingTest {
        // Setup
        val id = "123"
        val idType = IdType.DNI
        val name = "John"
        val lastName = "Doe"
        val role = StaffRole.SECURITY
        val propertyId = PropertyId("propertyId")
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(propertyId)
        coEvery { staffManager.addStaff(any()) } returns Result.success(mockk())
        coEvery { stringProvider.getString(Res.string.title_timecard_add_staff) } returns "Add Staff"

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.saveStaff(id, idType, name, lastName, role)

        // Assert
        verificationJob.join()
        coVerify {
            staffManager.addStaff(
                StaffModel.CreateStaffRequest(
                    idType = idType,
                    firstName = name,
                    lastName = lastName,
                    role = role,
                    propertyId = propertyId
                )
            )
        }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test saveStaff with valid data but failure response shows error snackbar`() = runBlockingTest {
        // Setup
        val id = "123"
        val idType = IdType.PASSPORT
        val name = "John"
        val lastName = "Doe"
        val role = StaffRole.SECURITY
        val propertyId = PropertyId("propertyId")
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(propertyId)
        coEvery { staffManager.addStaff(any()) } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_timecard_add_staff) } returns "Add Staff"
        coEvery { stringProvider.getString(Res.string.text_there_was_an_error_processing_request) } returns "There was an error processing the request."

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("There was an error processing the request."),
                    awaitItem()
                )
            }
        }

        viewModel.saveStaff(id, idType, name, lastName, role)

        // Assert
        verificationJob.join()
        coVerify {
            staffManager.addStaff(
                StaffModel.CreateStaffRequest(
                    idType = idType,
                    firstName = name,
                    lastName = lastName,
                    role = role,
                    propertyId = propertyId
                )
            )
        }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }
}