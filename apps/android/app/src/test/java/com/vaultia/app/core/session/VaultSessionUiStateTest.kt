package com.vaultia.app.core.session

import com.vaultia.app.core.model.vault.VaultSessionState
import org.junit.Assert.assertEquals
import org.junit.Test

class VaultSessionUiStateTest {
    @Test
    fun mapsNotInitializedToLockedUiState() {
        assertEquals(
            VaultSessionUiState.LOCKED,
            VaultSessionState.NOT_INITIALIZED.toUiState(),
        )
    }

    @Test
    fun mapsLockedToLockedUiState() {
        assertEquals(
            VaultSessionUiState.LOCKED,
            VaultSessionState.LOCKED.toUiState(),
        )
    }

    @Test
    fun mapsUnlockedToUnlockedUiState() {
        assertEquals(
            VaultSessionUiState.UNLOCKED,
            VaultSessionState.UNLOCKED.toUiState(),
        )
    }
}