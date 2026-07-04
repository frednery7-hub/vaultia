# Vaultia

Vaultia is a local-first Android vault project focused on secure architecture, no-network design, password policy, KDF evaluation, and audit-driven development.

The project is intentionally being built in security-first phases. Real secret storage is not enabled until the cryptographic foundation, storage model, and audit gates are completed.

---

## Current Status

```text
Project type:        Android local-first vault
Current phase:       Phase 22 completed
Security model:      No backend, no cloud sync, no INTERNET permission
KDF candidate:       Argon2id via lambdapioneer/argon2kt
Real storage:        Not enabled yet
Real encryption:     Not integrated yet
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
- Public-safe audit evidence organization.

---

## Intentionally Not Implemented Yet

The following capabilities are intentionally out of scope at the current phase:

- Real password unlock flow.
- Persistent vault storage.
- AES-GCM envelope encryption.
- Android Keystore integration.
- Biometric unlock.
- Backup/export flow.
- Real password, note, photo, or document storage.
- Internet access.
- Cloud sync.

This is deliberate. Vaultia is being built as a security-sensitive product, not as a UI-first prototype that stores data before the protection model is ready.

---

## KDF Evaluation

Phase 22 evaluates `com.lambdapioneer.argon2kt:argon2kt:1.6.0` as the Argon2id candidate for future master-key derivation.

Evaluated candidate parameters:

```text
FAST:         32 MiB memory, 2 iterations, parallelism 1, 32-byte output
CONSERVATIVE: 64 MiB memory, 2 iterations, parallelism 1, 32-byte output
```

The dependency is approved to continue as the official KDF candidate for the next phase, under an isolated contract. Final production parameters still require physical-device validation before release.

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

- KDF contract creation.
- Isolated KDF implementation.
- Error handling for native library loading.
- Future encrypted vault header design.
- Secure storage architecture.
- Envelope encryption design.
- Android Keystore evaluation.

The roadmap remains milestone-based and is not updated after every phase.

---

## License

This repository is published for portfolio and technical review purposes. Review the repository license before reuse.
