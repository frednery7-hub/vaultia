package com.vaultia.app.core.security.password

/**
 * Local, UI-agnostic, storage-agnostic, crypto-agnostic quality policy for
 * the Vaultia master password.
 *
 * This class validates the content of a single password string. It does not:
 * - know about a password-matching field from a second input;
 * - hash, salt, or derive anything;
 * - call any key derivation or secure hardware API;
 * - touch storage of any kind;
 * - know about UI.
 *
 * Matching this password against a second input typed by the user is the
 * responsibility of feature/auth, which is expected to call [validate] and
 * combine its result with its own flow/UI errors.
 */
object MasterPasswordPolicy {

    const val MIN_LENGTH = 16
    const val MAX_LENGTH = 64

    /**
     * Small, deliberately limited, embedded set of common/weak password
     * patterns rejected outright in this phase. Matching is case-insensitive.
     * A larger/external list is explicitly out of scope for Phase 21.
     */
    private val COMMON_OR_WEAK_PASSWORDS: Set<String> = setOf(
        "passwordpassword",
        "1234567890123456",
        "qwertyqwertyqwerty",
        "aaaaaaaaaaaaaaaa",
        "senha123senha123",
        "vaultiavaultia123",
        "minhasenhasegura",
        "passwordpassword123",
        "letmeinletmein123",
        "abcdefghijklmnop",
        "zyxwvutsrqponmlk",
    )

    /** Minimum run length to be considered a trivial sequence (e.g. "1234"). */
    private const val TRIVIAL_SEQUENCE_RUN_LENGTH = 4

    fun validate(password: String): MasterPasswordValidationResult {
        val errors = mutableListOf<MasterPasswordValidationError>()

        if (password.isEmpty()) {
            errors += MasterPasswordValidationError.EMPTY
        }

        if (password.isNotEmpty() && password.length < MIN_LENGTH) {
            errors += MasterPasswordValidationError.TOO_SHORT
        }

        if (password.length > MAX_LENGTH) {
            errors += MasterPasswordValidationError.TOO_LONG
        }

        if (password.isNotEmpty() && hasLeadingOrTrailingSpace(password)) {
            errors += MasterPasswordValidationError.LEADING_OR_TRAILING_SPACE
        }

        if (password.isNotEmpty() && isCommonOrWeak(password)) {
            errors += MasterPasswordValidationError.COMMON_OR_WEAK_PASSWORD
        }

        if (password.isNotEmpty() && isTrivialCharacterRepetition(password)) {
            errors += MasterPasswordValidationError.TRIVIAL_CHARACTER_REPETITION
        }

        if (password.isNotEmpty() && containsTrivialSequence(password)) {
            errors += MasterPasswordValidationError.TRIVIAL_SEQUENCE
        }

        return if (errors.isEmpty()) {
            MasterPasswordValidationResult.valid()
        } else {
            MasterPasswordValidationResult.invalid(errors)
        }
    }

    private fun hasLeadingOrTrailingSpace(password: String): Boolean {
        return password.first().isWhitespace() || password.last().isWhitespace()
    }

    private fun isCommonOrWeak(password: String): Boolean {
        return COMMON_OR_WEAK_PASSWORDS.contains(password.lowercase())
    }

    /**
     * Rejects a password that is made of a single repeated character,
     * e.g. "aaaaaaaaaaaaaaaa". A password that merely contains a short
     * repeated run (e.g. "aaaa" inside a longer passphrase) is NOT rejected
     * by this rule — only full-string single-character repetition is.
     */
    private fun isTrivialCharacterRepetition(password: String): Boolean {
        val first = password[0]
        return password.all { it == first }
    }

    /**
     * Rejects a password that contains a trivial ascending or descending
     * run of [TRIVIAL_SEQUENCE_RUN_LENGTH] or more consecutive characters
     * in the alphabet or in the digits, anywhere in the string — not only
     * when the whole password is such a sequence.
     *
     * Examples rejected: "1234", "4321", "abcd", "dcba", "qrst", embedded
     * anywhere in a longer password.
     */
    private fun containsTrivialSequence(password: String): Boolean {
        val lower = password.lowercase()
        var ascendingRun = 1
        var descendingRun = 1

        for (i in 1 until lower.length) {
            val previous = lower[i - 1]
            val current = lower[i]

            val isAscendingStep = isSequenceableChar(previous) &&
                isSequenceableChar(current) &&
                (current.code - previous.code == 1)
            val isDescendingStep = isSequenceableChar(previous) &&
                isSequenceableChar(current) &&
                (previous.code - current.code == 1)

            ascendingRun = if (isAscendingStep) ascendingRun + 1 else 1
            descendingRun = if (isDescendingStep) descendingRun + 1 else 1

            if (ascendingRun >= TRIVIAL_SEQUENCE_RUN_LENGTH ||
                descendingRun >= TRIVIAL_SEQUENCE_RUN_LENGTH
            ) {
                return true
            }
        }

        return false
    }

    private fun isSequenceableChar(char: Char): Boolean {
        return char.isDigit() || (char in 'a'..'z')
    }
}
