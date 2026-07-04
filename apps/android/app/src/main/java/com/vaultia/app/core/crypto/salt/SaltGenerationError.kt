package com.vaultia.app.core.crypto.salt

sealed class SaltGenerationError {
    data object InvalidSaltLength : SaltGenerationError()
    data object GenerationFailed : SaltGenerationError()
}
