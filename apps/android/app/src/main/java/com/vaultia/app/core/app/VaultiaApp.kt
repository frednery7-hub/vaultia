package com.vaultia.app.core.app

import android.app.Application

class VaultiaApp : Application() {
    lateinit var appContainer: VaultiaAppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = VaultiaAppContainer()
    }
}
