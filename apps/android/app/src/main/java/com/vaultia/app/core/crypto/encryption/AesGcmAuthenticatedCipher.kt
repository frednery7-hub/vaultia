package com.vaultia.app.core.crypto.encryption

import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class AesGcmAuthenticatedCipher(
    private val secureRandom: SecureRandom = SecureRandom(),
) : AuthenticatedCipher {
    override fun encrypt(
        key: ByteArray,
        plaintext: PlaintextPayload,
    ): EncryptionResult {
        if (!isValidAesKey(key)) {
            return EncryptionResult.Failure(EncryptionError.InvalidKey)
        }

        val nonce = ByteArray(EncryptedPayload.NONCE_LENGTH_BYTES)
        try {
            secureRandom.nextBytes(nonce)
        } catch (_: Throwable) {
            return EncryptionResult.Failure(EncryptionError.NonceGenerationFailed)
        }

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(key.copyOf(), ALGORITHM),
                GCMParameterSpec(TAG_LENGTH_BITS, nonce.copyOf()),
            )

            val combinedCiphertextAndTag = cipher.doFinal(plaintext.plaintextCopy())
            if (combinedCiphertextAndTag.size <= EncryptedPayload.AUTHENTICATION_TAG_LENGTH_BYTES) {
                return EncryptionResult.Failure(EncryptionError.EncryptionFailed)
            }

            val ciphertextLength = combinedCiphertextAndTag.size -
                EncryptedPayload.AUTHENTICATION_TAG_LENGTH_BYTES
            val ciphertext = combinedCiphertextAndTag.copyOfRange(0, ciphertextLength)
            val authenticationTag = combinedCiphertextAndTag.copyOfRange(
                ciphertextLength,
                combinedCiphertextAndTag.size,
            )

            EncryptionResult.Success(
                EncryptedPayload(
                    nonce = nonce,
                    ciphertext = ciphertext,
                    authenticationTag = authenticationTag,
                ),
            )
        } catch (_: IllegalArgumentException) {
            EncryptionResult.Failure(EncryptionError.InvalidPlaintext)
        } catch (_: Throwable) {
            EncryptionResult.Failure(EncryptionError.EncryptionFailed)
        }
    }

    override fun decrypt(
        key: ByteArray,
        encryptedPayload: EncryptedPayload,
    ): DecryptionResult {
        if (!isValidAesKey(key)) {
            return DecryptionResult.Failure(EncryptionError.InvalidKey)
        }

        return try {
            val combinedCiphertextAndTag = encryptedPayload.ciphertextCopy() +
                encryptedPayload.authenticationTagCopy()

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(key.copyOf(), ALGORITHM),
                GCMParameterSpec(TAG_LENGTH_BITS, encryptedPayload.nonceCopy()),
            )

            DecryptionResult.Success(
                PlaintextPayload(cipher.doFinal(combinedCiphertextAndTag)),
            )
        } catch (_: AEADBadTagException) {
            DecryptionResult.Failure(EncryptionError.AuthenticationFailed)
        } catch (_: IllegalArgumentException) {
            DecryptionResult.Failure(EncryptionError.InvalidEncryptedPayload)
        } catch (_: Throwable) {
            DecryptionResult.Failure(EncryptionError.DecryptionFailed)
        }
    }

    private fun isValidAesKey(key: ByteArray): Boolean {
        return key.size == 16 || key.size == 24 || key.size == 32
    }

    private companion object {
        const val ALGORITHM: String = "AES"
        const val TRANSFORMATION: String = "AES/GCM/NoPadding"
        const val TAG_LENGTH_BITS: Int = 128
    }
}
