# Plausible Deniability Scope Decision

## Status

Decisão arquitetural registrada antes da Phase 37.

## Decisão

Vaultia v1 não implementa duress password, decoy vault, hidden vault ou plausible deniability.

A ideia de duas senhas foi considerada no início do projeto, mas não foi incorporada à arquitetura implementada até a Phase 36.

## Motivo

Plausible deniability real não é uma feature simples de UI. É uma decisão de arquitetura criptográfica e de formato de storage.

Para ser tecnicamente defensável, exigiria:

- storage indistinguível entre vault real e vault de fachada;
- ausência de dois arquivos ou dois headers visíveis;
- padding e tamanho controlado;
- metadados não reveladores;
- senhas matematicamente independentes;
- ausência de botão, flag ou rota indicando camada oculta;
- threat model próprio contra coerção e análise forense.

Implementar apenas duas senhas na tela de login criaria falsa segurança.

## Estado Atual da Arquitetura

Até a Phase 36, Vaultia possui arquitetura de vault único:

- uma senha-mestra;
- um fluxo KDF;
- um VaultHeader;
- uma vault key derivada;
- um modelo de payload cifrado;
- um modelo de item cifrado;
- nenhuma deniabilidade forense.

## Escopo da v1

Vaultia v1 será um cofre offline forte e honesto:

- offline-only;
- sem internet;
- sem backend;
- sem recuperação de senha;
- senha-mestra única;
- Argon2id;
- AES-GCM;
- lock forte;
- proteção de tela;
- timeout;
- reautenticação para ações críticas;
- threat detection local.

Vaultia v1 não deve prometer hidden vault, decoy vault ou plausible deniability.

## Escopo Futuro

Plausible deniability pode ser reavaliada em v2 como research track separado, antes de qualquer mudança de storage final.

Essa reavaliação deve criar novo threat model, novo formato de vault, estratégia de padding, modelo de metadados e análise forense.

## Frase Pública Permitida

Vaultia v1 protege dados locais contra acesso não autorizado, sem internet, sem backend e sem recuperação de senha.

## Frase Pública Proibida

Vaultia v1 não deve afirmar que possui plausible deniability, hidden vault, decoy vault ou senha de coerção.
