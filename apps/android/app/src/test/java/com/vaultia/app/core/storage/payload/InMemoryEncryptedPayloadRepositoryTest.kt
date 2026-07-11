package com.vaultia.app.core.storage.payload

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryEncryptedPayloadRepositoryTest {
    private lateinit var repository: InMemoryEncryptedPayloadRepository
    @Before fun setUp() { repository = InMemoryEncryptedPayloadRepository() }
    @Test fun readWriteWorks() {
        val p = EncryptedPayloadPointer.from("test_payload_123.enc")
        val d = byteArrayOf(1, 2, 3)
        repository.savePayload(p, d)
        assertArrayEquals(d, (repository.readPayload(p) as StorageRepositoryResult.Success).value)
    }
}
