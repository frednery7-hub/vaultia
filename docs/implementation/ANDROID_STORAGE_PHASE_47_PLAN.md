# Android Storage — Phase 47 Plan

## Status

Planned.

## Nome

Storage Integration Boundary

## Contexto

A Phase 46 criou um composition root específico para o track de storage, expondo `StorageService` através de `StorageContainer`.

A Phase 47 prepara a integração desse container de storage com o container principal do aplicativo, mantendo o limite seguro entre infraestrutura de app, UI, storage, crypto e persistência real.

Esta fase ainda não implementa storage persistente, UI real de vault, decrypt para tela, Keystore, biometria, backup/export ou qualquer permissão de rede.

---

## Objetivo

Definir e implementar o boundary de integração entre o `VaultiaAppContainer` existente e o `StorageContainer` criado na Phase 46.

A fase deve permitir que o app container possua uma dependência de storage tecnicamente montada, sem que isso habilite leitura/escrita persistente ou exposição de dados sensíveis na UI.

---

## Arquivos Potencialmente Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/app/VaultiaAppContainer.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/composition/StorageCompositionRoot.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/app/VaultiaAppContainerStorageIntegrationTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/app/VaultiaAppContainerArchitectureTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_47_PLAN.md
docs/implementation/ANDROID_STORAGE_PHASE_47_DONE.md
```

---

## Regras de Segurança

- O app container pode receber `StorageContainer` ou `StorageService` como dependência técnica.
- A integração deve permanecer in-memory.
- A integração não pode criar storage persistente.
- A integração não pode criar Room, SQLite, DataStore ou SharedPreferences.
- A integração não pode ler ou escrever arquivos reais.
- A integração não pode conectar storage à UI real.
- A integração não pode renderizar registros reais de vault.
- A integração não pode descriptografar payloads.
- A integração não pode derivar chaves.
- A integração não pode usar Keystore.
- A integração não pode usar biometria.
- A integração não pode habilitar backup/export.
- A integração não pode adicionar `android.permission.INTERNET`.

A fase é apenas de wiring controlado, não de funcionalidade final de cofre.

---

## Critérios de Aceite

- Plano da Phase 47 criado.
- Estado atual do app container auditado antes da alteração.
- Storage integrado ao app container de forma explícita.
- `StorageService` acessível a partir do container principal ou por subcontainer.
- Teste de integração do container criado.
- Teste arquitetural criado ou atualizado para impedir vazamento de UI, Android APIs, persistência e crypto indevida.
- Nenhuma UI real de vault conectada.
- Nenhum storage persistente adicionado.
- Nenhuma permissão Android adicionada.
- Testes unitários passam.
- Check Android passa.
- APK continua sem `android.permission.INTERNET`.
