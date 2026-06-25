package com.vaultia.app.feature.vault.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

object VaultHomeShell {
    fun create(context: Context): LinearLayout {
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
            text = "Nenhum item salvo nesta fase."
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }

        root.addView(title)
        root.addView(description)

        root.addView(categoryCard(context, "Senhas", "0 itens"))
        root.addView(categoryCard(context, "Notas", "0 itens"))
        root.addView(categoryCard(context, "Fotos", "0 itens"))
        root.addView(categoryCard(context, "Documentos", "0 itens"))

        return root
    }

    private fun categoryCard(
        context: Context,
        title: String,
        count: String,
    ): TextView {
        return TextView(context).apply {
            text = "$title — $count"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(24, 14, 24, 14)
        }
    }
}
