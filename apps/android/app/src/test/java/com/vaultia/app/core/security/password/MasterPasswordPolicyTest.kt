package com.vaultia.app.core.security.password

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPasswordPolicyTest {

    // ---- Regra: vazia ----

    @Test
    fun emptyPasswordIsRejected() {
        val result = MasterPasswordPolicy.validate("")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.EMPTY))
    }

    @Test
    fun emptyPasswordDoesNotAlsoReportTooShort() {
        val result = MasterPasswordPolicy.validate("")

        assertFalse(result.errors.contains(MasterPasswordValidationError.TOO_SHORT))
    }

    // ---- Regra: comprimento minimo ----

    @Test
    fun passwordBelowMinimumLengthIsRejected() {
        val tooShort = "short16chars!!!" // 15 chars
        assertEquals(15, tooShort.length)

        val result = MasterPasswordPolicy.validate(tooShort)

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TOO_SHORT))
    }

    @Test
    fun passwordAtExactlyMinimumLengthIsNotRejectedForLength() {
        val exactlyMin = "z".repeat(MasterPasswordPolicy.MIN_LENGTH)
        assertEquals(MasterPasswordPolicy.MIN_LENGTH, exactlyMin.length)

        val result = MasterPasswordPolicy.validate(exactlyMin)

        assertFalse(result.errors.contains(MasterPasswordValidationError.TOO_SHORT))
    }

    // ---- Regra: comprimento maximo ----

    @Test
    fun passwordAboveMaximumLengthIsRejected() {
        val tooLong = "correctHorseBatteryStaple".repeat(3) // > 64 chars

        val result = MasterPasswordPolicy.validate(tooLong)

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TOO_LONG))
    }

    @Test
    fun passwordAtExactlyMaximumLengthIsNotRejectedForLength() {
        val exactlyMax = "cavalochuvametrocobrejanela".repeat(3).take(MasterPasswordPolicy.MAX_LENGTH)
        assertEquals(MasterPasswordPolicy.MAX_LENGTH, exactlyMax.length)

        val result = MasterPasswordPolicy.validate(exactlyMax)

        assertFalse(result.errors.contains(MasterPasswordValidationError.TOO_LONG))
    }

    @Test
    fun passwordIsNotSilentlyTruncated() {
        val tooLong = "x".repeat(MasterPasswordPolicy.MAX_LENGTH + 1)

        val result = MasterPasswordPolicy.validate(tooLong)

        assertFalse(result.isValid)
    }

    // ---- Regra: espaco no inicio ou no fim ----

    @Test
    fun passwordWithLeadingSpaceIsRejected() {
        val result = MasterPasswordPolicy.validate(" cavalo chuva metro cobreee")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.LEADING_OR_TRAILING_SPACE))
    }

    @Test
    fun passwordWithTrailingSpaceIsRejected() {
        val result = MasterPasswordPolicy.validate("cavalo chuva metro cobreee ")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.LEADING_OR_TRAILING_SPACE))
    }

    @Test
    fun passwordWithInternalSpacesOnlyIsNotRejectedForSpacing() {
        val result = MasterPasswordPolicy.validate("cavalo chuva metro cobre")

        assertFalse(result.errors.contains(MasterPasswordValidationError.LEADING_OR_TRAILING_SPACE))
    }

    // ---- Regra: lista de senhas comuns/fracas ----

    @Test
    fun knownCommonPasswordsAreRejected() {
        val commonPasswords = listOf(
            "passwordpassword",
            "1234567890123456",
            "qwertyqwertyqwerty",
            "aaaaaaaaaaaaaaaa",
            "senha123senha123",
            "vaultiavaultia123",
        )

        for (password in commonPasswords) {
            val result = MasterPasswordPolicy.validate(password)
            assertFalse("Expected '$password' to be rejected", result.isValid)
            assertTrue(
                "Expected '$password' to be flagged as common/weak",
                result.errors.contains(MasterPasswordValidationError.COMMON_OR_WEAK_PASSWORD),
            )
        }
    }

    @Test
    fun commonPasswordCheckIsCaseInsensitive() {
        val result = MasterPasswordPolicy.validate("PASSWORDPASSWORD")

        assertTrue(result.errors.contains(MasterPasswordValidationError.COMMON_OR_WEAK_PASSWORD))
    }

    // ---- Regra: repeticao trivial de caractere ----

    @Test
    fun fullStringSingleCharacterRepetitionIsRejected() {
        val result = MasterPasswordPolicy.validate("x".repeat(20))

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TRIVIAL_CHARACTER_REPETITION))
    }

    @Test
    fun shortRepeatedRunInsideLongerPassphraseIsNotRejectedForRepetition() {
        val result = MasterPasswordPolicy.validate("cavalo aaaa chuva metro cobre")

        assertFalse(result.errors.contains(MasterPasswordValidationError.TRIVIAL_CHARACTER_REPETITION))
    }

    // ---- Regra: sequencia trivial ----

    @Test
    fun ascendingDigitSequenceIsRejected() {
        val result = MasterPasswordPolicy.validate("abcdefghijklmnopqrstuvwxyz12")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TRIVIAL_SEQUENCE))
    }

    @Test
    fun trivialSequenceEmbeddedInLongerPasswordIsRejected() {
        val result = MasterPasswordPolicy.validate("minhasenha1234semestraseguranca")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TRIVIAL_SEQUENCE))
    }

    @Test
    fun descendingSequenceIsRejected() {
        val result = MasterPasswordPolicy.validate("senha9876muitosegurahojeagora")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TRIVIAL_SEQUENCE))
    }

    @Test
    fun shortNonTrivialRunIsNotRejectedAsSequence() {
        val result = MasterPasswordPolicy.validate("cavalo123chuvametrocobreja")

        assertFalse(result.errors.contains(MasterPasswordValidationError.TRIVIAL_SEQUENCE))
    }

    // ---- Casos que devem ser aceitos ----

    @Test
    fun longMemorablePassphraseWithoutSpecialCompositionIsAccepted() {
        val passphrases = listOf(
            "cavalo chuva metro cobre",
            "janela azul pedra domingo",
            "correto cavalo bateria grampo",
        )

        for (passphrase in passphrases) {
            val result = MasterPasswordPolicy.validate(passphrase)
            assertTrue("Expected '$passphrase' to be accepted, got ${result.errors}", result.isValid)
            assertTrue(result.errors.isEmpty())
        }
    }

    @Test
    fun passphraseWithAccentsIsAccepted() {
        val result = MasterPasswordPolicy.validate("café com leite quente hoje")

        assertTrue(result.isValid)
    }

    @Test
    fun passphraseMixingDigitsAndSymbolsIsAcceptedWhenNotRequired() {
        val result = MasterPasswordPolicy.validate("Cavalo7Chuva9Metro2Cobre!")

        assertTrue(result.isValid)
    }

    // ---- Combinacao de multiplos erros ----

    @Test
    fun multipleSimultaneousErrorsAreAllReported() {
        // " aaa " e curta e tem espaco nas bordas. Nao e repeticao trivial pura,
        // porque o primeiro caractere e o espaco, nao 'a' — a regra de
        // repeticao exige que TODOS os caracteres sejam iguais entre si.
        val result = MasterPasswordPolicy.validate(" aaa ")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TOO_SHORT))
        assertTrue(result.errors.contains(MasterPasswordValidationError.LEADING_OR_TRAILING_SPACE))
        assertFalse(result.errors.contains(MasterPasswordValidationError.TRIVIAL_CHARACTER_REPETITION))
    }

    @Test
    fun shortPasswordThatIsAlsoTrivialRepetitionReportsBothErrors() {
        // Aqui sim todos os caracteres sao iguais (sem espacos nas bordas),
        // entao TOO_SHORT e TRIVIAL_CHARACTER_REPETITION disparam juntos.
        val result = MasterPasswordPolicy.validate("aaaa")

        assertFalse(result.isValid)
        assertTrue(result.errors.contains(MasterPasswordValidationError.TOO_SHORT))
        assertTrue(result.errors.contains(MasterPasswordValidationError.TRIVIAL_CHARACTER_REPETITION))
    }

    // ---- Boundary: a policy nao conhece confirmacao ----

    @Test
    fun validateAcceptsOnlyASingleStringParameter() {
        val method = MasterPasswordPolicy::class.java.getMethod("validate", String::class.java)

        assertEquals(1, method.parameterCount)
    }
}
