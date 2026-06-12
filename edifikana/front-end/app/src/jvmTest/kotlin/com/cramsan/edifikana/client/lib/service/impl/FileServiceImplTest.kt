package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.service.FileService
import com.cramsan.edifikana.client.lib.service.FileServiceImpl
import com.cramsan.edifikana.client.lib.utils.IODependencies
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for the FileManagerImpl class.
 *
 * Tests the JVM implementation which delegates to platform-specific utility functions.
 * These are integration tests that verify FileManagerImpl correctly wraps the utilities.
 */
class FileServiceImplTest : CoroutineTest() {
    private lateinit var ioDependencies: IODependencies
    private lateinit var fileService: FileService
    private lateinit var tempFile: File

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        ioDependencies = IODependencies()
        fileService = FileServiceImpl(ioDependencies)

        // Create a temporary test file
        tempFile = File.createTempFile("test_image", ".jpg")
        tempFile.writeBytes(byteArrayOf(1, 2, 3, 4, 5))
    }

    /**
     * Tests that readFileBytes successfully reads a valid file.
     */
    @Test
    fun `readFileBytes returns success for valid file`() = runCoroutineTest {
        // Arrange
        val uri = CoreUri(tempFile.absolutePath)
        val expectedBytes = byteArrayOf(1, 2, 3, 4, 5)

        // Act
        val result = fileService.readFileBytes(uri)

        // Assert
        assertTrue(result.isSuccess)
        val actualBytes = result.getOrNull()!!
        assertEquals(expectedBytes.size, actualBytes.size)
        assertTrue(expectedBytes.contentEquals(actualBytes))
    }

    /**
     * Tests that readFileBytes returns failure for non-existent file.
     */
    @Test
    fun `readFileBytes returns failure for non-existent file`() = runCoroutineTest {
        // Arrange
        val uri = CoreUri("/non/existent/file.jpg")

        // Act
        val result = fileService.readFileBytes(uri)

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.isFailure)
    }

    /**
     * Tests that readFileBytes handles file:// URI format.
     */
    @Test
    fun `readFileBytes handles file URI format`() = runCoroutineTest {
        // Arrange
        // Use File.toURI() to get proper cross-platform file:// URI (file:///C:/... on Windows)
        val uri = CoreUri(tempFile.toURI().toString())
        val expectedBytes = byteArrayOf(1, 2, 3, 4, 5)

        // Act
        val result = fileService.readFileBytes(uri)

        // Assert
        assertTrue(result.isSuccess)
        val actualBytes = result.getOrNull()!!
        assertEquals(expectedBytes.size, actualBytes.size)
        assertTrue(expectedBytes.contentEquals(actualBytes))
    }

    /**
     * Tests that processImage returns the raw data on JVM.
     * Note: JVM implementation currently doesn't process images (no EXIF rotation or compression).
     */
    @Test
    fun `processImage returns raw data on JVM`() = runCoroutineTest {
        // Arrange
        val inputData = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

        // Act
        val result = fileService.processImage(inputData)

        // Assert
        assertTrue(result.isSuccess)
        val outputData = result.getOrNull()!!
        assertEquals(inputData.size, outputData.size)
        assertTrue(inputData.contentEquals(outputData))
    }

    /**
     * Tests that processImage handles empty data.
     */
    @Test
    fun `processImage handles empty data`() = runCoroutineTest {
        // Arrange
        val inputData = byteArrayOf()

        // Act
        val result = fileService.processImage(inputData)

        // Assert
        assertTrue(result.isSuccess)
        val outputData = result.getOrNull()!!
        assertEquals(0, outputData.size)
    }

    /**
     * Tests that getFilename extracts the filename from a path.
     */
    @Test
    fun `getFilename extracts filename from path`() {
        // Arrange
        val uri = CoreUri(tempFile.absolutePath)

        // Act
        val filename = fileService.getFilename(uri)

        // Assert
        assertTrue(filename.startsWith("test_image"))
        assertTrue(filename.endsWith(".jpg"))
    }

    /**
     * Tests that getFilename handles file:// URI format.
     */
    @Test
    fun `getFilename handles file URI format`() {
        // Arrange
        // Use File.toURI() to get proper cross-platform file:// URI (file:///C:/... on Windows)
        val uri = CoreUri(tempFile.toURI().toString())

        // Act
        val filename = fileService.getFilename(uri)

        // Assert
        assertTrue(filename.startsWith("test_image"))
        assertTrue(filename.endsWith(".jpg"))
    }

    /**
     * Tests that getFilename extracts filename from simple path.
     */
    @Test
    fun `getFilename extracts filename from simple path`() {
        // Arrange
        val uri = CoreUri("/path/to/image.png")

        // Act
        val filename = fileService.getFilename(uri)

        // Assert
        assertEquals("image.png", filename)
    }
}
