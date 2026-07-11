package com.vaultia.app.core.model.payload

data class PasswordPayload(
    override val title: String = "",
    val username: String = "",
    val passwordValue: String = "",
    val url: String = "",
    val additionalNotes: String = "",
    override val version: Int = 1
) : VaultItemPayload {
    init {
        require(title.length <= 255) { "Title exceeds maximum length." }
        require(username.length <= 255) { "Username exceeds maximum length." }
        require(passwordValue.length <= 1024) { "Password exceeds maximum length." }
        require(url.length <= 2048) { "URL exceeds maximum length." }
        require(additionalNotes.length <= 100_000) { "Notes exceed maximum length." }
    }
}
