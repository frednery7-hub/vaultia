# Android Foundation — Phase 29 Plan

## Nome

Vault Creation With Generated Salt

## Objetivo

Integrar o contrato de geração segura de salt ao fluxo de criação em memória do vault, permitindo criar um `CreatedVaultDraft` sem receber salt externo, ainda sem storage real, sem UI, sem AES-GCM, sem Android Keystore e sem persistência.

A Phase 29 conecta a Phase 27 e a Phase 28: o vault continua sendo criado apenas em memória, mas agora o salt pode ser gerado pelo próprio fluxo controlado.

---

## Contexto Técnico

A Phase 27 criou `VaultCreationService`, mas o fluxo exige salt externo.

A Phase 28 criou `SaltGenerator` e `SecureRandomSaltGenerator`, permitindo geração de salt recomendado de 32 bytes por CSPRNG.

A Phase 29 deve integrar essas peças mantendo o limite de segurança atual: criação em memória, sem armazenamento persistente e sem UI.

Dependências conceituais anteriores:

```text
Phase 21 — Master Password Policy Contract
Phase 24 — Argon2id KDF Implementation
Phase 25 — KDF Profiles and Public Vault Header Contract
Phase 26 — Vault Header Serialization Contract
Phase 27 — Vault Creation Draft Without Persistence
Phase 28 — Secure Salt Generation Contract
```

---

## Decisão Arquitetural

O `VaultCreationService` deve passar a receber um `SaltGenerator` opcional/injetável para criação com salt gerado.

O método existente com salt externo deve ser preservado, porque ele é útil para testes determinísticos, auditoria e cenários futuros de importação/migração controlada.

A integração com salt gerado deve ser adicionada como novo fluxo, sem quebrar o fluxo atual.

---

## Arquivos Planejados

### Main

Arquivos existentes a modificar:

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationService.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationError.kt
```

Arquivos novos, se necessário:

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/vault/VaultCreationWithoutExternalSaltRequest.kt
```

### Testes

Arquivos existentes a modificar:

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultCreationServiceTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultCreationArchitectureTest.kt
```

Arquivo novo, se necessário:

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/vault/VaultCreationWithGeneratedSaltTest.kt
```

---

## Fluxo Planejado

Novo fluxo com salt gerado:

```text
1. Receber senha mestra e timestamp
2. Validar senha mestra com MasterPasswordPolicy
3. Rejeitar senha inválida
4. Solicitar salt recomendado ao SaltGenerator
5. Rejeitar falha de geração de salt
6. Montar VaultCreationRequest com salt gerado
7. Reusar fluxo existente de criação em memória
8. Criar VaultHeader em memória
9. Derivar chave via KdfDeriver
10. Serializar VaultHeader
11. Retornar CreatedVaultDraft em memória
```

Nenhuma etapa deve escrever em disco.

---

## Contratos Planejados

### VaultCreationWithoutExternalSaltRequest

Entrada do novo fluxo sem salt externo.

Campos planejados:

```text
masterPassword: String
createdAtEpochMillis: Long
```

Regras:

- `masterPassword` não deve ser persistida.
- `createdAtEpochMillis` deve ser positivo.
- salt deve ser gerado pelo `SaltGenerator`, não recebido de fora.

### VaultCreationService

O service deve suportar dois fluxos:

```text
create(request: VaultCreationRequest): VaultCreationResult
createWithGeneratedSalt(request: VaultCreationWithoutExternalSaltRequest): VaultCreationResult
```

O primeiro fluxo preserva salt externo para testes/controlabilidade.

O segundo fluxo gera salt recomendado de 32 bytes usando `SaltGenerator.generateRecommendedSalt()`.

### VaultCreationError

Novo erro planejado:

```text
SaltGenerationFailed
```

Esse erro deve ser retornado quando o `SaltGenerator` retorna `Failure`.

---

## Requisitos de Segurança

- Não persistir senha mestra.
- Não persistir chave derivada.
- Não persistir salt.
- Não logar senha.
- Não logar chave derivada.
- Não logar salt.
- Não criar arquivo.
- Não escrever em disco.
- Não ler disco.
- Não criar storage real.
- Não criar SQLite.
- Não criar Room.
- Não criar DataStore.
- Não criar SharedPreferences.
- Não criar UI.
- Não adicionar permissão Android.
- Não adicionar `android.permission.INTERNET`.
- Não implementar AES-GCM.
- Não implementar Android Keystore.
- Não implementar biometria.

A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase.

---

## Escopo

Esta fase cria:

- request de criação sem salt externo;
- integração do `SaltGenerator` ao `VaultCreationService`;
- novo fluxo de criação com salt gerado;
- erro tipado para falha de geração de salt;
- testes de sucesso do fluxo com salt gerado;
- testes de falha de geração de salt;
- testes de preservação do fluxo com salt externo;
- testes de isolamento/arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- criação final de vault persistido;
- UI;
- onboarding;
- login real;
- desbloqueio real;
- AES-GCM;
- envelope encryption;
- payload cifrado;
- nonce real;
- tag de autenticação;
- Android Keystore;
- biometria;
- backup/export;
- modo discreto/decoy vault;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- request sem salt externo rejeita timestamp inválido.
- service com salt gerado rejeita senha mestra inválida.
- service com salt gerado chama `SaltGenerator.generateRecommendedSalt()`.
- service com salt gerado usa salt retornado pelo gerador.
- service com salt gerado cria `VaultHeader` válido.
- service com salt gerado deriva KDF por contrato.
- service com salt gerado serializa header.
- service com salt gerado retorna `CreatedVaultDraft`.
- falha do SaltGenerator retorna `SaltGenerationFailed`.
- fluxo com salt externo continua funcionando.
- arquitetura não importa Android UI.
- arquitetura não importa storage.
- arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- arquitetura não importa java.io ou java.nio.
- arquitetura não usa SecureRandom diretamente no pacote vault.
- arquitetura não usa Cipher, SecretKey ou KeyStore.
- arquitetura não usa logs.

---

## Critérios de Aceite

- `VaultCreationWithoutExternalSaltRequest.kt` criado se necessário.
- `VaultCreationService` integra `SaltGenerator`.
- Fluxo com salt externo continua funcionando.
- Fluxo com salt gerado funciona.
- Falha de geração de salt é mapeada para erro tipado.
- Header é criado em memória com salt gerado.
- Header é serializado em memória.
- KDF é chamado por contrato.
- Nenhum storage real é criado.
- Nenhuma escrita em disco é criada.
- Nenhuma leitura de disco é criada.
- Nenhuma UI é alterada.
- Nenhum AES-GCM é criado.
- Nenhum Android Keystore é criado.
- Nenhuma permissão Android é adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
