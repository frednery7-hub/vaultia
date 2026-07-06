# Storage Data Classification

## Status

Classification planned. No real storage implementation in this phase.

## Purpose

This document defines what future Vaultia storage may store in clear technical metadata, what must be encrypted, and what must never be persisted.

## Class A — Public Technical Header Data

May be stored in clear form because it is required to open and validate the vault format.

Allowed examples:

- vault format version;
- KDF algorithm;
- KDF version;
- KDF memory cost;
- KDF iterations;
- KDF parallelism;
- output length;
- salt;
- created timestamp.

## Class B — Safe Technical Item Metadata

May be stored in clear form only when the value is technical and non-user-secret.

Allowed examples:

- random technical item ID;
- item type;
- created timestamp;
- updated timestamp;
- payload format version;
- encrypted payload pointer.

Not allowed as clear metadata:

- real item title;
- account username;
- service URL when sensitive;
- document filename chosen by the user;
- photo filename chosen by the user;
- search text derived from user secrets.

## Class C — Must Be Encrypted

Must exist only inside authenticated encrypted payloads.

- password;
- note body;
- username;
- token;
- seed phrase;
- recovery code;
- private URL;
- real title;
- document bytes;
- photo bytes;
- user-provided filenames;
- any content entered or imported by the user.

## Class D — Never Persist

Must never be written to disk, logs, analytics, crash reports, backups or metadata indexes.

- master password;
- password field value;
- derived key;
- vault key in cleartext;
- decrypted plaintext;
- revealed UI content;
- clipboard content;
- decrypted document bytes;
- decrypted photo bytes;
- stack trace containing sensitive values.

## Search and Indexing Rule

Search indexes must not expose user secrets. Any future search feature must be designed separately and audited before implementation.
