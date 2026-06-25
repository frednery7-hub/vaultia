# Vaultia – Android Foundation Phase 6 PLAN

## Objetivo

Criar testes mínimos de arquitetura para proteger a base Android contra regressões antes de implementar funcionalidade sensível.

## Escopo Permitido

Esta fase permite:

- adicionar JUnit para testes unitários locais;
- criar testes metadata-only para `VaultItem`;
- criar testes para categorias aprovadas de `VaultItemType`;
- criar testes para estados locais de `VaultSessionState`;
- criar testes para contratos sem métodos/implementações;
- rodar build, test e check Android.

## Escopo Bloqueado

Esta fase não permite implementar:

- criptografia real;
- KDF;
- AES-GCM;
- Android Keystore;
- Master Password;
- banco local;
- storage criptografado;
- cofre real;
- biometria;
- backup;
- clipboard;
- rede;
- permissão INTERNET.

## Critérios de Aceite

A fase só pode ser fechada se:

- `./gradlew testDebugUnitTest` passar;
- `./scripts/check-android.sh` passar;
- Manifest continuar sem INTERNET;
- APK binário continuar sem INTERNET;
- nenhum arquivo perigoso aparecer no Git status;
- contratos continuarem sem implementação real.

## Status

- **Status:** Planejado
- **Próximo passo:** executar testes e auditoria final
