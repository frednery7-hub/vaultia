package com.vaultia.app.core.crypto.kdf

interface KdfDeriver {
    fun derive(
        password: ByteArray,
        parameters: KdfParameters,
    ): KdfDerivationOutcome
}
