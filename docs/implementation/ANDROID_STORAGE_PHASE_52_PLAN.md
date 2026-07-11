# Android Storage — Phase 52 Plan

## Status

Planned.

## Nome

File Encrypted Payload Repository Implementation

## Contexto

Na Phase 51, desenhamos a interface `EncryptedPayloadRepository` e implementamos a versão `InMemory`. O pacote `core.storage.payload` provou ser o local perfeito e isolado na arquitetura para gerenciar o transporte desses bytes.

Agora, na **Phase 52**, vamos construir a classe que efetivamente acessa o sistema de arquivos do Android, concretizando a metade "File I/O" do nosso Modelo de Armazenamento Híbrido (SQLite gerencia os IDs, File System guarda os arquivos gigantes).

## Objetivo

Implementar o `FileEncryptedPayloadRepository` responsável por gravar e ler os bytes reais (`ByteArray`) no armazenamento interno restrito (App-specific storage) do Android.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/local/payload/FileEncryptedPayloadRepository.kt
```

### Testes

```text
apps/android/app/src/androidTest/java/com/vaultia/app/core/storage/local/payload/FileEncryptedPayloadRepositoryTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_52_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Internal Storage Exclusivo**: O repositório usará estritamente um subdiretório dentro do diretório interno seguro do Android (ex: `Context.filesDir/vaultia_payloads`). Nunca usaremos diretórios públicos (Downloads, Documents). Os arquivos viverão na Sandbox criptografada de fábrica do Android.
2. **Defesa contra Path Traversal**: Como a classe `EncryptedPayloadPointer` já é blindada por Regex desde a Fundação (rejeitando barras e `..`), a leitura do diretório estará matematicamente presa na pasta definida.
3. **Escrita Bruta de Bytes**: O repositório apenas chama `File.writeBytes()` e `File.readBytes()`. A responsabilidade de garantir que esses bytes estão cifrados é exclusiva do Domínio (AES-GCM).

## Critérios de Aceite

- Plano Phase 52 registrado.
- Classe `FileEncryptedPayloadRepository` implementada no pacote permissivo `core.storage.local.payload`, recebendo o diretório base.
- Teste instrumentado (`androidTest`) executado, provando que um arquivo é fisicamente criado e lido no disco do emulador, e destruído no fim do teste.
