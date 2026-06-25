package com.vaultia.app.core.security.contract

import org.junit.Assert.assertEquals
import org.junit.Test

class CryptoServiceArchitectureTest {

    @Test
    fun cryptoService_hasNoMethodsInFoundationPhase6() {
        assertEquals(
            "CryptoService must remain contract-only with no methods in Foundation Phase 6.",
            0,
            CryptoService::class.java.declaredMethods.size,
        )
    }
}
