package com.vaultia.app.feature.vault.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.vaultia.app.core.model.vault.VaultItem
import com.vaultia.app.core.model.vault.VaultItemType

object VaultHomeShell {
    fun create(
        context: Context,
        items: List<VaultItem>,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }

        val title = TextView(context).apply {
            text = "Área interna do cofre"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 12)
        }

        val description = TextView(context).apply {
            text = if (items.isEmpty()) {
                "Nenhum item salvo nesta fase."
            } else {
                "Exibindo somente metadados temporários."
            }
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }

        root.addView(title)
        root.addView(description)

        root.addView(categoryCard(context, "Senhas", countItems(items, VaultItemType.PASSWORD)))
        root.addView(categoryCard(context, "Notas", countItems(items, VaultItemType.NOTE)))
        root.addView(categoryCard(context, "Fotos", countItems(items, VaultItemType.PHOTO)))
        root.addView(categoryCard(context, "Documentos", countItems(items, VaultItemType.DOCUMENT)))

        return root
    }

    private fun countItems(
        items: List<VaultItem>,
        type: VaultItemType,
    ): Int {
        return items.count { item -> item.type == type }
    }

    private fun categoryCard(
        context: Context,
        title: String,
        count: Int,
    ): TextView {
        return TextView(context).apply {
            text = "$title — $count itens"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(24, 14, 24, 14)
        }
    }
}
