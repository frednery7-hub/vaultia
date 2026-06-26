package com.vaultia.app.feature.vault.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.vaultia.app.core.model.vault.VaultItem
import com.vaultia.app.core.navigation.VaultDestination

object UnlockedVaultScreen {
    fun create(
        context: Context,
        items: List<VaultItem>,
        currentDestination: VaultDestination,
        onNavigateRequested: (VaultDestination) -> Unit,
        onLockRequested: () -> Unit,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 64, 48, 48)
        }

        val navigationTitle = TextView(context).apply {
            text = "Seção atual: ${currentDestination.label()}"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }

        val navigationBar = createNavigationBar(
            context = context,
            currentDestination = currentDestination,
            onNavigateRequested = onNavigateRequested,
        )

        val content = when (currentDestination) {
            VaultDestination.HOME -> VaultHomeShell.create(
                context = context,
                items = items,
            )

            VaultDestination.PASSWORDS -> VaultEmptyStateView.create(
                context = context,
                title = "Senhas",
                description = "Nenhuma senha real será criada nesta fase. Esta seção prepara apenas a navegação local.",
                securityNote = "Próximo passo futuro: autenticação local, KDF e criptografia antes de qualquer segredo real.",
            )

            VaultDestination.NOTES -> VaultEmptyStateView.create(
                context = context,
                title = "Notas",
                description = "Nenhuma nota sensível será armazenada nesta fase. O conteúdo real ainda está bloqueado por arquitetura.",
                securityNote = "Notas reais só entram depois de storage criptografado e política de desbloqueio.",
            )

            VaultDestination.PHOTOS -> VaultEmptyStateView.create(
                context = context,
                title = "Fotos",
                description = "Nenhuma foto privada será importada nesta fase. Arquivos reais continuam fora do escopo.",
                securityNote = "Fotos exigem armazenamento privado, nomes opacos e criptografia antes do uso real.",
            )

            VaultDestination.DOCUMENTS -> VaultEmptyStateView.create(
                context = context,
                title = "Documentos",
                description = "Nenhum documento real será salvo nesta fase. Esta tela é apenas preparação de UX.",
                securityNote = "Documentos reais só entram após envelope encryption e fluxo de importação seguro.",
            )
        }

        val lockButton = Button(context).apply {
            text = "Bloquear"
            setOnClickListener {
                onLockRequested()
            }
        }

        root.addView(navigationTitle)
        root.addView(navigationBar)
        root.addView(content)
        root.addView(lockButton)

        return root
    }

    private fun createNavigationBar(
        context: Context,
        currentDestination: VaultDestination,
        onNavigateRequested: (VaultDestination) -> Unit,
    ): LinearLayout {
        val navigationBar = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 24)
        }

        VaultDestination.entries.forEach { destination ->
            val button = Button(context).apply {
                text = if (destination == currentDestination) {
                    "• ${destination.label()}"
                } else {
                    destination.label()
                }

                setOnClickListener {
                    onNavigateRequested(destination)
                }
            }

            navigationBar.addView(button)
        }

        return navigationBar
    }

    private fun VaultDestination.label(): String {
        return when (this) {
            VaultDestination.HOME -> "Início"
            VaultDestination.PASSWORDS -> "Senhas"
            VaultDestination.NOTES -> "Notas"
            VaultDestination.PHOTOS -> "Fotos"
            VaultDestination.DOCUMENTS -> "Documentos"
        }
    }
}
