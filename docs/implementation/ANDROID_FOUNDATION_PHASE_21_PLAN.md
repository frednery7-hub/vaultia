# Android Foundation — Phase 21 Plan

## Nome

Master Password Policy Contract

## Objetivo

Definir e implementar a política de qualidade da senha mestra do Vaultia como
uma regra de domínio local, reutilizável e independente de UI, storage ou
criptografia — sem ainda salvar, derivar, ou persistir nenhuma senha real.

## Decisão arquitetural

```text
core/security/password/
    MasterPasswordPolicy.kt
    MasterPasswordValidationResult.kt
    MasterPasswordValidationError.kt
```

Não em `feature/auth`, não em `core/crypto`, não em
`core/session`.

### Fronteira

```text
feature/auth
    chama
core/security/password
    valida apenas a senha
core/crypto
    ainda não entra nesta fase
core/session
    ainda não muda nesta fase
core/storage
    ainda não muda nesta fase
```

`MasterPasswordPolicy` não sabe que existe campo de confirmação. A comparação
`password == confirmation` e o erro de "confirmação diferente" são
responsabilidade exclusiva de `feature/auth`, que combina os erros da policy
com os próprios erros de fluxo/UI.

## Regras de validação desta fase

```text
Senha mestra válida quando, simultaneamente:
- não é vazia;
- tem no mínimo 16 caracteres;
- tem no máximo 64 caracteres;
- não contém espaço no início ou no fim;
- não está na lista embutida de senhas/padrões comuns;
- não é repetição óbvia de um único caractere (ex.: "aaaaaaaaaaaaaaaa");
- não é sequência trivial numérica ou alfabética (ex.: "1234567890123456",
  "abcdefghijklmnop", "qwertyqwertyqwerty").
```

Explicitamente **fora desta fase**: exigência de composição obrigatória
(letra + número + símbolo). Senhas longas e memoráveis ("cavalo chuva metro
cobre") devem ser aceitas se atenderem aos critérios acima; a ausência de
composição obrigatória é uma decisão deliberada, não uma lacuna.

### Lista de senhas/padrões comuns (nesta fase)

Lista pequena, embutida no código-fonte (não em arquivo externo, não
carregada de rede). Cobre exatamente os padrões já identificados como risco:
repetições, sequências triviais, e termos genéricos previsíveis
("password", "senha", "vaultia" combinados com sequências numéricas óbvias).
Esta lista é deliberadamente pequena nesta fase; uma lista maior/externa fica
para fase futura, fora do escopo da Phase 21.

## Escopo

Esta fase cria:

- `MasterPasswordPolicy` — recebe uma senha como `String`, aplica as regras
  acima, devolve um `MasterPasswordValidationResult`. Não conhece UI, não
  conhece confirmação, não persiste nada.
- `MasterPasswordValidationResult` — resultado estruturado da validação
  (válida ou não, lista de erros associados).
- `MasterPasswordValidationError` — enum dos erros possíveis de qualidade da
  senha (vazia, curta demais, longa demais, espaço externo, senha comum,
  repetição trivial, sequência trivial).

## Fora de escopo

Esta fase não implementa:

- comparação senha + confirmação (fica em `feature/auth`, fase futura);
- hash da senha;
- salt;
- KDF;
- AES;
- Android Keystore;
- biometria;
- banco de dados;
- arquivos locais;
- storage criptografado ou não criptografado;
- tela real de criação de senha mestra;
- CRUD real;
- backup;
- import/export;
- backend;
- rede;
- permissão INTERNET;
- exigência de composição obrigatória (letra/número/símbolo).

## Critérios de aceite

- Existe `MasterPasswordPolicy` em `core/security/password`, com lógica real
  de validação (não é contrato vazio).
- A policy valida exclusivamente o conteúdo da senha — não recebe nem conhece
  um campo de confirmação.
- Senha com menos de 16 caracteres é rejeitada.
- Senha com mais de 64 caracteres é rejeitada, sem truncamento silencioso.
- Senha vazia é rejeitada.
- Senha com espaço no início ou no fim é rejeitada.
- Senha presente na lista embutida de padrões comuns é rejeitada.
- Senha que é repetição de um único caractere é rejeitada.
- Senha que é sequência trivial numérica ou alfabética é rejeitada.
- Senha longa, memorável, sem composição especial (ex.: passphrase de 4
  palavras com 16+ caracteres) é aceita.
- `MasterPasswordValidationResult` expõe se a senha é válida e, se não for,
  a lista de erros específicos.
- Nenhum storage real é usado.
- Nenhuma criptografia é usada.
- Nenhuma permissão nova é adicionada.
- O APK permanece sem `android.permission.INTERNET`.
- Testes cobrem cada regra de validação isoladamente e em combinação.
- O roadmap formal não é atualizado nesta fase (próxima atualização em 75%).
