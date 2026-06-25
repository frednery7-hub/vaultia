package com.vaultia.app.core.session.contract

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionManagerArchitectureTest {

    @Test
    fun sessionManager_hasNoMethodsInFoundationPhase6() {
        assertEquals(
            "SessionManager must remain contract-only with no methods in Foundation Phase 6.",
            0,
            SessionManager::class.java.declaredMethods.size,
        )
    }
}
