package com.vaultia.app.core.crypto.encryption

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class PlaintextPayloadTest {
    @Test
    fun rejectsEmptyPlaintext() {
        try {
            PlaintextPayload(ByteArray(0))
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }

    @Test
    fun protectsPlaintextWithDefensiveCopy() {
        val source = byteArrayOf(1, 2, 3, 4)
        val payload = PlaintextPayload(source)

        source[0] = 99
        val copy = payload.plaintextCopy()
        copy[1] = 88

        assertArrayEquals(byteArrayOf(1, 2, 3, 4), payload.plaintextCopy())
        assertEquals(4, payload.plaintextLengthBytes)
    }
}
