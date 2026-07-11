package com.vaultia.app.core.storage.payload

import com.vaultia.app.core.storage.model.EncryptedPayloadPointer
import com.vaultia.app.core.storage.repository.StorageRepositoryResult
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test

class InMemoryPayloadRepositoryTest {
    private lateinit var repository: InMemoryPayloadRepository
    @Before fun setUp() { repository = InMemoryPayloadRepository() }
    @Test fun readWriteWorks() {
        val p = EncryptedPayloadPointer.from("test_payload_123.enc")
        val d = byteArrayOf(1, 2, 3)
        repository.savePayload(p, d)
        assertArrayEquals(d, (repository.readPayload(p) as StorageRepositoryResult.Success).value)
    }
}
