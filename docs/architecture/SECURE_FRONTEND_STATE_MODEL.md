# Secure Frontend State Model

## Status

Documento arquitetural da Phase 36.

Este documento define o modelo de estado visual seguro para a futura interface real do Vaultia.

---

## Estados Visuais Planejados

```text
BOOTSTRAPPING
LOCKED
AUTHENTICATING
UNLOCKED_REDACTED
UNLOCKED_REVEALED
LOCKING
ERROR_REDACTED
```

---

## BOOTSTRAPPING

Estado inicial do app antes de determinar se existe vault configurado, sessão válida ou necessidade de autenticação.

Permitido:

- splash seguro;
- loading neutro;
- mensagens não sensíveis.

Proibido:

- exibir item de vault;
- exibir segredo;
- renderizar payload;
- navegar para tela sensível.

---

## LOCKED

Estado bloqueado. Nenhum conteúdo sensível pode estar renderizado.

Regras:

- limpar navegação sensível;
- redigir tela imediatamente;
- remover estado revelado;
- impedir acesso direto a detalhes;
- não manter secrets em UI state.

---

## AUTHENTICATING

Estado de autenticação em andamento.

Permitido:

- campo de master password quando aplicável;
- progresso genérico;
- erro redacted;
- bloqueio temporário após falhas futuras.

Proibido:

- listar itens;
- revelar secrets;
- logar senha;
- mostrar detalhes de KDF além do necessário.

---

## UNLOCKED_REDACTED

Estado desbloqueado, mas com conteúdo sensível redigido.

Este deve ser o estado padrão após unlock real futuro.

Permitido:

- listar itens de forma segura;
- mostrar categorias;
- mostrar labels redacted;
- permitir ação explícita de revelar;
- permitir ação explícita de copiar com aviso futuro.

Proibido:

- exibir senha automaticamente;
- exibir nota clara automaticamente;
- exibir documento claro automaticamente;
- exibir foto privada automaticamente.

---

## UNLOCKED_REVEALED

Estado temporário em que um segredo específico foi revelado por ação explícita.

Regras:

- deve ter timeout;
- deve ser reversível;
- deve permitir esconder imediatamente;
- deve redigir em background;
- deve redigir em lock;
- deve evitar logs;
- deve evitar screenshots quando a política estiver implementada.

Esse estado não deve ser persistido.

---

## LOCKING

Estado transitório para limpeza visual e retorno ao bloqueio.

Regras:

- limpar back stack sensível;
- remover secrets renderizados;
- cancelar timers de reveal;
- cancelar ações pendentes de clipboard;
- voltar para `LOCKED`.

---

## ERROR_REDACTED

Estado de erro seguro.

Pode informar que uma ação falhou, mas não pode expor:

- segredo;
- senha mestra;
- chave;
- plaintext;
- payload serializado;
- stack trace ao usuário;
- detalhes de decrypt sensíveis.

---

## Transições Permitidas

```text
BOOTSTRAPPING -> LOCKED
LOCKED -> AUTHENTICATING
AUTHENTICATING -> UNLOCKED_REDACTED
AUTHENTICATING -> ERROR_REDACTED
ERROR_REDACTED -> LOCKED
UNLOCKED_REDACTED -> UNLOCKED_REVEALED
UNLOCKED_REVEALED -> UNLOCKED_REDACTED
UNLOCKED_REDACTED -> LOCKING
UNLOCKED_REVEALED -> LOCKING
LOCKING -> LOCKED
```

Qualquer transição que tente acessar conteúdo sensível a partir de `LOCKED`, `BOOTSTRAPPING` ou `AUTHENTICATING` deve ser bloqueada pela arquitetura futura.
