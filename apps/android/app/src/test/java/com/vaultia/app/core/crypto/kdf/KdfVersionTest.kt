package com.vaultia.app.core.crypto.kdf

import org.junit.Assert.assertEquals
import org.junit.Test

class KdfVersionTest {
    @Test
    fun exposesArgon2Version13() {
        assertEquals(0x13, KdfVersion.ARGON2_VERSION_13.encodedValue)
    }
}
