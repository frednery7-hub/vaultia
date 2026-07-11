package com.vaultia.app.core.model.payload

data class NotePayload(
    override val title: String = "",
    val text: String = "",
    override val version: Int = 1
) : VaultItemPayload {
    init {
        require(title.length <= 255) { "Title exceeds maximum length." }
        require(text.length <= 200_000) { "Note exceeds maximum length." }
    }
}
