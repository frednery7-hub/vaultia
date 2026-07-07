package com.vaultia.app.core.storage.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class EncryptedPayloadPointerTest {
    @Test
    fun acceptsTechnicalEncryptedPayloadPointer() {
        val pointer = EncryptedPayloadPointer.from("payload_123456.enc")

        assertEquals("payload_123456.enc", pointer.value)
    }

    @Test
    fun requiresEncryptedSuffix() {
        assertFalse(EncryptedPayloadPointer.isValid("payload_123456"))
        assertTrue(EncryptedPayloadPointer.isValid("payload_123456.enc"))
    }

    @Test
    fun rejectsExternalOrPathLikePointers() {
        val invalidValues = listOf(
            "../payload.enc",
            "folder/payload.enc",
            "folder\\payload.enc",
            "file:name.enc",
            "~/payload.enc",
            "payload with spaces.enc",
        )

        invalidValues.forEach { value ->
            assertFalse(value, EncryptedPayloadPointer.isValid(value))
        }
    }

    @Test
    fun throwsForInvalidPointer() {
        assertThrows(IllegalArgumentException::class.java) {
            EncryptedPayloadPointer.from("folder/payload.enc")
        }
    }
}
