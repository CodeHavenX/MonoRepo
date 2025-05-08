package com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import edifikana_lib.Res
import edifikana_lib.role_admin
import edifikana_lib.role_security
import edifikana_lib.time_card_event_clock_in
import edifikana_lib.time_card_event_clock_out
import edifikana_lib.title_timecard_view_staff
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class ViewStaffViewModelTest : TestBase() {

    private lateinit var staffManager: StaffManager
    private lateinit var timeCardManager: TimeCardManager
    private lateinit var storageService: StorageService
    private lateinit var propertyManager: PropertyManager
    private lateinit var viewModel: ViewStaffViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        staffManager = mockk()
        timeCardManager = mockk()
        storageService = mockk()
        propertyManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        stringProvider = mockk()
        viewModel = ViewStaffViewModel(
            staffManager,
            timeCardManager,
            storageService,
            propertyManager,
            mockk(),
            stringProvider = stringProvider,
            dependencies,
        )
    }

    @Test
    fun `test loadStaff successfully loads staff and records`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("123")
        val staff = StaffModel(
            id = staffId,
            idType = IdType.PASSPORT,
            name = "John",
            lastName = "Doe",
            role = StaffRole.SECURITY,
            email = "john.doe@test.com",
            status = StaffStatus.ACTIVE,
        )
        val records = listOf(
            TimeCardRecordModel(
                id = TimeCardEventId("1"),
                staffPk = staffId,
                eventType = TimeCardEventType.CLOCK_IN,
                propertyId = PropertyId("propertyId"),
                eventTime = 23524543,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
                entityId = "entityId",
            ),
            TimeCardRecordModel(
                id = TimeCardEventId("2"),
                staffPk = staffId,
                eventType = TimeCardEventType.CLOCK_OUT,
                propertyId = PropertyId("propertyId"),
                eventTime = 32523532126,
                imageUrl = "imageUrl",
                imageRef = "imageRef",
                entityId = "entityId",
            )
        )
        coEvery { staffManager.getStaff(staffId) } returns Result.success(staff)
        coEvery { timeCardManager.getRecords(staffId) } returns Result.success(records)
        coEvery { stringProvider.getString(Res.string.title_timecard_view_staff) } returns "View Staff"
        coEvery { stringProvider.getString(Res.string.role_security) } returns "Security"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_out) } returns "Clock Out"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_in) } returns "Clock In"

        // Act
        viewModel.loadStaff(staffId)

        // Assert
        coVerify { staffManager.getStaff(staffId) }
        coVerify { timeCardManager.getRecords(staffId) }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("John Doe", uiState.staff?.fullName)
        assertEquals(2, uiState.records.size)
    }

    @Test
    fun `test loadStaff handles failure`() = runBlockingTest {
        // Arrange
        val staffId = StaffId("123")
        coEvery { staffManager.getStaff(staffId) } returns Result.failure(Exception("Error"))
        coEvery { timeCardManager.getRecords(staffId) } returns Result.failure(Exception("Error"))

        // Act
        viewModel.loadStaff(staffId)

        // Assert
        coVerify { staffManager.getStaff(staffId) }
        coVerify { timeCardManager.getRecords(staffId) }
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.isLoading)
        assertEquals("", uiState.staff?.fullName)
        assertEquals(0, uiState.records.size)
    }

    @Test
    fun `test share emits ShareContent event`() = runBlockingTest {
        // Arrange
        val timeCardEventId = TimeCardEventId("1")
        val record = TimeCardRecordModel(
            id = timeCardEventId,
            staffPk = StaffId("123"),
            eventType = TimeCardEventType.CLOCK_IN,
            propertyId = PropertyId("propertyId"),
            eventTime = 23524543,
            imageUrl = "imageUrl",
            imageRef = "imageRef",
            entityId = "entityId",
        )
        val staff = StaffModel(
            id = StaffId("123"),
            idType = IdType.PASSPORT,
            name = "John",
            lastName = "Doe",
            role = StaffRole.SECURITY,
            email = "john.doe@test.com",
            status = StaffStatus.ACTIVE,
        )
        coEvery { storageService.downloadFile(any()) } returns Result.success(CoreUri.createUri("http://example.com/image.jpg"))
        coEvery { staffManager.getStaff(any()) } returns Result.success(staff)
        coEvery { timeCardManager.getRecords(any()) } returns Result.success(listOf(record))
        coEvery { stringProvider.getString(Res.string.title_timecard_view_staff) } returns "View Staff"
        coEvery { stringProvider.getString(Res.string.role_security) } returns "Security"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_out) } returns "Clock-Out"
        coEvery { stringProvider.getString(Res.string.time_card_event_clock_in) } returns "Clock-In"

        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.ShareContent(
                        "Clock-In de John Doe\n1970-09-30T02:35:43",
                        CoreUri.createUri("http://example.com/image.jpg")
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.loadStaff(StaffId("123")).join()
        viewModel.share(timeCardEventId).join()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                viewModel.navigateBack()
                assertEquals(
                    EdifikanaApplicationEvent.NavigateBack,
                    awaitItem()
                )
            }
        }

        // Assert
        verificationJob.join()
    }
}