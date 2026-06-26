package com.vaultia.app.feature.vault.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

object VaultEmptyStateView {
    fun create(
        context: Context,
        title: String,
        description: String,
        securityNote: String,
    ): LinearLayout {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 24, 0, 24)
        }

        val titleView = TextView(context).apply {
            text = title
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val descriptionView = TextView(context).apply {
            text = description
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 12)
        }

        val securityNoteView = TextView(context).apply {
            text = securityNote
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
        }

        root.addView(titleView)
        root.addView(descriptionView)
        root.addView(securityNoteView)

        return root
    }
}
