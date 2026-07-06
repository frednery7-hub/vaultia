# Android Storage — Phase 41 Plan

## Status

Planned.

## Nome

Storage Architecture Boundary

## Contexto

A Android Foundation foi concluída em `v0.8.0-foundation`, com 40 / 40 fases concluídas e Final Audit Gate aprovado sem ressalvas.

A Phase 41 abre o macrobloco de storage. Esta fase ainda não cria persistência real. O objetivo é definir arquitetura, classificação de dados e regras de boundary antes de qualquer Room, SQLite, DataStore, SharedPreferences, File I/O, Keystore, biometria, backup/export ou UI real.

---

## Objetivo

Definir a fronteira arquitetural do storage local do Vaultia.

A fase deve documentar:

- metadados técnicos permitidos;
- payloads que devem existir apenas criptografados;
- dados que nunca podem ser persistidos;
- separação entre storage, crypto e UI;
- modelo alvo de storage híbrido local;
- critérios para fases futuras de implementação.

---

## Decisão Arquitetural Inicial

Modelo alvo: storage híbrido local.

```text
SQLite/Room futuro: metadados técnicos e índices seguros.
App private files futuro: payloads criptografados e arquivos binários criptografados.
Keystore futuro: proteção auxiliar da Vault Key quando aplicável.
Master password: nunca persistida.
Derived key: nunca persistida.
Plaintext: nunca persistido.
```

---

## Arquivos Planejados

```text
docs/architecture/STORAGE_ARCHITECTURE.md
docs/architecture/STORAGE_DATA_CLASSIFICATION.md
docs/architecture/STORAGE_BOUNDARY_RULES.md
docs/implementation/ANDROID_STORAGE_PHASE_41_PLAN.md
```

Código planejado nesta fase: nenhum.

---

## Classificação de Dados

### Pode existir em metadados técnicos

- ID técnico aleatório;
- tipo do item;
- timestamps técnicos;
- versão do formato;
- ponteiro técnico para payload criptografado.

### Deve existir apenas criptografado

- título real do item;
- username;
- URL sensível;
- nota;
- senha;
- token;
- seed phrase;
- documento;
- foto;
- qualquer conteúdo escolhido pelo usuário.

### Nunca pode ser persistido

- senha mestra;
- derived key;
- vault key em claro;
- plaintext descriptografado;
- conteúdo revelado na UI;
- logs de secrets;
- stack traces contendo material sensível.

---

## Requisitos de Segurança

- Não adicionar `android.permission.INTERNET`.
- Não criar backend.
- Não criar cloud sync.
- Não criar storage real nesta fase.
- Não adicionar Room nesta fase.
- Não adicionar SQLite direto nesta fase.
- Não adicionar DataStore nesta fase.
- Não adicionar SharedPreferences nesta fase.
- Não adicionar File I/O real nesta fase.
- Não conectar crypto a storage nesta fase.
- Não conectar decrypt à UI.
- Não renderizar secrets reais.
- Não implementar Keystore.
- Não implementar biometria.
- Não implementar backup/export.
- Não implementar plausible deniability.

---

## Critérios de Aceite

- Plano da Phase 41 criado.
- Arquitetura de storage planejada.
- Classificação de dados planejada.
- Regras de boundary planejadas.
- Nenhuma implementação real de storage criada.
- Nenhuma permissão Android adicionada.
- Testes unitários continuam passando.
- Check Android continua passando.
- APK continua sem `android.permission.INTERNET`.
