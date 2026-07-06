package com.vaultia.app.core.ui.screen

enum class SecureScreenEvent {
    APP_FOREGROUND,
    APP_BACKGROUND,
    APP_PAUSE,
    APP_STOP,
    SENSITIVE_SCREEN_OPENED,
    SENSITIVE_SCREEN_CLOSED,
    SECRET_REVEALED,
    SECRET_REDACTED,
    LOCK_REQUESTED,
}
