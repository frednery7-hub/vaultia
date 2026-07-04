# Secure Frontend Rendering Rules

## Status

Documento arquitetural da Phase 36.

Este documento define regras de renderização segura para secrets no frontend futuro do Vaultia.

---

## Regra Principal

Secrets não devem ser renderizados por padrão.

Qualquer exposição visual de segredo deve ser:

- explícita;
- temporária;
- reversível;
- redigida automaticamente;
- não logada;
- não transportada pela navegação;
- não exposta em previews.

---

## Dados Permitidos em Lista

Listas futuras podem exibir somente metadados seguros ou redigidos.

Permitido:

- tipo técnico do item;
- identificador técnico não sensível quando necessário;
- timestamps não sensíveis quando necessário;
- label redacted;
- ícone por categoria;
- estado visual bloqueado ou redigido.

Proibido por padrão:

- senha;
- nota clara;
- token;
- seed phrase;
- documento claro;
- foto privada clara;
- payload serializado;
- key material;
- master password.

---

## Dados Permitidos em Detalhe

Detalhes futuros devem iniciar redigidos.

O usuário deve solicitar revelação explicitamente.

Fluxo obrigatório:

```text
UNLOCKED_REDACTED
acao explicita de revelar
UNLOCKED_REVEALED
timeout ou acao de esconder
UNLOCKED_REDACTED
```

A UI não deve manter segredo revelado indefinidamente.

---

## Componentes de Renderização Sensível

Campos sensíveis futuros devem usar componentes dedicados.

Componentes genéricos de texto não devem receber secrets diretamente.

Componentes sensíveis devem ter:

- estado redacted por padrão;
- ação explícita de revelar;
- ação explícita de esconder;
- timeout de redaction;
- bloqueio em background;
- ausência de logs;
- ausência de previews reais.

---

## Screenshot e Recents

Quando telas reais forem criadas, a política de screenshot deve ser aplicada antes da renderização de vault real.

Requisitos futuros:

- bloquear screenshots em telas sensíveis;
- ocultar conteúdo no app switcher;
- redigir conteúdo ao ir para background;
- não depender de ação manual do usuário;
- testar comportamento em ciclo de vida Android.

---

## Clipboard

Clipboard deve ser tratado como superfície insegura.

Regras futuras:

- copiar segredo somente por ação explícita;
- nunca copiar automaticamente;
- informar risco ao usuário;
- limpar clipboard quando possível;
- não registrar o valor copiado em logs;
- não exibir o valor copiado em toast, snackbar ou exception.

---

## Logs e Erros

Proibido logar:

- master password;
- derived key;
- vault key;
- plaintext;
- secret revelado;
- payload serializado;
- ciphertext;
- nonce;
- authentication tag.

Erros devem ser redacted e acionáveis, sem detalhes internos sensíveis.
