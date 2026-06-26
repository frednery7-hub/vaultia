package com.vaultia.app.feature.auth.ui

import java.nio.file.Paths
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthBoundaryNoticeViewArchitectureTest {
    private val authBoundarySource = Paths.get(
        "src/main/java/com/vaultia/app/feature/auth/ui/AuthBoundaryNoticeView.kt",
    ).toFile().readText()

    @Test
    fun authBoundaryNoticeIsExplicitAboutSimulatedUnlock() {
        assertTrue(authBoundarySource.contains("object AuthBoundaryNoticeView"))
        assertTrue(authBoundarySource.contains("Limite de segurança atual"))
        assertTrue(authBoundarySource.contains("desbloqueio desta versão ainda é simulado"))
        assertTrue(authBoundarySource.contains("Dados reais só serão permitidos"))
    }

    @Test
    fun authBoundaryNoticeNamesRequiredSecurityGates() {
        assertTrue(authBoundarySource.contains("autenticação local real"))
        assertTrue(authBoundarySource.contains("KDF"))
        assertTrue(authBoundarySource.contains("criptografia"))
        assertTrue(authBoundarySource.contains("storage seguro"))
    }

    @Test
    fun authBoundaryNoticeDoesNotUseStorageCryptoOrNetworkApis() {
        assertFalse(authBoundarySource.contains("SharedPreferences"))
        assertFalse(authBoundarySource.contains("DataStore"))
        assertFalse(authBoundarySource.contains("SQLite"))
        assertFalse(authBoundarySource.contains("Room"))
        assertFalse(authBoundarySource.contains("File("))
        assertFalse(authBoundarySource.contains("Cipher"))
        assertFalse(authBoundarySource.contains("Keystore"))
        assertFalse(authBoundarySource.contains("Http"))
        assertFalse(authBoundarySource.contains("Socket"))
        assertFalse(authBoundarySource.contains("Retrofit"))
        assertFalse(authBoundarySource.contains("OkHttp"))
    }
}
