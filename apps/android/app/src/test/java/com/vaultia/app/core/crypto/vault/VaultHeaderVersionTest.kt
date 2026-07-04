package com.vaultia.app.core.crypto.vault

import org.junit.Assert.assertEquals
import org.junit.Test

class VaultHeaderVersionTest {
    @Test
    fun exposesVersionOneZero() {
        assertEquals(0x00010000, VaultHeaderVersion.V1_0.encodedValue)
        assertEquals(1, VaultHeaderVersion.V1_0.major)
        assertEquals(0, VaultHeaderVersion.V1_0.minor)
    }
}
