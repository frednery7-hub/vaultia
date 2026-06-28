package com.vaultia.app.core.security.password

/**
 * Structured result of validating a master password against
 * [MasterPasswordPolicy].
 *
 * This result only describes the quality of the password content.
 * It does not know about, and never carries, a confirmation-mismatch
 * error — that error belongs to feature/auth.
 */
data class MasterPasswordValidationResult(
    val isValid: Boolean,
    val errors: List<MasterPasswordValidationError>,
) {
    companion object {
        fun valid(): MasterPasswordValidationResult =
            MasterPasswordValidationResult(isValid = true, errors = emptyList())

        fun invalid(errors: List<MasterPasswordValidationError>): MasterPasswordValidationResult {
            require(errors.isNotEmpty()) {
                "An invalid result must carry at least one error."
            }
            return MasterPasswordValidationResult(isValid = false, errors = errors)
        }
    }
}
