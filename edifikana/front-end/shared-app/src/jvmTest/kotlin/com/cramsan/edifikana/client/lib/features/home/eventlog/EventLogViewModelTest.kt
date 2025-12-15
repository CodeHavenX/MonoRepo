package com.cramsan.edifikana.client.lib.features.home.eventlog

import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [EventLogViewModel].
 */
class EventLogViewModelTest : CoroutineTest() {

    private lateinit var viewModel: EventLogViewModel
    private lateinit var eventLogManager: EventLogManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        eventLogManager = mockk(relaxed = true)
        viewModel = EventLogViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            eventLogManager = eventLogManager,
        )
    }

    @Test
    fun `test initial UI state`() = runCoroutineTest {
        // Assert initial state
        val initialState = viewModel.uiState.value
        assertTrue(initialState.isLoading)
        assertTrue(initialState.events.isEmpty())
    }

    @Test
    fun `test loadEvents with successful response updates UI state`() = runCoroutineTest {
        // Set up
        val propertyId = PropertyId("property-1")
        val records = listOf(
            createEventLogRecord(
                id = EventLogEntryId("event-1"),
                title = "Test Event 1",
                description = "Description 1",
                unit = "Unit A",
            ),
            createEventLogRecord(
                id = EventLogEntryId("event-2"),
                title = "Test Event 2",
                description = "Description 2",
                unit = "Unit B",
            ),
        )
        coEvery { eventLogManager.getRecords(propertyId) } returns Result.success(records)

        // Act
        viewModel.loadEvents(propertyId)

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.events.size)
        assertEquals("Test Event 1", state.events[0].title)
        assertEquals("Description 1", state.events[0].description)
        assertEquals("Unit A", state.events[0].unit)
        assertEquals("Test Event 2", state.events[1].title)
        coVerify { eventLogManager.getRecords(propertyId) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test loadEvents with empty response updates UI state with empty list`() = runCoroutineTest {
        // Set up
        val propertyId = PropertyId("property-1")
        coEvery { eventLogManager.getRecords(propertyId) } returns Result.success(emptyList())

        // Act
        viewModel.loadEvents(propertyId)

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.events.isEmpty())
        coVerify { eventLogManager.getRecords(propertyId) }
    }

    @Test
    fun `test loadEvents with failure updates UI state with empty list`() = runCoroutineTest {
        // Set up
        val propertyId = PropertyId("property-1")
        val error = RuntimeException("Network error")
        coEvery { eventLogManager.getRecords(propertyId) } returns Result.failure(error)

        // Act
        viewModel.loadEvents(propertyId)

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.events.isEmpty())
        coVerify { eventLogManager.getRecords(propertyId) }
    }

    @Test
    fun `test loadEvents transforms records to UI models correctly`() = runCoroutineTest {
        // Set up
        val propertyId = PropertyId("property-1")
        val record = createEventLogRecord(
            id = EventLogEntryId("event-1"),
            title = "Water Leak",
            description = "Leak in basement",
            unit = "Basement",
            eventType = EventLogEventType.INCIDENT,
            fallbackEventType = "Custom Type",
            fallbackEmployeeName = "John Doe",
        )
        coEvery { eventLogManager.getRecords(propertyId) } returns Result.success(listOf(record))

        // Act
        viewModel.loadEvents(propertyId)

        // Assert
        val state = viewModel.uiState.value
        assertEquals(1, state.events.size)
        val uiModel = state.events[0]
        assertEquals(EventLogEntryId("event-1"), uiModel.id)
        assertEquals("Water Leak", uiModel.title)
        assertEquals("Leak in basement", uiModel.description)
        assertEquals("Basement", uiModel.unit)
        assertEquals(EventLogEventType.INCIDENT, uiModel.eventType)
        assertEquals("Custom Type", uiModel.fallbackEventType)
        assertEquals("John Doe", uiModel.employeeName)
    }

    @Test
    fun `test loadEvents for different properties`() = runCoroutineTest {
        // Set up
        val propertyId1 = PropertyId("property-1")
        val propertyId2 = PropertyId("property-2")
        val records1 = listOf(
            createEventLogRecord(
                id = EventLogEntryId("event-1"),
                title = "Event for Property 1",
            ),
        )
        val records2 = listOf(
            createEventLogRecord(
                id = EventLogEntryId("event-2"),
                title = "Event for Property 2",
            ),
            createEventLogRecord(
                id = EventLogEntryId("event-3"),
                title = "Another Event for Property 2",
            ),
        )
        coEvery { eventLogManager.getRecords(propertyId1) } returns Result.success(records1)
        coEvery { eventLogManager.getRecords(propertyId2) } returns Result.success(records2)

        // Act - load first property
        viewModel.loadEvents(propertyId1)
        val stateAfterFirst = viewModel.uiState.value

        // Assert first load
        assertEquals(1, stateAfterFirst.events.size)
        assertEquals("Event for Property 1", stateAfterFirst.events[0].title)

        // Act - load second property
        viewModel.loadEvents(propertyId2)
        val stateAfterSecond = viewModel.uiState.value

        // Assert second load replaces events
        assertEquals(2, stateAfterSecond.events.size)
        assertEquals("Event for Property 2", stateAfterSecond.events[0].title)
        assertEquals("Another Event for Property 2", stateAfterSecond.events[1].title)
    }

    /**
     * Helper function to create an EventLogRecordModel for testing.
     */
    private fun createEventLogRecord(
        id: EventLogEntryId,
        title: String,
        description: String = "",
        unit: String = "",
        eventType: EventLogEventType = EventLogEventType.INCIDENT,
        fallbackEventType: String? = null,
        fallbackEmployeeName: String? = null,
        propertyId: PropertyId = PropertyId("test-property"),
        timeRecorded: Long = 1734278400L, // Dec 15, 2025
    ): EventLogRecordModel {
        return EventLogRecordModel(
            id = id,
            entityId = id.eventLogEntryId,
            employeePk = null,
            propertyId = propertyId,
            timeRecorded = timeRecorded,
            unit = unit,
            eventType = eventType,
            fallbackEmployeeName = fallbackEmployeeName,
            fallbackEventType = fallbackEventType,
            title = title,
            description = description,
            attachments = emptyList(),
        )
    }
}

