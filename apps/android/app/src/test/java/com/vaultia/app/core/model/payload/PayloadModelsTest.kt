package com.vaultia.app.core.model.payload

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PayloadModelsTest {
    @Test
    fun `password payload accepts valid data`() {
        val payload = PasswordPayload(
            username = "user@example.com",
            passwordValue = "secret123",
            url = "https://example.com",
            additionalNotes = "Test note"
        )
        assertEquals("secret123", payload.passwordValue)
    }

    @Test
    fun `password payload rejects giant data`() {
        val giantPassword = "a".repeat(1025)
        assertThrows(IllegalArgumentException::class.java) {
            PasswordPayload(passwordValue = giantPassword)
        }
    }

    @Test
    fun `note payload accepts valid data`() {
        val payload = NotePayload(text = "Hello world")
        assertEquals("Hello world", payload.text)
    }

    @Test
    fun `note payload rejects giant text`() {
        val giantText = "a".repeat(200_001)
        assertThrows(IllegalArgumentException::class.java) {
            NotePayload(text = giantText)
        }
    }
}
