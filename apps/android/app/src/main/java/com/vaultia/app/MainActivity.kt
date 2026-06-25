package com.vaultia.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Vaultia\nFundação Android Fase 1"
            textSize = 22f
            gravity = android.view.Gravity.CENTER
        }

        setContentView(textView)
    }
}
