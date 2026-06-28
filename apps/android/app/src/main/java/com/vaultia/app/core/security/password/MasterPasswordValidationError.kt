package com.vaultia.app.core.security.password

/**
 * Possible quality errors for a Vaultia master password.
 *
 * This enum represents failures of the password content itself.
 * It intentionally does not include a "confirmation mismatch" case —
 * that concern belongs to feature/auth, not to this policy.
 */
enum class MasterPasswordValidationError {
    EMPTY,
    TOO_SHORT,
    TOO_LONG,
    LEADING_OR_TRAILING_SPACE,
    COMMON_OR_WEAK_PASSWORD,
    TRIVIAL_CHARACTER_REPETITION,
    TRIVIAL_SEQUENCE,
}
