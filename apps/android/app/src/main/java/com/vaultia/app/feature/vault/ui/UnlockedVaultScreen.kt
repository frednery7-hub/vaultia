package com.vaultia.app.feature.vault.ui

import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import com.vaultia.app.core.model.vault.VaultItem

object UnlockedVaultScreen {
    fun create(
        context: Context,
        items: List<VaultItem>,
        onLockRequested: () -> Unit,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 64, 48, 48)
        }

        val homeShell = VaultHomeShell.create(
            context = context,
            items = items,
        )

        val lockButton = Button(context).apply {
            text = "Bloquear"
            setOnClickListener {
                onLockRequested()
            }
        }

        root.addView(homeShell)
        root.addView(lockButton)

        return root
    }
}