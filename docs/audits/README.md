# Vaultia — Audit Evidence

This directory contains public-safe audit evidence for the Vaultia project.

Vaultia is a local-first Android vault project focused on secure architecture, no-network design, password policy, KDF evaluation, and audit-driven development.

## Audit Structure

```text
docs/audits/
├── foundation-50/
│   └── 50% foundation audit evidence
├── phase-22/
│   └── Argon2id / KDF dependency evaluation audit
└── pre-github-upload/
    └── public repository upload security audit
```

## Scope

These files document security and engineering checks performed during development. They are structured summaries, not raw logs.

Audit files committed here must not contain:

- secrets;
- private keys;
- API tokens;
- local machine paths;
- APK/AAB binaries;
- Gradle logs;
- Logcat dumps;
- personal environment details.

## Public Safety Rule

Only sanitized, structured audit summaries should be committed here.

Raw evidence, large logs, APKs, AABs, emulator files, local Gradle caches, and temporary build output must remain outside Git.
