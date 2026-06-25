package com.vaultia.app.core.session.contract

/**
 * Contract boundary for future local vault session state.
 *
 * Future responsibilities:
 * - locked/unlocked state;
 * - local session timeout;
 * - memory-only unlock state;
 * - no remote session.
 *
 * No implementation is allowed in Foundation Phase 5.
 */
internal interface SessionManager
