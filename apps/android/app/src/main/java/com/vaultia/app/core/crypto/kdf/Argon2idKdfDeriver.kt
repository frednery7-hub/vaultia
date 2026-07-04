package com.vaultia.app.core.crypto.kdf

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode

class Argon2idKdfDeriver(
    private val hashFunction: Argon2idHashFunction = Argon2idHashFunction {
            password,
            salt,
            iterations,
            memoryCostKiB,
            parallelism,
            outputLengthBytes,
        ->
        Argon2Kt()
            .hash(
                mode = Argon2Mode.ARGON2_ID,
                password = password,
                salt = salt,
                tCostInIterations = iterations,
                mCostInKibibyte = memoryCostKiB,
                parallelism = parallelism,
                hashLengthInBytes = outputLengthBytes,
            )
            .rawHashAsHexadecimal()
            .hexToByteArray()
    },
) : KdfDeriver {
    override fun derive(
        password: ByteArray,
        parameters: KdfParameters,
    ): KdfDerivationOutcome {
        if (password.isEmpty()) {
            return KdfDerivationOutcome.Failure(KdfDerivationError.InvalidParameters)
        }

        if (parameters !is KdfParameters.Argon2id) {
            return KdfDerivationOutcome.Failure(KdfDerivationError.UnsupportedAlgorithm)
        }

        return try {
            val derivedKey = hashFunction.hash(
                password = password.copyOf(),
                salt = parameters.saltCopy(),
                iterations = parameters.iterations,
                memoryCostKiB = parameters.memoryCostKiB,
                parallelism = parameters.parallelism,
                outputLengthBytes = parameters.outputLengthBytes,
            )

            KdfDerivationOutcome.Success(
                KdfResult(
                    derivedKey = derivedKey,
                    parameters = parameters,
                ),
            )
        } catch (_: UnsatisfiedLinkError) {
            KdfDerivationOutcome.Failure(KdfDerivationError.NativeLibraryUnavailable)
        } catch (_: NoClassDefFoundError) {
            KdfDerivationOutcome.Failure(KdfDerivationError.NativeLibraryUnavailable)
        } catch (_: LinkageError) {
            KdfDerivationOutcome.Failure(KdfDerivationError.NativeLibraryUnavailable)
        } catch (_: Throwable) {
            KdfDerivationOutcome.Failure(KdfDerivationError.DerivationFailed)
        }
    }
}

fun interface Argon2idHashFunction {
    fun hash(
        password: ByteArray,
        salt: ByteArray,
        iterations: Int,
        memoryCostKiB: Int,
        parallelism: Int,
        outputLengthBytes: Int,
    ): ByteArray
}

private fun String.hexToByteArray(): ByteArray {
    require(length % 2 == 0) {
        "Hexadecimal string must have an even length."
    }

    return ByteArray(length / 2) { index ->
        substring(index * 2, index * 2 + 2).toInt(16).toByte()
    }
}
