package com.vaultia.app.core.crypto.kdf

sealed class KdfDerivationError {
    data object InvalidParameters : KdfDerivationError()
    data object UnsupportedAlgorithm : KdfDerivationError()
    data object NativeLibraryUnavailable : KdfDerivationError()
    data object DerivationFailed : KdfDerivationError()
}
