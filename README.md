# Vaultia

Vaultia is a local-first Android vault project focused on secure architecture, no-network design, password policy, KDF evaluation, and audit-driven development.

The project is intentionally being built in security-first phases. Real secret storage is not enabled until the cryptographic foundation, storage model, and audit gates are completed.

---

## Current Status

```text
Project type:        Android local-first vault
Current phase:       Android Foundation completed — Phase 40 / 40
Security model:      No backend, no cloud sync, no INTERNET permission
KDF:                 Argon2id implemented through isolated contracts
Foundation progress: 100%
Real storage:        Not enabled yet
Real vault UI:       Not enabled yet
Encryption boundary: AES-GCM boundary implemented
Repository state:    Public-safe portfolio repository
```

Vaultia is not presented as a finished password manager yet. The current goal is to build and audit the security foundation before allowing real user secrets to be stored.

---

## Security Principles

- Local-first design.
- No backend in the current scope.
- No cloud synchronization.
- No account system.
- No remote password recovery.
- No `android.permission.INTERNET` permission.
- No real secrets stored before the crypto and storage layers are audited.
- Security documentation and implementation are developed through explicit audit gates.

---

## Implemented Foundation

- Android project foundation.
- No-network application boundary.
- Explicit `allowBackup=false` policy.
- Local vault session skeleton.
- Locked and unlocked UI states.
- Metadata-only vault item model.
- In-memory repository used only for development scaffolding.
- Master password policy contract.
- Argon2id KDF dependency evaluation.
- Instrumented benchmark for KDF candidate parameters.
- Argon2id KDF contract and implementation.
- KDF profiles and public VaultHeader contract.
- VaultHeader serialization contract.
- Vault creation draft without persistence.
- Secure salt generation contract.
- Vault creation draft with generated salt.
- AES-GCM authenticated encryption boundary.
- Vault payload encryption draft.
- Vault payload decryption draft.
- Vault payload serialization contract.
- Encrypted vault item draft contract.
- Vault item serialization contract.
- Secure frontend architecture documentation.
- Secure UI state contracts.
- Secure screen guard contracts.
- Authentication friction and critical action contracts.
- Local threat detection contracts.
- Public-safe audit evidence organization.

---

## Intentionally Not Implemented Yet

The following capabilities are intentionally out of scope at the current phase:

- Real production unlock flow.
- Persistent vault storage.
- Real password, note, photo, or document storage.
- Real vault UI connected to decryption.
- Android Keystore integration.
- Biometric unlock.
- Backup/export flow.
- Real Android `FLAG_SECURE` integration.
- Real Android root, debugger, emulator, hooking, tampering, overlay, or accessibility detection.
- Internet access.
- Cloud sync.
- Backend integration.
- Duress password.
- Hidden vault.
- Decoy vault.
- Plausible deniability.

This is deliberate. Vaultia is being built as a security-sensitive product, not as a UI-first prototype that stores data before the protection model is ready.

---

## KDF and Encryption Foundation

Phase 22 evaluates `com.lambdapioneer.argon2kt:argon2kt:1.6.0` as the Argon2id candidate for future master-key derivation.

Evaluated candidate parameters:

```text
FAST:         32 MiB memory, 2 iterations, parallelism 1, 32-byte output
CONSERVATIVE: 64 MiB memory, 2 iterations, parallelism 1, 32-byte output
```

Later phases created the KDF contract, Argon2id implementation, KDF profiles, VaultHeader model, and VaultHeader serialization.

AES-GCM authenticated encryption boundaries were also created, followed by payload encryption, payload decryption, payload serialization, encrypted item draft, and item serialization contracts.

Final production parameters still require physical-device validation before a production release.

See:

- `docs/implementation/ANDROID_FOUNDATION_PHASE_22_PLAN.md`
- `docs/implementation/ANDROID_FOUNDATION_PHASE_22_DONE.md`
- `docs/audits/phase-22/AUDIT_PHASE_22_INDEPENDENT.md`

---

## Audit Evidence

Public-safe audit evidence is organized under:

```text
docs/audits/
├── README.md
├── foundation-50/
├── phase-22/
└── pre-github-upload/
```

The audit directory contains structured summaries only. Raw logs, APKs, AABs, local build output, emulator files, and environment-specific artifacts are intentionally excluded from Git.

---

## Project Structure

```text
apps/android/              Android application
docs/SDD/                  security and design documentation
docs/implementation/       phase plans and completion reports
docs/audits/               public-safe audit evidence
scripts/                   validation and audit scripts
releases/android/          marker directory only; APKs are not committed
```

---

## Running Checks

From the repository root:

```bash
cd apps/android
./gradlew testDebugUnitTest
```

Android validation script:

```bash
./scripts/check-android.sh
```

The Android check script validates build execution, manifest policy, APK permission state, and local artifact hygiene.

---

## Public Repository Safety

Before public upload, the repository was checked for:

- versioned secrets;
- credentials;
- local absolute paths;
- APK/AAB binaries;
- raw logs;
- Android local configuration files;
- unintended Git remotes.

See:

- `docs/audits/pre-github-upload/PRE_GITHUB_UPLOAD_SECURITY_AUDIT.md`

---

## Roadmap Direction

Next engineering areas include:

- Android Foundation Final Audit report.
- Real storage architecture.
- Android Keystore evaluation.
- Real unlock flow.
- Real vault UI.
- Real `FLAG_SECURE` integration.
- Biometric convenience layer.
- Backup/export design.
- Native local threat detection implementation.
- Physical-device validation for KDF parameters and app behavior.

The roadmap remains milestone-based and is not updated after every small implementation step.

---

## License

This repository is published for portfolio and technical review purposes. Review the repository license before reuse.
