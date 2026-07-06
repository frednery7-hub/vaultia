# Storage Architecture

## Status

Architecture planned. No real storage implementation in this phase.

## Scope

This document defines the intended local storage architecture for Vaultia after the Android Foundation milestone `v0.8.0-foundation`.

Phase 41 does not implement persistence. It defines the boundary that future storage work must follow.

## Target Model

Vaultia will use a hybrid local storage model.

```text
Future SQLite or Room: technical metadata and safe indexes.
Future app private files: encrypted payloads and encrypted binary objects.
Future Android Keystore: optional auxiliary protection for vault material.
Master password: never persisted.
Derived key: never persisted.
Plaintext: never persisted.
```

## Logical Components

### Vault Header Store

Stores public vault header data needed to derive and validate the local vault format.

Allowed future content:

- format version;
- KDF algorithm;
- KDF version;
- KDF parameters;
- public salt;
- creation timestamp.

Not allowed:

- master password;
- derived key;
- vault key in cleartext;
- decrypted payload.

### Metadata Store

Stores only technical metadata required to locate encrypted items.

Allowed future content:

- technical item ID;
- item type;
- technical timestamps;
- format version;
- encrypted payload pointer.

Real titles, usernames, URLs, notes, passwords, documents and photos must not be stored as clear metadata.

### Encrypted Payload Store

Stores serialized encrypted payloads only.

Payload storage may later use app private files or another private local mechanism, but must never require internet, backend or cloud sync.

### Binary Object Store

Stores encrypted binary content for future photo and document items.

Filenames must be technical identifiers only. User filenames must be encrypted inside payload content.

## Phase 41 Non-Goals

- no Room dependency;
- no SQLite implementation;
- no DataStore;
- no SharedPreferences;
- no File I/O implementation;
- no Keystore;
- no biometric unlock;
- no backup or export;
- no decrypt-to-UI connection;
- no real secret persistence.
