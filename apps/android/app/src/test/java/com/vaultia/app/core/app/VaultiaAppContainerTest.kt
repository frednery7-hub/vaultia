package com.vaultia.app.core.app

import com.vaultia.app.core.model.vault.VaultSessionState
import com.vaultia.app.core.navigation.VaultDestination
import org.junit.Assert.assertEquals
import org.junit.Test

class VaultiaAppContainerTest {
    @Test
    fun createsLockedSessionManager() {
        val container = VaultiaAppContainer()

        assertEquals(VaultSessionState.LOCKED, container.sessionManager.state())
    }

    @Test
    fun createsNavigatorAtHomeDestination() {
        val container = VaultiaAppContainer()

        assertEquals(VaultDestination.HOME, container.vaultNavigator.current())
    }
}
