# Vaultia – Android Foundation Phase 2 DONE

## Decisão

A Fundação Android Fase 2 foi concluída com sucesso.

## Data

2026-06-24

## Objetivo da Fase

A Fase 2 teve como objetivo higienizar a base do projeto Android antes de qualquer implementação funcional.

Esta fase protege o repositório contra versionamento acidental de arquivos gerados, caminhos locais, APKs, secrets e artefatos sensíveis.

## Resultado

Foram criados e validados:

- `.gitignore` na raiz do projeto;
- `apps/android/.gitignore`;
- regras para ignorar `.gradle/`;
- regras para ignorar `build/`;
- regras para ignorar `local.properties`;
- regras para ignorar APK/AAB/APKS;
- regras para ignorar keystores e credenciais;
- regras para ignorar artefatos sensíveis do Vaultia.

## Git

O repositório Git foi inicializado com sucesso.

Branch inicial:

- `main`

Nenhum commit foi realizado nesta fase.

## Arquivos Ignorados Validados

Foram validados com `git check-ignore`:

- `apps/android/local.properties`;
- `apps/android/app/build/outputs/apk/debug/app-debug.apk`;
- `apps/android/.gradle/9.5.0/gc.properties`;
- `apps/android/app/build/intermediates/merged_manifests/debug/processDebugManifest/AndroidManifest.xml`.

## Limpeza Física

Foram removidos fisicamente antes do rebuild:

- `apps/android/.gradle`;
- `apps/android/build`;
- `apps/android/app/build`.

## Rebuild

Após a limpeza física, o projeto foi recompilado do zero.

Resultado:

- build debug bem-sucedido;
- APK debug regenerado;
- APK debug sem permissão `android.permission.INTERNET`.

## Artefato Debug Regenerado

APK:

- `apps/android/app/build/outputs/apk/debug/app-debug.apk`

Tamanho observado:

- aproximadamente `824 KB`

## Segurança

A fase confirmou:

- nenhum arquivo perigoso aparece no `git status`;
- `local.properties` não será versionado;
- APK debug não será versionado;
- `.gradle/` não será versionado;
- `build/` não será versionado;
- Manifest fonte permanece sem `uses-permission`;
- APK binário permanece sem `android.permission.INTERNET`.

## Escopo Não Implementado

Esta fase não implementou:

- cofre;
- Master Password;
- criptografia;
- banco local;
- storage seguro;
- biometria;
- backup;
- auditoria local;
- UI sensível.

## Status

- **Status:** Concluído
- **Próximo passo:** abrir projeto no VS Code e auditar arquivos versionáveis antes do primeiro commit
