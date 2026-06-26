package com.vaultia.app.feature.bootstrap.ui

import android.content.Context
import android.widget.FrameLayout
import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.core.session.VaultSessionUiState
import com.vaultia.app.core.session.toUiState
import com.vaultia.app.core.storage.InMemoryVaultRepository
import com.vaultia.app.feature.vault.ui.LockedVaultScreen
import com.vaultia.app.feature.vault.ui.UnlockedVaultScreen

object BootstrapScreen {
    fun create(
        context: Context,
        sessionManager: InMemorySessionManager,
        vaultRepository: InMemoryVaultRepository,
    ): FrameLayout {
        val root = FrameLayout(context)

        fun render() {
            root.removeAllViews()

            val screen = when (sessionManager.state().toUiState()) {
                VaultSessionUiState.LOCKED -> LockedVaultScreen.create(
                    context = context,
                    onUnlockRequested = {
                        sessionManager.unlockForCurrentProcessOnly()
                        render()
                    },
                )

                VaultSessionUiState.UNLOCKED -> UnlockedVaultScreen.create(
                    context = context,
                    items = vaultRepository.listMetadata(),
                    onLockRequested = {
                        sessionManager.lock()
                        render()
                    },
                )
            }

            root.addView(screen)
        }

        render()

        return root
    }
}