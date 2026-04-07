package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TaskId
import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTime::class)
class SupabaseTaskDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${testPrefix}@test.com")
            orgId = createTestOrganization("org_$testPrefix", "")
            propertyId = createTestProperty("${testPrefix}_Property", testUserId!!, orgId!!)
        }
    }

    @Test
    fun `createTask should return created task with correct fields`() = runCoroutineTest {
        // Arrange
        val title = "${testPrefix}_Fix leaky faucet"

        // Act
        val result = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = title,
            description = "Dripping in unit 4B",
            priority = TaskPriority.HIGH,
            dueDate = null,
        ).registerTaskForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val task = result.getOrNull()
        assertNotNull(task)
        assertEquals(title, task.title)
        assertEquals("Dripping in unit 4B", task.description)
        assertEquals(TaskPriority.HIGH, task.priority)
        assertEquals(TaskStatus.OPEN, task.status)
        assertEquals(propertyId, task.propertyId)
        assertEquals(testUserId, task.createdBy)
        assertNull(task.completedAt)
        assertNull(task.statusChangedAt)
    }

    @Test
    fun `getTask should return created task`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_GetTask",
            description = null,
            priority = TaskPriority.MEDIUM,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)
        val created = createResult.getOrNull()!!

        // Act
        val getResult = taskDatastore.getTask(created.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals(created.id, fetched.id)
        assertEquals("${testPrefix}_GetTask", fetched.title)
    }

    @Test
    fun `getTask should return null when not found`() = runCoroutineTest {
        // Arrange
        val fakeId = TaskId(UUID.random())

        // Act
        val result = taskDatastore.getTask(fakeId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `getTask should return null when soft deleted`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_SoftDeletedTask",
            description = null,
            priority = TaskPriority.LOW,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)
        val task = createResult.getOrNull()!!
        taskDatastore.deleteTask(task.id)

        // Act
        val getResult = taskDatastore.getTask(task.id)

        // Assert
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `getTasks should return all tasks for property`() = runCoroutineTest {
        // Arrange & Act
        val result1 = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_Task1",
            description = null,
            priority = TaskPriority.LOW,
            dueDate = null,
        ).registerTaskForDeletion()
        val result2 = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_Task2",
            description = null,
            priority = TaskPriority.HIGH,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val listResult = taskDatastore.getTasks(propertyId = propertyId!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val tasks = listResult.getOrNull()
        assertNotNull(tasks)
        val titles = tasks.map { it.title }
        assertTrue(titles.contains("${testPrefix}_Task1"))
        assertTrue(titles.contains("${testPrefix}_Task2"))
    }

    @Test
    fun `getTasks should filter by status`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_OpenTask",
            description = null,
            priority = TaskPriority.MEDIUM,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)

        // Act
        val openResult = taskDatastore.getTasks(propertyId = propertyId!!, status = TaskStatus.OPEN)
        val completedResult = taskDatastore.getTasks(propertyId = propertyId!!, status = TaskStatus.COMPLETED)

        // Assert
        assertTrue(openResult.isSuccess)
        val openTitles = openResult.getOrNull()!!.map { it.title }
        assertTrue(openTitles.contains("${testPrefix}_OpenTask"))

        assertTrue(completedResult.isSuccess)
        val completedTitles = completedResult.getOrNull()!!.map { it.title }
        assertTrue(!completedTitles.contains("${testPrefix}_OpenTask"))
    }

    @Test
    fun `getTasks should filter by priority`() = runCoroutineTest {
        // Arrange
        val result = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_HighPriorityTask",
            description = null,
            priority = TaskPriority.HIGH,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(result.isSuccess)

        // Act
        val highResult = taskDatastore.getTasks(propertyId = propertyId!!, priority = TaskPriority.HIGH)
        val lowResult = taskDatastore.getTasks(propertyId = propertyId!!, priority = TaskPriority.LOW)

        // Assert
        assertTrue(highResult.isSuccess)
        val highTitles = highResult.getOrNull()!!.map { it.title }
        assertTrue(highTitles.contains("${testPrefix}_HighPriorityTask"))

        assertTrue(lowResult.isSuccess)
        val lowTitles = lowResult.getOrNull()!!.map { it.title }
        assertTrue(!lowTitles.contains("${testPrefix}_HighPriorityTask"))
    }

    @Test
    fun `getTasks should not return deleted tasks`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_DeletedTask",
            description = null,
            priority = TaskPriority.MEDIUM,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)
        val task = createResult.getOrNull()!!
        taskDatastore.deleteTask(task.id)

        // Act
        val listResult = taskDatastore.getTasks(propertyId = propertyId!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val titles = listResult.getOrNull()!!.map { it.title }
        assertTrue(!titles.contains("${testPrefix}_DeletedTask"))
    }

    @Test
    fun `updateTask should update title and priority`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_OldTitle",
            description = "Old description",
            priority = TaskPriority.LOW,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)
        val task = createResult.getOrNull()!!

        // Act
        val updateResult = taskDatastore.updateTask(
            taskId = task.id,
            title = "${testPrefix}_NewTitle",
            description = null,
            priority = TaskPriority.HIGH,
            status = null,
            assigneeId = null,
            dueDate = null,
            statusChangedBy = null,
            completedAt = null,
            statusChangedAt = null,
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals("${testPrefix}_NewTitle", updated.title)
        assertEquals(TaskPriority.HIGH, updated.priority)
    }

    @Test
    fun `updateTask should update status and set audit fields`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_StatusTask",
            description = null,
            priority = TaskPriority.MEDIUM,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)
        val task = createResult.getOrNull()!!

        // Act
        val updateResult = taskDatastore.updateTask(
            taskId = task.id,
            title = null,
            description = null,
            priority = null,
            status = TaskStatus.COMPLETED,
            assigneeId = null,
            dueDate = null,
            statusChangedBy = testUserId,
            completedAt = Clock.System.now(),
            statusChangedAt = Clock.System.now(),
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals(TaskStatus.COMPLETED, updated.status)
        assertNotNull(updated.completedAt)
        assertNotNull(updated.statusChangedAt)
        assertEquals(testUserId, updated.statusChangedBy)
    }

    @Test
    fun `deleteTask should soft delete and make task invisible`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_ToDelete",
            description = null,
            priority = TaskPriority.LOW,
            dueDate = null,
        ).registerTaskForDeletion()
        assertTrue(createResult.isSuccess)
        val task = createResult.getOrNull()!!

        // Act
        val deleteResult = taskDatastore.deleteTask(task.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = taskDatastore.getTask(task.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `purgeTask should hard delete the task`() = runCoroutineTest {
        // Arrange
        val createResult = taskDatastore.createTask(
            propertyId = propertyId!!,
            unitId = null,
            commonAreaId = null,
            assigneeId = null,
            createdBy = testUserId!!,
            title = "${testPrefix}_ToPurge",
            description = null,
            priority = TaskPriority.LOW,
            dueDate = null,
        )
        assertTrue(createResult.isSuccess)
        val task = createResult.getOrNull()!!

        // Act
        val purgeResult = taskDatastore.purgeTask(task.id)

        // Assert
        assertTrue(purgeResult.isSuccess)
        val getResult = taskDatastore.getTask(task.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }
}
