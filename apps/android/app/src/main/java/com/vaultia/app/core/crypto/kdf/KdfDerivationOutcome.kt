package com.vaultia.app.core.crypto.kdf

sealed class KdfDerivationOutcome {
    class Success(val result: KdfResult) : KdfDerivationOutcome()
    class Failure(val error: KdfDerivationError) : KdfDerivationOutcome()
}
