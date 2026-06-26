package com.vaultia.app.feature.vault.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.vaultia.app.feature.auth.ui.AuthBoundaryNoticeView

object LockedVaultScreen {
    fun create(
        context: Context,
        onUnlockRequested: () -> Unit,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

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
            text = "Estado: cofre bloqueado"
            textSize = 16f
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 12, 0, 12)
        }

        val authBoundaryNotice = AuthBoundaryNoticeView.create(context)

        val unlockButton = Button(context).apply {
            text = "Desbloquear simulado"
            setOnClickListener {
                onUnlockRequested()
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(description)
        root.addView(stateLabel)
        root.addView(authBoundaryNotice)
        root.addView(unlockButton)

        return root
    }
}
