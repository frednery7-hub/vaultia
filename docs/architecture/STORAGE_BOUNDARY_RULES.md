# Storage Boundary Rules

## Status

Boundary rules planned. No real storage implementation in this phase.

## Purpose

This document defines how future storage code must interact with crypto, UI, logs and platform APIs.

## Rule 1 — Storage Must Not Derive Keys

Storage code must not call KDF implementation directly.

Key derivation remains in the crypto/auth boundary.

## Rule 2 — Storage Must Not Decrypt for UI

Storage code may return encrypted records or technical metadata. It must not render, format or expose decrypted values to UI components.

## Rule 3 — UI Must Not Access Files or Databases Directly

Future UI must go through application use cases or repositories. UI code must not directly read database rows, files, serialized payloads, nonce, tag or ciphertext.

## Rule 4 — Logs Must Stay Redacted

Future storage logs, if any, must never include secret material, plaintext, keys, serialized encrypted payloads, user titles, usernames, URLs, filenames, document contents or photo contents.

## Rule 5 — File Names Must Be Technical

Future payload and binary filenames must be random technical identifiers. User-provided names must be encrypted inside payload content.

## Rule 6 — No Network Dependency

Storage must remain offline-only. It must not depend on backend, cloud sync, HTTP clients, remote config, analytics or external services.

## Rule 7 — Transactional Integrity Required Later

Future implementation must define atomic write behavior for item metadata and encrypted payload files. A metadata row must not point to a missing encrypted payload, and an orphan encrypted payload must be handled by a recovery or cleanup rule.

## Rule 8 — No Storage Before Audit

Any future implementation adding Room, SQLite, DataStore, SharedPreferences or File I/O must have a phase plan, architecture test and audit evidence before release.

## Phase 41 Confirmation

Phase 41 creates documentation only.

It does not add:

- Room;
- SQLite;
- DataStore;
- SharedPreferences;
- File I/O;
- Keystore;
- biometric unlock;
- backup/export;
- UI connected to decrypt;
- internet permission.
