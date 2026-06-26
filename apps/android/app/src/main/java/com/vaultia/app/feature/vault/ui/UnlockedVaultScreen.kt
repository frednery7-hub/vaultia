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

            VaultDestination.PASSWORDS -> createSectionPlaceholder(
                context = context,
                title = "Senhas",
                description = "Área reservada para metadados de senhas. CRUD real ainda não implementado.",
            )

            VaultDestination.NOTES -> createSectionPlaceholder(
                context = context,
                title = "Notas",
                description = "Área reservada para metadados de notas. Conteúdo sensível ainda não implementado.",
            )

            VaultDestination.PHOTOS -> createSectionPlaceholder(
                context = context,
                title = "Fotos",
                description = "Área reservada para metadados de fotos privadas. Arquivos reais ainda não implementados.",
            )

            VaultDestination.DOCUMENTS -> createSectionPlaceholder(
                context = context,
                title = "Documentos",
                description = "Área reservada para metadados de documentos. Storage real ainda não implementado.",
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

    private fun createSectionPlaceholder(
        context: Context,
        title: String,
        description: String,
    ): LinearLayout {
        val section = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 24, 0, 24)
        }

        val sectionTitle = TextView(context).apply {
            text = title
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val sectionDescription = TextView(context).apply {
            text = description
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 12)
        }

        section.addView(sectionTitle)
        section.addView(sectionDescription)

        return section
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