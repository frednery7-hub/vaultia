# Android Vault Engine — Phase 55 Plan

## Status

Planned.

## Nome

Vault Item Payload Serialization Contract

## Contexto

Agora que temos as classes `PasswordPayload` e `NotePayload` garantindo a integridade dos dados na memória (Phase 54), precisamos de um mecanismo para converter essas estruturas em Arrays de Bytes (`ByteArray`). Esse `ByteArray` será o nosso `PlaintextPayload`, a matéria-prima exata que o `VaultPayloadEncryptionService` (AES-GCM) exige para gerar o `EncryptedPayload`.

Seguindo o padrão brilhante estabelecido previamente no `VaultItemSerializer`, não usaremos bibliotecas JSON de terceiros, garantindo uma arquitetura 100% limpa e com dependência zero.

## Objetivo

Criar o `VaultItemPayloadSerializer`, responsável por serializar e desserializar Payloads em um formato customizado baseado em texto (Key-Value) com codificação Base64 para suportar quebras de linha e caracteres especiais com segurança absoluta, gerando no final o `ByteArray` UTF-8.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/model/payload/VaultItemPayloadSerializationError.kt
apps/android/app/src/main/java/com/vaultia/app/core/model/payload/VaultItemPayloadSerializationResult.kt
apps/android/app/src/main/java/com/vaultia/app/core/model/payload/VaultItemPayloadSerializer.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/model/payload/VaultItemPayloadSerializerTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_VAULT_ENGINE_PHASE_55_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Codificação Base64 Segura**: Notas e Senhas frequentemente possuem quebras de linha (`\n`) ou símbolos estranhos. O Serializador converterá o valor de todos os campos em `java.util.Base64` antes de concatenar (Ex: `username=dXNlcg==\n`). Isso previne injeção de delimitadores e quebra do parser.
2. **Determinismo**: A serialização será estritamente determinística e usará "Magic Headers" e "Footers" (Ex: `VAULTIA_PAYLOAD_V1` e `END_VAULTIA_PAYLOAD`), para garantir que qualquer erro de corrupção de disco seja detectado antes de se tentar converter em objetos na UI.
3. **Padrão Android Foundation**: Usaremos Kotlin puro e as classes já fornecidas pela JVM (`java.util.Base64`), eliminando o erro de "Stub!" do `android.util.Base64` nos testes unitários e mantendo a compilação super veloz.

## Critérios de Aceite

- Plano Phase 55 criado.
- Classes de Erro e Resultado (`VaultItemPayloadSerializationError` e `Result`) desenhadas.
- Classe `VaultItemPayloadSerializer` criada com métodos `encode(VaultItemPayload): ByteArray` e `decode(ByteArray): VaultItemPayloadSerializationResult`.
- Testes unitários comprovando que quebras de linha gigantes em `NotePayload` são serializadas e recuperadas perfeitamente via Base64.
