package com.vaultia.app.core.security.password

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MasterPasswordPolicyArchitectureTest {
    private val policySource = Paths.get(
        "src/main/java/com/vaultia/app/core/security/password/MasterPasswordPolicy.kt",
    ).toFile().readText()

    private val resultSource = Paths.get(
        "src/main/java/com/vaultia/app/core/security/password/MasterPasswordValidationResult.kt",
    ).toFile().readText()

    private val errorSource = Paths.get(
        "src/main/java/com/vaultia/app/core/security/password/MasterPasswordValidationError.kt",
    ).toFile().readText()

    @Test
    fun policyDeclaresExpectedLengthConstants() {
        assertTrue(policySource.contains("MIN_LENGTH = 16"))
        assertTrue(policySource.contains("MAX_LENGTH = 64"))
    }

    @Test
    fun policyExposesSingleValidateEntryPoint() {
        assertTrue(policySource.contains("fun validate(password: String): MasterPasswordValidationResult"))
    }

    @Test
    fun policyDoesNotKnowAboutConfirmationField() {
        assertFalse(policySource.contains("confirmation"))
        assertFalse(policySource.contains("Confirmation"))
        assertFalse(policySource.contains("confirm"))
    }

    @Test
    fun errorEnumDoesNotContainConfirmationMismatch() {
        assertFalse(errorSource.contains("CONFIRMATION"))
        assertFalse(errorSource.contains("MISMATCH"))
    }

    @Test
    fun errorEnumContainsExpectedCases() {
        assertTrue(errorSource.contains("EMPTY"))
        assertTrue(errorSource.contains("TOO_SHORT"))
        assertTrue(errorSource.contains("TOO_LONG"))
        assertTrue(errorSource.contains("LEADING_OR_TRAILING_SPACE"))
        assertTrue(errorSource.contains("COMMON_OR_WEAK_PASSWORD"))
        assertTrue(errorSource.contains("TRIVIAL_CHARACTER_REPETITION"))
        assertTrue(errorSource.contains("TRIVIAL_SEQUENCE"))
    }

    @Test
    fun resultDoesNotExposeMutablePublicState() {
        assertFalse(resultSource.contains("var "))
    }

    @Test
    fun policyDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(policySource.contains("SharedPreferences"))
        assertFalse(policySource.contains("DataStore"))
        assertFalse(policySource.contains("SQLite"))
        assertFalse(policySource.contains("Room"))
        assertFalse(policySource.contains("File("))
        assertFalse(policySource.contains("Cipher"))
        assertFalse(policySource.contains("Keystore"))
        assertFalse(policySource.contains("MessageDigest"))
        assertFalse(policySource.contains("Http"))
        assertFalse(policySource.contains("Socket"))
        assertFalse(policySource.contains("Retrofit"))
        assertFalse(policySource.contains("OkHttp"))
    }

    @Test
    fun policyDoesNotDependOnAndroidFramework() {
        assertFalse(policySource.contains("import android."))
        assertFalse(policySource.contains("import androidx."))
    }

    @Test
    fun policyHasNoUiAwareness() {
        assertFalse(policySource.contains("Activity"))
        assertFalse(policySource.contains("Fragment"))
        assertFalse(policySource.contains("Compose"))
        assertFalse(policySource.contains("TextView"))
        assertFalse(policySource.contains("Context"))
    }

    @Test
    fun commonPasswordListRemainsSmallAndEmbedded() {
        assertFalse(policySource.contains("readText()"))
        assertFalse(policySource.contains("InputStream"))
        assertFalse(policySource.contains("assets"))
        assertTrue(policySource.contains("COMMON_OR_WEAK_PASSWORDS"))
    }
}
