package com.vaultia.app.core.security

/**
 * Security boundary for Vaultia.
 *
 * Future responsibilities:
 * - master password validation boundary;
 * - key derivation boundary;
 * - cryptographic service contracts;
 * - secure session state.
 *
 * This package must not implement homemade cryptography.
 */
internal object SecurityPackageInfo
