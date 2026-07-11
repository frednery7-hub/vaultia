package com.vaultia.app.core.model.payload

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultItemPayloadSerializerTest {

    @Test
    fun `serializes and deserializes PasswordPayload with special characters correctly`() {
        val original = PasswordPayload(
            title = "My Bank",
            username = "user@test.com",
            passwordValue = "s3cr3t!@#\nwithnewline",
            url = "https://example.com/?q=test=1",
            additionalNotes = "First line\nSecond line\nThird line="
        )

        val encodedBytes = VaultItemPayloadSerializer.encode(original)
        val result = VaultItemPayloadSerializer.decode(encodedBytes)

        assertTrue(result is VaultItemPayloadSerializationResult.Success)
        val decoded = (result as VaultItemPayloadSerializationResult.Success).payload
        
        assertTrue(decoded is PasswordPayload)
        val decodedPassword = decoded as PasswordPayload
        
        assertEquals(original.title, decodedPassword.title)
        assertEquals(original.username, decodedPassword.username)
        assertEquals(original.passwordValue, decodedPassword.passwordValue)
        assertEquals(original.url, decodedPassword.url)
        assertEquals(original.additionalNotes, decodedPassword.additionalNotes)
        assertEquals(original.version, decodedPassword.version)
    }

    @Test
    fun `serializes and deserializes NotePayload with massive text`() {
        val original = NotePayload(title = "Massive Note", text = "Hello\n".repeat(1000))

        val encodedBytes = VaultItemPayloadSerializer.encode(original)
        val result = VaultItemPayloadSerializer.decode(encodedBytes)

        assertTrue(result is VaultItemPayloadSerializationResult.Success)
        val decoded = (result as VaultItemPayloadSerializationResult.Success).payload
        
        assertTrue(decoded is NotePayload)
        val decodedNote = decoded as NotePayload
        
        assertEquals(original.title, decodedNote.title)
        assertEquals(original.text, decodedNote.text)
    }
}
