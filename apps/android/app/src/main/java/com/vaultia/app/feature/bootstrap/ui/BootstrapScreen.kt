package com.vaultia.app.feature.bootstrap.ui

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Non-sensitive bootstrap UI for Vaultia.
 *
 * This screen is intentionally simple and must not collect,
 * store, process, or display vault content.
 */
internal object BootstrapScreen {

    fun create(context: Context): View {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)

            addView(
                title(context),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ),
            )

            addView(
                subtitle(context),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ),
            )

            addView(
                warning(context),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
    }

    private fun title(context: Context): TextView {
        return TextView(context).apply {
            text = "Vaultia"
            gravity = Gravity.CENTER
            textSize = 32f
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun subtitle(context: Context): TextView {
        return TextView(context).apply {
            text = "Cofre local para dados sensíveis"
            gravity = Gravity.CENTER
            textSize = 18f
            setPadding(0, 24, 0, 0)
        }
    }

    private fun warning(context: Context): TextView {
        return TextView(context).apply {
            text = "Este app é local-first. Sem nuvem, sem conta e sem recuperação remota."
            gravity = Gravity.CENTER
            textSize = 14f
            setPadding(0, 32, 0, 0)
        }
    }
}
