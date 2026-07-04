package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfAlgorithm
import com.vaultia.app.core.crypto.kdf.KdfProfileCatalog
import com.vaultia.app.core.crypto.kdf.KdfVersion
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test

class VaultHeaderTest {
    @Test
    fun createsValidHeaderFromProfileAndSalt() {
        val header = VaultHeader.fromProfile(
            profile = KdfProfileCatalog.CONSERVATIVE,
            salt = validSalt(),
            createdAtEpochMillis = 1000L,
        )

        assertEquals(VaultHeaderVersion.V1_0, header.formatVersion)
        assertEquals(KdfAlgorithm.ARGON2ID, header.kdfAlgorithm)
        assertEquals(KdfVersion.ARGON2_VERSION_13, header.kdfVersion)
        assertEquals(64 * 1024, header.memoryCostKiB)
        assertEquals(2, header.iterations)
        assertEquals(1, header.parallelism)
        assertEquals(32, header.outputLengthBytes)
        assertEquals(1000L, header.createdAtEpochMillis)
        assertArrayEquals(validSalt(), header.saltCopy())
    }

    @Test
    fun headerStoresConcreteParametersNotProfileName() {
        val header = VaultHeader.fromProfile(
            profile = KdfProfileCatalog.FAST,
            salt = validSalt(),
            createdAtEpochMillis = 1000L,
        )

        val fieldNames = header::class.java.declaredFields.map { field -> field.name }

        assertEquals(32 * 1024, header.memoryCostKiB)
        assertEquals(2, header.iterations)
        assertEquals(1, header.parallelism)
        assertEquals(32, header.outputLengthBytes)
        assertFalse(fieldNames.contains("profileName"))
        assertFalse(fieldNames.contains("profileId"))
        assertFalse(fieldNames.contains("profile"))
    }

    @Test
    fun protectsSaltWithDefensiveCopy() {
        val salt = validSalt()
        val header = VaultHeader.fromProfile(
            profile = KdfProfileCatalog.FAST,
            salt = salt,
            createdAtEpochMillis = 1000L,
        )

        salt[0] = 99
        val firstCopy = header.saltCopy()
        firstCopy[1] = 88

        assertArrayEquals(validSalt(), header.saltCopy())
    }

    @Test
    fun convertsHeaderBackToKdfParameters() {
        val header = VaultHeader.fromProfile(
            profile = KdfProfileCatalog.CONSERVATIVE,
            salt = validSalt(),
            createdAtEpochMillis = 1000L,
        )

        val parameters = header.toKdfParameters()

        assertEquals(KdfAlgorithm.ARGON2ID, parameters.algorithm)
        assertEquals(32, parameters.outputLengthBytes)
        assertEquals(2, parameters.iterations)
    }

    @Test
    fun rejectsInvalidHeaderValues() {
        expectIllegalArgument { validHeader(salt = ByteArray(15)) }
        expectIllegalArgument { validHeader(memoryCostKiB = 0) }
        expectIllegalArgument { validHeader(iterations = 0) }
        expectIllegalArgument { validHeader(parallelism = 0) }
        expectIllegalArgument { validHeader(outputLengthBytes = 0) }
        expectIllegalArgument { validHeader(createdAtEpochMillis = 0L) }
    }

    @Test
    fun headerDoesNotExposeSecretFieldNames() {
        val forbiddenNames = listOf(
            "password",
            "passwordHash",
            "derivedKey",
            "vaultKey",
            "encryptedVaultKey",
            "ciphertext",
            "nonce",
            "tag",
            "payload",
            "filePath",
            "storagePath",
            "biometricKeyAlias",
        )
        val fieldNames = VaultHeader::class.java.declaredFields.map { field -> field.name }

        forbiddenNames.forEach { forbiddenName ->
            assertFalse("Forbidden field found: $forbiddenName", fieldNames.contains(forbiddenName))
        }
    }

    private fun validHeader(
        salt: ByteArray = validSalt(),
        memoryCostKiB: Int = 32 * 1024,
        iterations: Int = 2,
        parallelism: Int = 1,
        outputLengthBytes: Int = 32,
        createdAtEpochMillis: Long = 1000L,
    ): VaultHeader = VaultHeader(
        formatVersion = VaultHeaderVersion.V1_0,
        kdfAlgorithm = KdfAlgorithm.ARGON2ID,
        kdfVersion = KdfVersion.ARGON2_VERSION_13,
        salt = salt,
        memoryCostKiB = memoryCostKiB,
        iterations = iterations,
        parallelism = parallelism,
        outputLengthBytes = outputLengthBytes,
        createdAtEpochMillis = createdAtEpochMillis,
    )

    private fun validSalt(): ByteArray = ByteArray(32) { it.toByte() }

    private fun expectIllegalArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException.")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
