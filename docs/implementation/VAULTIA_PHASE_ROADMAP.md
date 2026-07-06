# Vaultia Phase Roadmap

## Escala de progresso

O MVP local-first do Vaultia está organizado em 40 fases.

Cada fase concluída representa 2.5% do progresso total.

---

## Progresso atual

Após a Phase 40:

```text
40 / 40 fases concluídas
0 fases restantes
Progresso: 100%
```

Status atual:

```text
Android Foundation concluída.
Foundation Final Audit Gate em validação final.
```

---

## 42.5% Checkpoint — Early Foundation Progress

Após a Phase 17:

```text
17 / 40 = 42.5%
```

Esse checkpoint registrou a evolução inicial da fundação Android local-first, antes da consolidação dos contratos criptográficos principais.

---

## 75% Checkpoint — Foundation Security Audit

Status: aprovado.

```text
30 / 40 fases concluídas
10 fases restantes
Progresso: 75%
```

Checkpoint executado após Phase 30 — Encryption Contract and AES-GCM Boundary.

Resumo do gate:

- política de senha mestra criada;
- KDF Argon2id avaliado e implementado;
- perfis KDF definidos;
- VaultHeader criado e serializável;
- criação de vault draft em memória criada;
- geração segura de salt criada;
- criação de vault draft com salt gerado criada;
- boundary AES-GCM criado;
- Manifest e APK continuam sem `android.permission.INTERNET`;
- storage real ainda não foi criado;
- UI real ainda não foi criada;
- Android Keystore ainda não foi implementado;
- biometria ainda não foi implementada.

Próxima fase recomendada naquele momento: Phase 31 — Vault Payload Encryption Draft.

---

## 100% Checkpoint — Android Foundation Final Audit Gate

Status: aprovado pela primeira auditoria e em consolidação documental final.

```text
40 / 40 fases concluídas
0 fases restantes
Progresso: 100%
```

Checkpoint executado após Phase 40 — Local Threat Detection Contract.

Resumo da fundação concluída:

- política de senha mestra criada;
- KDF Argon2id avaliado e implementado;
- contratos KDF criados;
- perfis KDF criados;
- VaultHeader criado;
- serializer de VaultHeader criado;
- criação de vault draft em memória criada;
- geração segura de salt criada;
- criação de vault draft com salt gerado criada;
- boundary AES-GCM criado;
- payload encryption draft criado;
- payload decryption draft criado;
- payload serialization contract criado;
- encrypted vault item draft criado;
- vault item serialization contract criado;
- arquitetura segura de frontend documentada;
- contratos seguros de estado de UI criados;
- contratos de screen guard criados;
- contratos de reautenticação e ações críticas criados;
- contratos de local threat detection criados;
- decisão formal de não incluir plausible deniability na v1 registrada;
- Manifest e APK continuam sem `android.permission.INTERNET`;
- backend e cloud continuam fora do escopo;
- storage real ainda não foi criado;
- UI real de cofre ainda não foi criada;
- decrypt ainda não foi conectado à UI;
- Android Keystore ainda não foi implementado;
- biometria ainda não foi implementada;
- backup/export real ainda não foi implementado.

---

## Observação de Escopo

Foundation concluída não significa produto final completo.

A Android Foundation entrega a base arquitetural, criptográfica e de segurança para evolução do Vaultia.

Ainda não estão implementados:

- storage real de vault;
- fluxo real de unlock;
- UI real de cofre;
- integração real de `FLAG_SECURE`;
- Android Keystore;
- biometria;
- backup/export real;
- detecção Android real de root, debugger, emulator, hooking ou tampering;
- APK release assinado para distribuição.
