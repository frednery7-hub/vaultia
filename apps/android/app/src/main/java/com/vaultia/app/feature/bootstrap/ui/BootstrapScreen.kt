package com.vaultia.app.feature.bootstrap.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.core.session.VaultSessionUiState
import com.vaultia.app.core.session.toUiState
import com.vaultia.app.core.storage.InMemoryVaultRepository
import com.vaultia.app.feature.vault.ui.VaultHomeShell

object BootstrapScreen {
    fun create(
        context: Context,
        sessionManager: InMemorySessionManager,
        vaultRepository: InMemoryVaultRepository,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }

        fun render() {
            root.removeAllViews()

            val uiState = sessionManager.state().toUiState()
            val isUnlocked = uiState == VaultSessionUiState.UNLOCKED

            val title = TextView(context).apply {
                text = "Vaultia"
                textSize = 32f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
            }

            val subtitle = TextView(context).apply {
                text = "Cofre local para dados sensíveis"
                textSize = 18f
                gravity = Gravity.CENTER
            }

            val description = TextView(context).apply {
                text = "Este app é local-first. Sem nuvem, sem conta e sem recuperação remota."
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, 24, 0, 24)
            }

            val stateLabel = TextView(context).apply {
                text = if (isUnlocked) {
                    "Estado: cofre desbloqueado"
                } else {
                    "Estado: cofre bloqueado"
                }
                textSize = 16f
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
                setPadding(0, 12, 0, 12)
            }

            val actionButton = Button(context).apply {
                text = if (isUnlocked) {
                    "Bloquear"
                } else {
                    "Desbloquear simulado"
                }

                setOnClickListener {
                    if (sessionManager.state().toUiState() == VaultSessionUiState.UNLOCKED) {
                        sessionManager.lock()
                    } else {
                        sessionManager.unlockForCurrentProcessOnly()
                    }

                    render()
                }
            }

            root.addView(title)
            root.addView(subtitle)
            root.addView(description)
            root.addView(stateLabel)
            root.addView(actionButton)

            if (isUnlocked) {
                root.addView(VaultHomeShell.create(context, vaultRepository.listMetadata()))
            }
        }

        render()

        return root
    }
}