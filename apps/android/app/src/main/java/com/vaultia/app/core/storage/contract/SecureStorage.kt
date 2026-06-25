package com.vaultia.app.core.storage.contract

/**
 * Contract boundary for future encrypted local storage.
 *
 * This interface must only be implemented after the secure storage
 * architecture is approved.
 *
 * Rules:
 * - no plaintext secret persistence;
 * - no external network storage;
 * - no cloud sync;
 * - no implementation in Foundation Phase 5.
 */
internal interface SecureStorage
