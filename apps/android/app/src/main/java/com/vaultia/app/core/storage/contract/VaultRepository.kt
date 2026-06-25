package com.vaultia.app.core.storage.contract

/**
 * Contract boundary for future vault item persistence.
 *
 * This repository must not expose plaintext secrets across layers
 * without explicit security review.
 *
 * No implementation is allowed in Foundation Phase 5.
 */
internal interface VaultRepository
