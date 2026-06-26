package com.vaultia.app.core.navigation

class InMemoryVaultNavigator {
    private var currentDestination: VaultDestination = VaultDestination.HOME

    fun current(): VaultDestination = currentDestination

    fun navigateTo(destination: VaultDestination) {
        currentDestination = destination
    }

    fun resetToHome() {
        currentDestination = VaultDestination.HOME
    }
}