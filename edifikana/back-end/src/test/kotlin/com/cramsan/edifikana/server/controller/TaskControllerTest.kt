package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.task.TaskPriority
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.TaskService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.Task
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTime::class)
class TaskControllerTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // createTask
    // -------------------------------------------------------------------------

    @Test
    fun `test createTask succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_task_request.json")
        val expectedResponse = readFileContent("requests/create_task_response.json")
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val callerUserId = UserId("user123")
        coEvery {
            taskService.createTask(
                propertyId = PropertyId("property123"),
                unitId = UnitId("unit123"),
                commonAreaId = null,
                assigneeId = null,
                createdBy = callerUserId,
                title = "Fix leaky faucet",
                description = "Dripping in unit 4B",
                priority = TaskPriority.HIGH,
                dueDate = null,
            )
        }.answers {
            task(TaskId("task123"), PropertyId("property123"), UnitId("unit123"), callerUserId)
        }
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerUserId)
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.MANAGER) } returns true

        // Act
        val response = client.post("task") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test createTask fails when user lacks MANAGER role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_task_request.json")
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, PropertyId("property123"), UserRole.MANAGER) } returns false

        // Act
        val response = client.post("task") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { taskService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // -------------------------------------------------------------------------
    // getTask
    // -------------------------------------------------------------------------

    @Test
    fun `test getTask returns 200 when found and user has EMPLOYEE role`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        val callerUserId = UserId("user123")
        coEvery { taskService.getTask(taskId) } returns task(taskId, PropertyId("property123"), UnitId("unit123"), callerUserId)
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerUserId)
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.EMPLOYEE) } returns true

        // Act
        val response = client.get("task/task123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test getTask returns 404 when task is not found`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        coEvery { taskService.getTask(taskId) } returns null
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.EMPLOYEE) } returns true

        // Act
        val response = client.get("task/task123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test getTask returns 404 when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.EMPLOYEE) } returns false

        // Act
        val response = client.get("task/task123")

        // Assert
        coVerify { taskService wasNot Called }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // -------------------------------------------------------------------------
    // getTasks
    // -------------------------------------------------------------------------

    @Test
    fun `test getTasks returns 200 when user has EMPLOYEE role`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val propertyId = PropertyId("property123")
        val callerUserId = UserId("user123")
        coEvery {
            taskService.getTasks(
                propertyId = propertyId,
                unitId = null,
                status = null,
                assigneeId = null,
                priority = null,
            )
        } returns listOf(task(TaskId("task123"), propertyId, UnitId("unit123"), callerUserId))
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerUserId)
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, propertyId, UserRole.EMPLOYEE) } returns true

        // Act
        val response = client.get("task/list?property_id=property123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test getTasks fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val propertyId = PropertyId("property123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, propertyId, UserRole.EMPLOYEE) } returns false

        // Act
        val response = client.get("task/list?property_id=property123")

        // Assert
        coVerify { taskService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // -------------------------------------------------------------------------
    // updateTask
    // -------------------------------------------------------------------------

    @Test
    fun `test updateTask succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_task_request.json")
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        val callerUserId = UserId("user123")
        coEvery {
            taskService.updateTask(
                taskId = taskId,
                title = "Fix leaky faucet (urgent)",
                description = null,
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                assigneeId = null,
                dueDate = null,
                callerUserId = callerUserId,
            )
        } returns task(
            TaskId("task123"), PropertyId("property123"), UnitId("unit123"), callerUserId,
            title = "Fix leaky faucet (urgent)",
            status = TaskStatus.IN_PROGRESS,
            statusChangedBy = callerUserId,
            statusChangedAt = Instant.fromEpochSeconds(1),
        )
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = callerUserId)
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.MANAGER) } returns true

        // Act
        val response = client.put("task/task123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test updateTask fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_task_request.json")
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.MANAGER) } returns false

        // Act
        val response = client.put("task/task123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { taskService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // -------------------------------------------------------------------------
    // deleteTask
    // -------------------------------------------------------------------------

    @Test
    fun `test deleteTask succeeds when user has MANAGER role`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        coEvery { taskService.deleteTask(taskId) } returns true
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.MANAGER) } returns true

        // Act
        val response = client.delete("task/task123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test deleteTask fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val taskService = get<TaskService>()
        val rbacService = get<RBACService>()
        val taskId = TaskId("task123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, taskId, UserRole.MANAGER) } returns false

        // Act
        val response = client.delete("task/task123")

        // Assert
        coVerify { taskService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun task(
        id: TaskId,
        propertyId: PropertyId,
        unitId: UnitId?,
        createdBy: UserId,
        title: String = "Fix leaky faucet",
        description: String? = "Dripping in unit 4B",
        priority: TaskPriority = TaskPriority.HIGH,
        status: TaskStatus = TaskStatus.OPEN,
        statusChangedBy: UserId? = null,
        statusChangedAt: Instant? = null,
    ) = Task(
        id = id,
        propertyId = propertyId,
        unitId = unitId,
        commonAreaId = null,
        assigneeId = null,
        createdBy = createdBy,
        statusChangedBy = statusChangedBy,
        title = title,
        description = description,
        priority = priority,
        status = status,
        dueDate = null,
        createdAt = Instant.fromEpochSeconds(0),
        completedAt = null,
        statusChangedAt = statusChangedAt,
    )
}
