# Vaultia — Portfolio Summary

Vaultia is a local-first Android vault project focused on secure architecture, no-network design, password policy, Argon2id KDF evaluation, and audit-driven development.

The project is intentionally not presented as a finished password manager yet. Its current focus is building and auditing the security foundation before enabling real user-secret storage.

## Public Positioning

Vaultia demonstrates applied mobile security engineering through a staged Android implementation process. The project prioritizes security boundaries, auditability, and responsible product claims over premature feature delivery.

## Current Technical Highlights

- Android/Kotlin local-first application foundation.
- No backend and no cloud synchronization in the current scope.
- No `android.permission.INTERNET` permission.
- Explicit `allowBackup=false` policy.
- Master password policy contract.
- Metadata-only vault model during foundation phases.
- Argon2id KDF dependency evaluation using `lambdapioneer/argon2kt`.
- Instrumented benchmark for KDF candidate parameters.
- Public-safe audit evidence under `docs/audits/`.
- Pre-GitHub upload security audit before public publication.

## Security Boundaries

The project currently does not store real secrets. This is intentional.

The following features are not enabled yet:

- real password unlock flow;
- persistent vault storage;
- AES-GCM envelope encryption;
- Android Keystore integration;
- biometric unlock;
- backup/export flow;
- real password, note, photo, or document storage.

## Audit Evidence

Relevant public-safe audit files:

- `docs/audits/README.md`
- `docs/audits/foundation-50/AUDIT_50_PERCENT_DONE.md`
- `docs/audits/phase-22/AUDIT_PHASE_22_INDEPENDENT.md`
- `docs/audits/pre-github-upload/PRE_GITHUB_UPLOAD_SECURITY_AUDIT.md`

## LinkedIn Short Description

Vaultia is a local-first Android vault project focused on mobile security architecture. The current version does not store real secrets yet; instead, it demonstrates a security-first foundation with no network permission, explicit audit gates, master password policy design, and Argon2id KDF evaluation before enabling real encrypted storage.

## GitHub Link

https://github.com/frednery7-hub/vaultia
