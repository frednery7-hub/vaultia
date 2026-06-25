package com.vaultia.app

import android.app.Activity
import android.os.Bundle
import com.vaultia.app.core.session.InMemorySessionManager
import com.vaultia.app.feature.bootstrap.ui.BootstrapScreen

class MainActivity : Activity() {
    private val sessionManager = InMemorySessionManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(BootstrapScreen.create(this, sessionManager))
    }
}
