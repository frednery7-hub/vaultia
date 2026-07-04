# Android Foundation — Phase 30 Plan

## Nome

Encryption Contract and AES-GCM Boundary

## Objetivo

Criar contratos de criptografia autenticada para payloads do vault, definindo plaintext, ciphertext, nonce, authentication tag, erros tipados e interface de encrypt/decrypt, preparando o boundary para AES-GCM sem criar storage real, sem UI, sem Android Keystore e sem persistência.

A Phase 30 não transforma o Vaultia em cofre persistente. Ela cria a camada contratual e testável para criptografia autenticada antes de qualquer escrita em disco.

---

## Contexto Técnico

Até a Phase 29, o Vaultia consegue criar um draft de vault em memória com senha mestra validada, salt gerado por CSPRNG, KDF Argon2id, VaultHeader e header serializado.

Ainda falta a camada que cifra payloads reais do vault.

A Phase 30 deve introduzir essa camada de forma isolada, auditável e sem storage.

Dependências conceituais anteriores:

```text
Phase 23 — KDF Contract and Safe Models
Phase 24 — Argon2id KDF Implementation
Phase 25 — KDF Profiles and Public Vault Header Contract
Phase 27 — Vault Creation Draft Without Persistence
Phase 28 — Secure Salt Generation Contract
Phase 29 — Vault Creation With Generated Salt
```

---

## Decisão Arquitetural

A criptografia autenticada será isolada em um pacote próprio, separado de vault creation, storage, UI e Keystore.

Pacote planejado:

```text
com.vaultia.app.core.crypto.encryption
```

A interface deve operar sobre bytes e retornar resultados tipados, sem lançar exceção como caminho normal.

A implementação concreta planejada será AES-GCM usando APIs criptográficas padrão da JVM/Android, mas ainda sem Android Keystore.

A chave recebida nessa fase será um `ByteArray` em memória, vindo de testes/fakes ou de um resultado KDF futuro. Nenhuma chave será persistida.

---

## Arquivos Planejados

### Main

```text
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/AuthenticatedCipher.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/EncryptionError.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/EncryptionResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/DecryptionResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/EncryptedPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/PlaintextPayload.kt
apps/android/app/src/main/java/com/vaultia/app/core/crypto/encryption/AesGcmAuthenticatedCipher.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/EncryptedPayloadTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/PlaintextPayloadTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/AesGcmAuthenticatedCipherTest.kt
apps/android/app/src/test/java/com/vaultia/app/core/crypto/encryption/EncryptionArchitectureTest.kt
```

---

## Contratos Planejados

### AuthenticatedCipher

Interface de criptografia autenticada.

Métodos planejados:

```text
encrypt(key: ByteArray, plaintext: PlaintextPayload): EncryptionResult
decrypt(key: ByteArray, encryptedPayload: EncryptedPayload): DecryptionResult
```

Regras:

- chave deve ter tamanho válido para AES: 16, 24 ou 32 bytes;
- v1 deve usar chave de 32 bytes, compatível com o KDF atual;
- plaintext não deve ser vazio;
- resultado deve usar cópia defensiva;
- falhas devem ser tipadas;
- não deve logar plaintext, ciphertext, nonce, tag ou chave.

### PlaintextPayload

Modelo de payload claro em memória.

Regras:

- recebe bytes;
- rejeita payload vazio;
- protege conteúdo com cópia defensiva;
- não persiste conteúdo.

### EncryptedPayload

Modelo de payload cifrado autenticado.

Campos planejados:

```text
nonce: ByteArray
ciphertext: ByteArray
authenticationTag: ByteArray
```

Regras:

- nonce recomendado para AES-GCM: 12 bytes;
- tag recomendada: 16 bytes / 128 bits;
- ciphertext não deve ser vazio;
- todos os arrays devem usar cópia defensiva;
- não deve conter plaintext.

### EncryptionError

Erros planejados:

```text
InvalidKey
InvalidPlaintext
InvalidEncryptedPayload
EncryptionFailed
AuthenticationFailed
DecryptionFailed
NonceGenerationFailed
```

### AesGcmAuthenticatedCipher

Implementação concreta planejada.

Regras:

- usar AES/GCM/NoPadding;
- gerar nonce novo por encrypt;
- usar nonce de 12 bytes;
- usar tag de 128 bits;
- separar ciphertext e authentication tag no modelo;
- falha de autenticação deve retornar erro tipado;
- não usar Android Keystore nesta fase;
- não usar storage real nesta fase.

---

## Requisitos de Segurança

- Gerar nonce novo por operação de encryption.
- Não reutilizar nonce com a mesma chave.
- Não aceitar key vazia.
- Não aceitar plaintext vazio.
- Não aceitar encrypted payload inválido.
- Não expor arrays internos por referência.
- Não logar key.
- Não logar plaintext.
- Não logar ciphertext.
- Não logar nonce.
- Não logar authentication tag.
- Não persistir key.
- Não persistir plaintext.
- Não persistir ciphertext.
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
- Não implementar Android Keystore.
- Não implementar biometria.

A limpeza determinística de memória em JVM/Android continua fora do escopo desta fase.

---

## Escopo

Esta fase cria:

- contrato de criptografia autenticada;
- modelos seguros de plaintext e encrypted payload;
- erros tipados de encryption/decryption;
- implementação AES-GCM isolada;
- geração de nonce em memória;
- testes de round-trip encrypt/decrypt;
- testes de dados adulterados;
- testes de nonce/tag inválidos;
- testes de cópia defensiva;
- testes de arquitetura;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- storage real;
- escrita em disco;
- leitura de disco;
- criação final de vault persistido;
- integração com VaultCreationService;
- criptografia de itens reais do vault;
- serialização final de payload cifrado em arquivo;
- UI;
- onboarding;
- login real;
- desbloqueio real;
- Android Keystore;
- biometria;
- backup/export;
- modo discreto/decoy vault;
- alteração no roadmap formal.

---

## Testes Planejados

Cobertura mínima:

- PlaintextPayload rejeita bytes vazios.
- PlaintextPayload protege bytes com cópia defensiva.
- EncryptedPayload rejeita nonce inválido.
- EncryptedPayload rejeita ciphertext vazio.
- EncryptedPayload rejeita tag inválida.
- EncryptedPayload protege arrays com cópia defensiva.
- AES-GCM rejeita chave inválida.
- AES-GCM encrypt gera nonce de 12 bytes.
- AES-GCM encrypt gera tag de 16 bytes.
- AES-GCM encrypt não retorna plaintext.
- AES-GCM decrypt recupera plaintext original.
- AES-GCM decrypt falha com ciphertext adulterado.
- AES-GCM decrypt falha com tag adulterada.
- AES-GCM decrypt falha com nonce adulterado.
- Duas chamadas de encrypt com mesma chave e plaintext geram nonces diferentes.
- Arquitetura não importa Android UI.
- Arquitetura não importa storage.
- Arquitetura não importa Room, DataStore, SQLite ou SharedPreferences.
- Arquitetura não importa java.io ou java.nio.
- Arquitetura não usa Android Keystore.
- Arquitetura não usa logs.

---

## Critérios de Aceite

- `AuthenticatedCipher.kt` criado.
- `PlaintextPayload.kt` criado.
- `EncryptedPayload.kt` criado.
- `EncryptionError.kt` criado.
- `EncryptionResult.kt` criado.
- `DecryptionResult.kt` criado.
- `AesGcmAuthenticatedCipher.kt` criado.
- AES-GCM usa nonce de 12 bytes.
- AES-GCM usa tag de 128 bits.
- Encrypt/decrypt round-trip funciona.
- Dados adulterados falham.
- Arrays usam cópia defensiva.
- Nenhum storage real é criado.
- Nenhuma escrita em disco é criada.
- Nenhuma leitura de disco é criada.
- Nenhuma UI é alterada.
- Nenhum Android Keystore é criado.
- Nenhuma permissão Android é adicionada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
