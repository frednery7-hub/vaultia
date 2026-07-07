package com.vaultia.app.core.storage.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class StorageRecordIdTest {
    @Test
    fun acceptsTechnicalRecordId() {
        val id = StorageRecordId.from("record_123456")

        assertEquals("record_123456", id.value)
    }

    @Test
    fun rejectsShortRecordId() {
        assertFalse(StorageRecordId.isValid("short"))

        assertThrows(IllegalArgumentException::class.java) {
            StorageRecordId.from("short")
        }
    }

    @Test
    fun rejectsPathLikeRecordId() {
        val invalidValues = listOf(
            "../secret",
            "folder/secret",
            "folder\\secret",
            "file:name",
            "~/secret",
            "record with spaces",
        )

        invalidValues.forEach { value ->
            assertFalse(value, StorageRecordId.isValid(value))
        }
    }

    @Test
    fun acceptsOnlyTechnicalCharacters() {
        assertTrue(StorageRecordId.isValid("REC_1234-abcd"))
        assertFalse(StorageRecordId.isValid("REC_1234.abcd"))
        assertFalse(StorageRecordId.isValid("REC_1234@abcd"))
    }
}
