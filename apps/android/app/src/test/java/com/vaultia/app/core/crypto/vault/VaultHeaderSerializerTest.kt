package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfAlgorithm
import com.vaultia.app.core.crypto.kdf.KdfProfileCatalog
import com.vaultia.app.core.crypto.kdf.KdfVersion
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultHeaderSerializerTest {
    @Test
    fun encodeValidHeaderContainsMagicFooterAndAllowedFields() {
        val encoded = VaultHeaderSerializer.encode(validHeader())

        assertTrue(encoded.startsWith("VAULTIA_HEADER_V1\n"))
        assertTrue(encoded.endsWith("END_VAULTIA_HEADER"))
        assertTrue(encoded.contains("formatVersion=65536"))
        assertTrue(encoded.contains("kdfAlgorithm=ARGON2ID"))
        assertTrue(encoded.contains("kdfVersion=19"))
        assertTrue(encoded.contains("saltHex=000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f"))
        assertTrue(encoded.contains("memoryCostKiB=65536"))
        assertTrue(encoded.contains("iterations=2"))
        assertTrue(encoded.contains("parallelism=1"))
        assertTrue(encoded.contains("outputLengthBytes=32"))
        assertTrue(encoded.contains("createdAtEpochMillis=1000"))
    }

    @Test
    fun encodeIsDeterministicForSameHeader() {
        val header = validHeader()

        assertEquals(
            VaultHeaderSerializer.encode(header),
            VaultHeaderSerializer.encode(header),
        )
    }

    @Test
    fun roundTripPreservesPublicHeaderFields() {
        val original = validHeader()
        val outcome: VaultHeaderSerializationResult = VaultHeaderSerializer.decode(
            VaultHeaderSerializer.encode(original),
        )

        assertTrue(outcome is VaultHeaderSerializationResult.Success)
        val decoded = (outcome as VaultHeaderSerializationResult.Success).header

        assertEquals(original.formatVersion, decoded.formatVersion)
        assertEquals(original.kdfAlgorithm, decoded.kdfAlgorithm)
        assertEquals(original.kdfVersion, decoded.kdfVersion)
        assertArrayEquals(original.saltCopy(), decoded.saltCopy())
        assertEquals(original.memoryCostKiB, decoded.memoryCostKiB)
        assertEquals(original.iterations, decoded.iterations)
        assertEquals(original.parallelism, decoded.parallelism)
        assertEquals(original.outputLengthBytes, decoded.outputLengthBytes)
        assertEquals(original.createdAtEpochMillis, decoded.createdAtEpochMillis)
    }

    @Test
    fun encodedHeaderDoesNotContainSecretFields() {
        val encoded = VaultHeaderSerializer.encode(validHeader())
        val forbiddenTerms = listOf(
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
            "profileName",
            "profileId",
            "profile",
        )

        forbiddenTerms.forEach { forbiddenTerm ->
            assertFalse("Forbidden term found: $forbiddenTerm", encoded.contains(forbiddenTerm))
        }
    }

    @Test
    fun decodeEmptyInputReturnsEmptyInput() {
        assertFailure("", VaultHeaderSerializationError.EmptyInput)
        assertFailure("   ", VaultHeaderSerializationError.EmptyInput)
    }

    @Test
    fun decodeInvalidMagicReturnsInvalidMagic() {
        assertFailure(
            VaultHeaderSerializer.encode(validHeader()).replace("VAULTIA_HEADER_V1", "WRONG_MAGIC"),
            VaultHeaderSerializationError.InvalidMagic,
        )
    }

    @Test
    fun decodeMissingFieldReturnsMissingField() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .lines()
            .filterNot { line -> line.startsWith("iterations=") }
            .joinToString("\n")

        assertFailure(corrupted, VaultHeaderSerializationError.MissingField)
    }

    @Test
    fun decodeDuplicateFieldReturnsDuplicateField() {
        val encoded = VaultHeaderSerializer.encode(validHeader())
        val corrupted = encoded.replace(
            "END_VAULTIA_HEADER",
            "iterations=2\nEND_VAULTIA_HEADER",
        )

        assertFailure(corrupted, VaultHeaderSerializationError.DuplicateField)
    }

    @Test
    fun decodeInvalidNumericValueReturnsInvalidFieldValue() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .replace("iterations=2", "iterations=abc")

        assertFailure(corrupted, VaultHeaderSerializationError.InvalidFieldValue)
    }

    @Test
    fun decodeFutureFormatVersionReturnsUnsupportedFormatVersion() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .replace("formatVersion=65536", "formatVersion=131072")

        assertFailure(corrupted, VaultHeaderSerializationError.UnsupportedFormatVersion)
    }

    @Test
    fun decodeUnknownKdfAlgorithmReturnsUnsupportedKdfAlgorithm() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .replace("kdfAlgorithm=ARGON2ID", "kdfAlgorithm=UNKNOWN")

        assertFailure(corrupted, VaultHeaderSerializationError.UnsupportedKdfAlgorithm)
    }

    @Test
    fun decodeUnknownKdfVersionReturnsUnsupportedKdfVersion() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .replace("kdfVersion=19", "kdfVersion=20")

        assertFailure(corrupted, VaultHeaderSerializationError.UnsupportedKdfVersion)
    }

    @Test
    fun decodeInvalidSaltHexReturnsInvalidSaltEncoding() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .replace(
                "saltHex=000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f",
                "saltHex=not-hex",
            )

        assertFailure(corrupted, VaultHeaderSerializationError.InvalidSaltEncoding)
    }

    @Test
    fun decodeShortSaltReturnsInvalidHeader() {
        val corrupted = VaultHeaderSerializer.encode(validHeader())
            .replace(
                "saltHex=000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f",
                "saltHex=000102",
            )

        assertFailure(corrupted, VaultHeaderSerializationError.InvalidHeader)
    }

    private fun validHeader(): VaultHeader = VaultHeader.fromProfile(
        profile = KdfProfileCatalog.CONSERVATIVE,
        salt = ByteArray(32) { index -> index.toByte() },
        createdAtEpochMillis = 1000L,
    )

    private fun assertFailure(
        input: String,
        expectedError: VaultHeaderSerializationError,
    ) {
        val outcome: VaultHeaderSerializationResult = VaultHeaderSerializer.decode(input)

        assertTrue(outcome is VaultHeaderSerializationResult.Failure)
        val failure = outcome as VaultHeaderSerializationResult.Failure
        assertEquals(expectedError, failure.error)
    }
}
