package com.vaultia.app.core.crypto.vault

import com.vaultia.app.core.crypto.kdf.KdfAlgorithm
import com.vaultia.app.core.crypto.kdf.KdfVersion

object VaultHeaderSerializer {
    private const val MAGIC: String = "VAULTIA_HEADER_V1"
    private const val FOOTER: String = "END_VAULTIA_HEADER"

    private val requiredFields = listOf(
        "formatVersion",
        "kdfAlgorithm",
        "kdfVersion",
        "saltHex",
        "memoryCostKiB",
        "iterations",
        "parallelism",
        "outputLengthBytes",
        "createdAtEpochMillis",
    )

    fun encode(header: VaultHeader): String {
        return buildString {
            appendLine(MAGIC)
            appendLine("formatVersion=${header.formatVersion.encodedValue}")
            appendLine("kdfAlgorithm=${header.kdfAlgorithm.name}")
            appendLine("kdfVersion=${header.kdfVersion.encodedValue}")
            appendLine("saltHex=${header.saltCopy().toHex()}")
            appendLine("memoryCostKiB=${header.memoryCostKiB}")
            appendLine("iterations=${header.iterations}")
            appendLine("parallelism=${header.parallelism}")
            appendLine("outputLengthBytes=${header.outputLengthBytes}")
            appendLine("createdAtEpochMillis=${header.createdAtEpochMillis}")
            append(FOOTER)
        }
    }

    fun decode(input: String): VaultHeaderSerializationResult {
        if (input.isBlank()) {
            return failure(VaultHeaderSerializationError.EmptyInput)
        }

        val lines = input.trimEnd().lines()
        if (lines.size < 3 || lines.first() != MAGIC || lines.last() != FOOTER) {
            return failure(VaultHeaderSerializationError.InvalidMagic)
        }

        val fields = mutableMapOf<String, String>()
        for (line in lines.drop(1).dropLast(1)) {
            val separatorIndex = line.indexOf("=")
            if (separatorIndex <= 0 || separatorIndex == line.lastIndex) {
                return failure(VaultHeaderSerializationError.InvalidFieldValue)
            }

            val key = line.substring(0, separatorIndex)
            val value = line.substring(separatorIndex + 1)

            if (fields.containsKey(key)) {
                return failure(VaultHeaderSerializationError.DuplicateField)
            }

            fields[key] = value
        }

        if (!fields.keys.containsAll(requiredFields)) {
            return failure(VaultHeaderSerializationError.MissingField)
        }

        val formatVersion = fields["formatVersion"]?.toIntOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidFieldValue)
        if (formatVersion != VaultHeaderVersion.V1_0.encodedValue) {
            return failure(VaultHeaderSerializationError.UnsupportedFormatVersion)
        }

        val kdfAlgorithm = parseKdfAlgorithm(fields["kdfAlgorithm"])
            ?: return failure(VaultHeaderSerializationError.UnsupportedKdfAlgorithm)

        val kdfVersion = parseKdfVersion(fields["kdfVersion"])
            ?: return failure(VaultHeaderSerializationError.UnsupportedKdfVersion)

        val salt = fields["saltHex"]?.hexToByteArrayOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidSaltEncoding)

        val memoryCostKiB = fields["memoryCostKiB"]?.toIntOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidFieldValue)
        val iterations = fields["iterations"]?.toIntOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidFieldValue)
        val parallelism = fields["parallelism"]?.toIntOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidFieldValue)
        val outputLengthBytes = fields["outputLengthBytes"]?.toIntOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidFieldValue)
        val createdAtEpochMillis = fields["createdAtEpochMillis"]?.toLongOrNull()
            ?: return failure(VaultHeaderSerializationError.InvalidFieldValue)

        return try {
            VaultHeaderSerializationResult.Success(
                VaultHeader(
                    formatVersion = VaultHeaderVersion.V1_0,
                    kdfAlgorithm = kdfAlgorithm,
                    kdfVersion = kdfVersion,
                    salt = salt,
                    memoryCostKiB = memoryCostKiB,
                    iterations = iterations,
                    parallelism = parallelism,
                    outputLengthBytes = outputLengthBytes,
                    createdAtEpochMillis = createdAtEpochMillis,
                ),
            )
        } catch (_: IllegalArgumentException) {
            failure(VaultHeaderSerializationError.InvalidHeader)
        }
    }

    private fun parseKdfAlgorithm(value: String?): KdfAlgorithm? {
        return when (value) {
            KdfAlgorithm.ARGON2ID.name -> KdfAlgorithm.ARGON2ID
            KdfAlgorithm.PBKDF2_HMAC_SHA256.name -> KdfAlgorithm.PBKDF2_HMAC_SHA256
            else -> null
        }
    }

    private fun parseKdfVersion(value: String?): KdfVersion? {
        val encodedValue = value?.toIntOrNull() ?: return null
        return KdfVersion.entries.firstOrNull { version ->
            version.encodedValue == encodedValue
        }
    }

    private fun failure(error: VaultHeaderSerializationError): VaultHeaderSerializationResult.Failure {
        return VaultHeaderSerializationResult.Failure(error)
    }

    private fun ByteArray.toHex(): String {
        return joinToString(separator = "") { byte ->
            "%02x".format(byte.toInt() and 0xff)
        }
    }

    private fun String.hexToByteArrayOrNull(): ByteArray? {
        if (isEmpty() || length % 2 != 0) {
            return null
        }

        if (!all { char -> char in '0'..'9' || char in 'a'..'f' || char in 'A'..'F' }) {
            return null
        }

        return ByteArray(length / 2) { index ->
            substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }
}
