# Android Storage — Phase 50 Plan

## Status

Planned.

## Nome

Room Storage Production Integration

## Contexto

As fases anteriores implementaram o Room (`Phase 48`) e o `RoomStorageRepository` (`Phase 49`) de forma perfeitamente isolada. Agora precisamos expor essa estrutura real para ser consumida pela aplicação.

O `VaultiaAppContainer` (coração da injeção de dependências do App) não pode importar classes do Android (`Context`, `Room`, `SQLite`) devido às severas restrições do `VaultiaAppContainerArchitectureTest`. No entanto, ele aceita um `StorageContainer` abstrato no seu construtor.

## Objetivo

Atualizar o `StorageCompositionRoot` adicionando a fábrica oficial de produção `createLocalContainer(context: Context)`. Esse método será o responsável por instanciar o `VaultiaLocalDatabase` real no disco, criar o `RoomStorageRepository` e encapsular tudo em um `StorageContainer`.

## Arquivos Afetados

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/composition/StorageCompositionRoot.kt
```

### Testes

```text
apps/android/app/src/androidTest/java/com/vaultia/app/core/storage/composition/StorageCompositionRootTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_50_PLAN.md
```

## Regras de Arquitetura e Segurança

1. **Blindagem do AppContainer**: Nenhuma linha do `VaultiaAppContainer` ou do pacote `core.app` será alterada. O `VaultiaAppContainerArchitectureTest` permanecerá verde.
2. **Separação de Contexto**: O `StorageCompositionRoot` usará o `Context` (applicationContext) para construir o banco de dados persistente (`vaultia_local_metadata.db`).
3. **Sem Criptografia de DB**: Nesta fase, o banco não usará SQLCipher, pois o `StorageRecordMetadataEntity` já foi classificado como seguro para cleartext na Phase 48 (senhas ficam no payload cifrado em disco).

## Critérios de Aceite

- Plano Phase 50 criado.
- `StorageCompositionRoot.createLocalContainer(context)` implementado.
- Teste instrumentado validando que a instância gerada salva e recupera dados corretamente integrando todas as camadas (Root -> Service -> UseCase -> Repository -> DAO).
- Architecture Tests passando com sucesso (nenhum vazamento de Context/Room para o `core.app`).
- APK compila sem a permissão `android.permission.INTERNET`.
