package com.vaultia.app.core.storage

/**
 * Storage boundary for Vaultia.
 *
 * Future responsibilities:
 * - encrypted local metadata storage;
 * - encrypted file references;
 * - secure backup import/export boundary.
 *
 * This package must not store plaintext vault content.
 */
internal object StoragePackageInfo
