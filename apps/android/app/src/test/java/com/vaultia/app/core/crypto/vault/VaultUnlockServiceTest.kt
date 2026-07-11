package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.Argon2idKdfDeriver
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultUnlockServiceTest {
    @Test
    fun `unlock fails with empty header`() {
        val service = VaultUnlockService(Argon2idKdfDeriver())
        val result = service.unlock(VaultUnlockRequest("password".toCharArray(), ""))
        assertTrue(result is VaultUnlockResult.Failure)
    }
}
