package com.vaultia.app.core.storage.contract

import org.junit.Assert.assertEquals
import org.junit.Test

class StorageContractsArchitectureTest {

    @Test
    fun secureStorage_hasNoMethodsInFoundationPhase6() {
        assertEquals(
            "SecureStorage must remain contract-only with no methods in Foundation Phase 6.",
            0,
            SecureStorage::class.java.declaredMethods.size,
        )
    }

    @Test
    fun vaultRepository_hasNoMethodsInFoundationPhase6() {
        assertEquals(
            "VaultRepository must remain contract-only with no methods in Foundation Phase 6.",
            0,
            VaultRepository::class.java.declaredMethods.size,
        )
    }
}
