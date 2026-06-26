package com.vaultia.app

import android.app.Activity
import android.os.Bundle
import com.vaultia.app.core.app.VaultiaAppContainer
import com.vaultia.app.feature.bootstrap.ui.BootstrapScreen

class MainActivity : Activity() {
    private val appContainer = VaultiaAppContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            BootstrapScreen.create(
                context = this,
                sessionManager = appContainer.sessionManager,
                vaultRepository = appContainer.vaultRepository,
                vaultNavigator = appContainer.vaultNavigator,
            ),
        )
    }
}