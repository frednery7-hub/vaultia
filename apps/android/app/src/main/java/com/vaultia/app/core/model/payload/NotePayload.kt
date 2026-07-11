package com.vaultia.app.core.model.payload

data class NotePayload(
    val text: String = "",
    override val version: Int = 1
) : VaultItemPayload {
    init {
        require(text.length <= 200_000) { "Note text exceeds maximum length of 200,000 characters." }
    }
}
