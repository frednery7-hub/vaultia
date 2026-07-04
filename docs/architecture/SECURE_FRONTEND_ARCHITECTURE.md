# Secure Frontend Architecture

## Status

Documento arquitetural da Phase 36.

Este documento define a arquitetura segura da futura interface real do Vaultia. Ele não implementa UI, não conecta decrypt à interface e não cria storage real.

---

## Objetivo

Definir limites arquiteturais para que a interface futura do Vaultia não exponha secrets por renderização indevida, logs, previews, screenshots, clipboard, navegação, back stack ou estados inconsistentes.

A premissa de segurança é que o frontend é uma superfície de exposição. Mesmo com criptografia correta, uma UI mal desenhada pode vazar dados sensíveis.

---

## Separação Obrigatória de Camadas

A arquitetura futura deve separar três camadas:

```text
1. Demo UI atual
2. Secure UI shell futuro
3. Vault data rendering futuro
```

### 1. Demo UI atual

- Deve continuar identificada como demonstrativa.
- Não deve renderizar dados reais do cofre.
- Não deve ser usada como prova de segurança operacional.
- Não deve receber payload decryptado.
- Não deve receber master password.
- Não deve receber key material.

### 2. Secure UI shell futuro

- Deve controlar estado de sessão.
- Deve controlar lock e unlock visual.
- Deve controlar redaction.
- Deve controlar política de screenshot.
- Deve controlar política de clipboard.
- Deve controlar timeout visual.
- Deve limpar navegação sensível no lock.

### 3. Vault data rendering futuro

- Só pode renderizar dados após autenticação real.
- Só pode renderizar dados após derivação de chave real.
- Só pode renderizar dados após decrypt controlado.
- Deve consumir modelos de apresentação seguros.
- Não deve receber secrets em navigation args.
- Não deve logar estado sensível.

---

## Fronteiras de Segurança

### Fronteira Auth

- A UI não deve considerar usuário autenticado por estado demonstrativo.
- Unlock simulado não deve abrir vault real.
- Erros de autenticação devem ser redacted.
- Mensagens de erro não devem revelar política interna de KDF além do necessário.

### Fronteira Crypto

- Decrypt não deve alimentar diretamente componentes visuais genéricos.
- O resultado decryptado deve passar por mapeamento para modelo de apresentação seguro.
- Secrets revelados devem ter escopo curto.
- A UI não deve armazenar key material.
- A UI não deve reter plaintext além do necessário.

### Fronteira Navigation

- Rotas não devem carregar secrets.
- Rotas não devem carregar payload serializado.
- Rotas podem carregar identificadores técnicos não sensíveis.
- Deep links para áreas sensíveis devem cair em estado bloqueado.
- Lock deve limpar back stack sensível.

### Fronteira Platform

- Screenshots devem ser bloqueados em telas sensíveis quando o vault real existir.
- Recents screen deve ocultar conteúdo sensível.
- Background deve redigir ou bloquear conteúdo sensível.
- Clipboard deve ser tratado como superfície insegura.

---

## Política de Implementação Futura

Antes de qualquer tela real do vault, as próximas fases devem cumprir:

- criar estado visual seguro;
- criar componentes de redaction;
- criar política de screenshot;
- criar política de lifecycle lock/redaction;
- criar regra de navegação segura;
- definir clipboard como ação explícita;
- garantir que previews usem fake data;
- impedir logs de secrets.

---

## Fora de Escopo Nesta Phase

- Tela real do vault.
- Lista real de itens.
- Detalhe real de item.
- Reveal real de senha.
- Clipboard em código.
- Screenshot blocking em código.
- Android Keystore.
- Biometria.
- Storage real.
- Backup/export.
