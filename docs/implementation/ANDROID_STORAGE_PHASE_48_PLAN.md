# Android Storage — Phase 48 Plan

## Status

Planned.

## Nome

Local Metadata Database Schema Draft

## Contexto

A Phase 47 conectou o `StorageContainer` in-memory ao container do aplicativo. O próximo grande marco de infraestrutura é preparar a camada real de persistência.

No modelo híbrido desenhado na Phase 41, metadados técnicos seguros (IDs, timestamps, tipos de item, ponteiros de arquivos) devem ser indexados no SQLite (via Room) para rápida consulta, enquanto as informações ultrassecretas (senhas, usernames, notas) residirão em payloads cifrados em disco (`File I/O`).

A Phase 48 dá o primeiro passo focado estritamente na modelagem do banco de dados Room (Entities e DAOs), validando o esquema via testes em memória. 

---

## Objetivo

Definir e implementar os contratos de persistência do SQLite usando o framework Room, focando na entidade de metadados (`StorageRecordMetadata`). 

O banco de dados deve ser validado de forma isolada em ambiente de teste (`Room.inMemoryDatabaseBuilder()`), mantendo a camada de produção do aplicativo sem criar arquivos `.db` reais ainda.

---

## Arquivos Potencialmente Afetados

### Build e Configuração

```text
apps/android/build.gradle.kts (Adição do Room Runtime, Compiler KSP)
```

### Produção

```text
apps/android/app/src/main/java/com/vaultia/app/core/storage/local/entity/StorageRecordMetadataEntity.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/local/dao/StorageRecordMetadataDao.kt
apps/android/app/src/main/java/com/vaultia/app/core/storage/local/VaultiaLocalDatabase.kt
```

### Testes

```text
apps/android/app/src/test/java/com/vaultia/app/core/storage/local/VaultiaLocalDatabaseTest.kt
```

### Documentação

```text
docs/implementation/ANDROID_STORAGE_PHASE_48_PLAN.md
```

---

## Regras de Segurança e Arquitetura

- **Classificação Estrita de Dados:** A classe `@Entity` do Room **NUNCA** pode ter colunas para título real, senha, username, salt público ou keys. 
- **Colunas Permitidas:** Apenas ID UUID/String, `StorageRecordType`, timestamps long, version code, e a referência opaca para o payload (`payloadPointer`).
- **Isolamento de Produção:** Na Phase 48, não criaremos o `Room.databaseBuilder()` no `VaultiaAppContainer`. O banco será usado exclusivamente em contexto de testes in-memory para validação de sintaxe SQL e DAO.
- **Integração limpa:** Usar KSP (Kotlin Symbol Processing) em vez do legado KAPT para o compilador do Room.

---

## Critérios de Aceite

- Plano da Phase 48 criado.
- Dependências do Room (runtime e compiler) integradas via KSP com sucesso no Gradle.
- Classe `StorageRecordMetadataEntity` criada, mapeando os metadados técnicos necessários sem violar a classificação de dados.
- Classe `StorageRecordMetadataDao` criada e suportando insert, findById, findAll e deleteById.
- Classe abstrata `VaultiaLocalDatabase` estendendo `RoomDatabase` implementada.
- Um teste in-memory (`VaultiaLocalDatabaseTest`) valida todas as interações do DAO isoladamente.
- Nenhuma integração do Room feita no `VaultiaAppContainer` ou `StorageContainer` (o App continua usando o Mock/In-Memory em produção).
- APK compila sem a permissão `android.permission.INTERNET`.
- Todos os testes e scripts de validação (`check-android.sh`) continuam passando.
