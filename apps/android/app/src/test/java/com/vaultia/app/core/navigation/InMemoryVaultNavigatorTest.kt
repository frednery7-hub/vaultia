package com.vaultia.app.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class InMemoryVaultNavigatorTest {
    @Test
    fun startsAtHomeDestination() {
        val navigator = InMemoryVaultNavigator()

        assertEquals(VaultDestination.HOME, navigator.current())
    }

    @Test
    fun navigatesToRequestedDestination() {
        val navigator = InMemoryVaultNavigator()

        navigator.navigateTo(VaultDestination.PASSWORDS)

        assertEquals(VaultDestination.PASSWORDS, navigator.current())
    }

    @Test
    fun resetToHomeReturnsNavigatorToHomeDestination() {
        val navigator = InMemoryVaultNavigator()

        navigator.navigateTo(VaultDestination.DOCUMENTS)
        navigator.resetToHome()

        assertEquals(VaultDestination.HOME, navigator.current())
    }
}
