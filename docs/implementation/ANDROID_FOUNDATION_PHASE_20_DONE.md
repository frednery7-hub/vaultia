# Android Foundation — Phase 20 Done

## Status

Aprovado.

## Objetivo

Deixar explícito na UI que o desbloqueio atual ainda é simulado e que dados reais só serão permitidos depois de autenticação local real, KDF, criptografia e storage seguro.

## Resultado

Foi criado o componente reutilizável:

- `AuthBoundaryNoticeView`

O aviso foi aplicado em:

- `LockedVaultScreen`
- `UnlockedVaultScreen`

Com isso, tanto o estado bloqueado quanto o estado desbloqueado deixam claro que a versão atual ainda não protege dados reais.

## Progresso

Após esta fase:

```text
20 / 40 = 50%
```

## Observação sobre roadmap

O roadmap não foi atualizado nesta fase. A próxima atualização formal do roadmap será em 75%.

## Garantias de segurança desta fase

- Nenhum storage real foi adicionado.
- Nenhuma autenticação real foi adicionada.
- Nenhuma criptografia foi adicionada.
- Nenhum Android Keystore foi adicionado.
- Nenhuma permissão nova foi adicionada.
- O APK permanece sem android.permission.INTERNET.
- A UI declara explicitamente que o desbloqueio é simulado.
- A UI declara explicitamente que dados reais dependem de autenticação local real, KDF, criptografia e storage seguro.

## Testes

Foram adicionados e atualizados testes para validar:

- existência do componente `AuthBoundaryNoticeView`;
- mensagem explícita sobre desbloqueio simulado;
- menção obrigatória a autenticação local real, KDF, criptografia e storage seguro;
- uso do aviso na tela bloqueada;
- uso do aviso na tela desbloqueada;
- ausência de storage, criptografia ou rede nesta fase.

## Critério de aceite

A Phase 20 é considerada concluída porque a fronteira pré-autenticação ficou explícita na UI antes da entrada no bloco crítico de autenticação, KDF e criptografia.
