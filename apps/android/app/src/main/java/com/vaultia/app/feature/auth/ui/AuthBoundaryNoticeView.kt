package com.vaultia.app.feature.auth.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

object AuthBoundaryNoticeView {
    fun create(context: Context): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 16, 0, 16)
        }

        val title = TextView(context).apply {
            text = "Limite de segurança atual"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val description = TextView(context).apply {
            text = "O desbloqueio desta versão ainda é simulado. Dados reais só serão permitidos depois de autenticação local real, KDF, criptografia e storage seguro."
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
        }

        root.addView(title)
        root.addView(description)

        return root
    }
}
