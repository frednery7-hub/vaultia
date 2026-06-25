package com.vaultia.app.core.model.vault

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class VaultItemArchitectureTest {

    @Test
    fun vaultItem_containsOnlyMetadataFields() {
        val fieldNames = VaultItem::class.java.declaredFields
            .map { it.name }
            .filterNot { it.startsWith("$") }
            .sorted()

        assertEquals(
            listOf(
                "createdAtEpochMillis",
                "id",
                "title",
                "type",
                "updatedAtEpochMillis",
            ),
            fieldNames,
        )
    }

    @Test
    fun vaultItem_doesNotContainSecretPayloadFields() {
        val forbiddenTerms = listOf(
            "password",
            "secret",
            "payload",
            "plaintext",
            "ciphertext",
            "noteBody",
            "documentBytes",
            "photoBytes",
            "key",
            "salt",
            "nonce",
            "tag",
        )

        val fieldNames = VaultItem::class.java.declaredFields
            .map { it.name.lowercase() }

        forbiddenTerms.forEach { forbidden ->
            assertFalse(
                "Forbidden field detected in VaultItem: $forbidden",
                fieldNames.any { it.contains(forbidden.lowercase()) },
            )
        }
    }

    @Test
    fun vaultItemType_containsOnlyApprovedV1Categories() {
        assertEquals(
            listOf(
                VaultItemType.PASSWORD,
                VaultItemType.SECURE_NOTE,
                VaultItemType.PRIVATE_PHOTO,
                VaultItemType.PRIVATE_DOCUMENT,
            ),
            VaultItemType.entries,
        )
    }

    @Test
    fun vaultSessionState_containsOnlyLocalStates() {
        assertEquals(
            listOf(
                VaultSessionState.NOT_INITIALIZED,
                VaultSessionState.LOCKED,
                VaultSessionState.UNLOCKED,
            ),
            VaultSessionState.entries,
        )
    }
}
