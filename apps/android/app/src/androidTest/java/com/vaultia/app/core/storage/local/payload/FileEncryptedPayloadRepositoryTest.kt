package com.vaultia.app.core.storage.local.payload

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.repository.StorageRepositoryError
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FileEncryptedPayloadRepositoryTest {

    private lateinit var testDirectory: File
    private lateinit var repository: FileEncryptedPayloadRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        testDirectory = File(context.filesDir, "test_vaultia_payloads")
        if (testDirectory.exists()) {
            testDirectory.deleteRecursively()
        }
        testDirectory.mkdirs()
        repository = FileEncryptedPayloadRepository(testDirectory)
    }

    @After
    fun tearDown() {
        if (testDirectory.exists()) {
            testDirectory.deleteRecursively()
        }
    }

    @Test
    fun savePayloadAndReadPayloadSuccessfullyFromDisk() {
        val pointer = EncryptedPayloadPointer.from("test_payload_123.enc")
        val data = byteArrayOf(10, 20, 30, 40, 50)

        val saveResult = repository.savePayload(pointer, data)
        assertTrue(saveResult is StorageRepositoryResult.Success)

        val physicalFile = File(testDirectory, "test_payload_123.enc")
        assertTrue(physicalFile.exists())
        assertArrayEquals(data, physicalFile.readBytes())

        val readResult = repository.readPayload(pointer)
        assertTrue(readResult is StorageRepositoryResult.Success)
        assertArrayEquals(data, (readResult as StorageRepositoryResult.Success).value)
    }

    @Test
    fun readPayloadReturnsRecordNotFoundWhenFileIsMissing() {
        val pointer = EncryptedPayloadPointer.from("missing_payload.enc")
        
        val readResult = repository.readPayload(pointer)
        
        assertTrue(readResult is StorageRepositoryResult.Failure)
        assertEquals(StorageRepositoryError.RecordNotFound, (readResult as StorageRepositoryResult.Failure).error)
    }

    @Test
    fun deletePayloadRemovesFileFromDiskSuccessfully() {
        val pointer = EncryptedPayloadPointer.from("test_payload_123.enc")
        val data = byteArrayOf(9, 9, 9)
        repository.savePayload(pointer, data)

        val physicalFile = File(testDirectory, "test_payload_123.enc")
        assertTrue(physicalFile.exists())

        val deleteResult = repository.deletePayload(pointer)
        assertTrue(deleteResult is StorageRepositoryResult.Success)
        assertEquals(true, (deleteResult as StorageRepositoryResult.Success).value)

        assertTrue(!physicalFile.exists())
    }
    
    @Test
    fun deletePayloadReturnsFalseWhenMissing() {
        val pointer = EncryptedPayloadPointer.from("missing_payload.enc")
        val deleteResult = repository.deletePayload(pointer)
        
        assertTrue(deleteResult is StorageRepositoryResult.Success)
        assertEquals(false, (deleteResult as StorageRepositoryResult.Success).value)
    }
}
