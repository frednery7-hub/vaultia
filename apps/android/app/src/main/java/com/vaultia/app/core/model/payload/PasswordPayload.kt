package com.vaultia.app.core.model.payload

data class PasswordPayload(
    val username: String = "",
    val passwordValue: String = "",
    val url: String = "",
    val additionalNotes: String = "",
    override val version: Int = 1
) : VaultItemPayload {
    init {
        require(username.length <= 255) { "Username exceeds maximum length of 255 characters." }
        require(passwordValue.length <= 1024) { "Password exceeds maximum length of 1024 characters." }
        require(url.length <= 2048) { "URL exceeds maximum length of 2048 characters." }
        require(additionalNotes.length <= 100_000) { "Notes exceed maximum length of 100,000 characters." }
    }
}
