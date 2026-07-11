package com.vaultia.app.feature.bootstrap.ui

import android.content.Context
import android.widget.FrameLayout
import com.vaultia.app.core.crypto.kdf.KdfParameters
import com.vaultia.app.core.crypto.kdf.KdfResult
import com.vaultia.app.core.navigation.InMemoryVaultNavigator
import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.core.session.VaultSessionUiState
import com.vaultia.app.core.session.toUiState
import com.vaultia.app.core.vault.VaultRepository
import com.vaultia.app.core.vault.VaultRepositoryResult
import com.vaultia.app.feature.vault.ui.LockedVaultScreen
import com.vaultia.app.feature.vault.ui.UnlockedVaultScreen

object BootstrapScreen {
    fun create(
        context: Context,
        sessionManager: InMemorySessionManager,
        vaultRepository: VaultRepository,
        vaultNavigator: InMemoryVaultNavigator,
    ): FrameLayout {
        val root = FrameLayout(context)

        fun render() {
            root.removeAllViews()

            val screen = when (sessionManager.state().toUiState()) {
                VaultSessionUiState.LOCKED -> LockedVaultScreen.create(
                    context = context,
                    onUnlockRequested = {
                        val mockKey = ByteArray(32) { 0 }
                        val mockParams = KdfParameters.Argon2id(ByteArray(16) { 0 }, 32, 1, 1024, 1)
                        val mockResult = KdfResult(mockKey, mockParams)
                        sessionManager.unlockForCurrentProcessOnly(mockResult)
                        vaultNavigator.resetToHome()
                        render()
                    },
                )

                VaultSessionUiState.UNLOCKED -> {
                    val key = sessionManager.getSessionKey()
                    val itemsResult = if (key != null) vaultRepository.listItems(key) else null
                    val items = if (itemsResult is VaultRepositoryResult.Success) itemsResult.data else emptyList()
                    
                    UnlockedVaultScreen.create(
                        context = context,
                        items = items,
                        currentDestination = vaultNavigator.current(),
                        onNavigateRequested = { destination ->
                            vaultNavigator.navigateTo(destination)
                            render()
                        },
                        onLockRequested = {
                            sessionManager.lock()
                            vaultNavigator.resetToHome()
                            render()
                        },
                    )
                }
            }

            root.addView(screen)
        }

        render()

        return root
    }
}
