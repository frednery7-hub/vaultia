package com.vaultia.app.core.security.contract

/**
 * Contract boundary for future Vaultia cryptographic operations.
 *
 * This interface intentionally does not define concrete algorithms,
 * key formats, or implementation details yet.
 *
 * Rules:
 * - no homemade cryptography;
 * - no plaintext vault data persistence;
 * - no implementation in Foundation Phase 5;
 * - implementation must be approved by the cryptography architecture document.
 */
internal interface CryptoService
