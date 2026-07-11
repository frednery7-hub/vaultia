package com.vaultia.app.core.storage.local.payload

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FilePayloadRepositoryTest {
    private lateinit var testDirectory: File
    private lateinit var repository: FilePayloadRepository
    @Before fun setUp() {
        testDirectory = File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "test_vaultia_payloads")
        if (testDirectory.exists()) testDirectory.deleteRecursively()
        testDirectory.mkdirs()
        repository = FilePayloadRepository(testDirectory)
    }
    @After fun tearDown() { if (testDirectory.exists()) testDirectory.deleteRecursively() }
    @Test fun readWriteWorks() {
        val p = EncryptedPayloadPointer.from("test_payload_123.enc")
        val d = byteArrayOf(10, 20, 30)
        repository.savePayload(p, d)
        assertArrayEquals(d, (repository.readPayload(p) as StorageRepositoryResult.Success).value)
    }
}
