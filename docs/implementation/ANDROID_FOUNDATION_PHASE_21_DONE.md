# Android Foundation — Phase 21 Done

## Status

Aprovado.

## Objetivo

Definir e implementar a política de qualidade da senha mestra do Vaultia como
uma regra de domínio local, reutilizável e independente de UI, storage ou
criptografia — sem ainda salvar, derivar, ou persistir nenhuma senha real.

## Resultado

Foi criado o pacote `core/security/password` com três arquivos:

- `MasterPasswordPolicy` — valida o conteúdo de uma senha (`String`) e
  devolve um `MasterPasswordValidationResult`. Não conhece UI, não conhece
  campo de confirmação, não persiste nada, não deriva nem faz hash de nada.
- `MasterPasswordValidationResult` — resultado estruturado (válida ou não +
  lista de erros).
- `MasterPasswordValidationError` — enum com os sete erros de qualidade
  possíveis.

A policy aplica as seguintes regras, simultaneamente:

- rejeita senha vazia;
- exige mínimo de 16 caracteres;
- exige máximo de 64 caracteres, sem truncamento silencioso;
- rejeita espaço no início ou no fim;
- rejeita senhas presentes em uma lista pequena embutida de padrões
  comuns/fracos;
- rejeita repetição de um único caractere ao longo de toda a senha;
- rejeita sequência trivial ascendente ou descendente (4+ caracteres
  consecutivos no alfabeto ou nos dígitos), em qualquer posição da senha.

Deliberadamente, esta fase **não** exige composição obrigatória
(letra + número + símbolo). Passphrases longas e memoráveis sem caracteres
especiais são aceitas quando atendem aos critérios acima.

A comparação `password == confirmation` e o tratamento de "confirmação
diferente" permanecem fora desta fase, reservados para `feature/auth`.

## Progresso

Esta fase está dentro do bloco 50% → 75%. O roadmap formal não é atualizado
nesta fase, conforme regra do projeto (próxima atualização formal em 75%).

## Garantias de segurança desta fase

- Nenhum storage real foi adicionado ou utilizado.
- Nenhuma criptografia real foi adicionada ou utilizada.
- Nenhum Android Keystore foi utilizado.
- Nenhuma permissão nova foi adicionada.
- O APK permanece sem `android.permission.INTERNET`.
- A policy não depende de `android.*` nem `androidx.*`.
- A policy não tem qualquer ciência de UI (Activity, Fragment, Compose,
  TextView, Context).
- A lista de padrões comuns permanece pequena e embutida no código-fonte —
  não é carregada de arquivo externo nem de rede.
- A policy não recebe nem conhece um campo de confirmação; esse boundary é
  verificado por teste de arquitetura dedicado.

## Testes

Foram criados:

- `MasterPasswordPolicyTest` — cobre cada regra de validação isoladamente,
  casos de borda (limites exatos de 16 e 64 caracteres, acentos/Unicode,
  combinação de múltiplos erros simultâneos) e os casos de aceite
  (passphrases longas memoráveis, com e sem símbolos/números).
- `MasterPasswordPolicyArchitectureTest` — garante a forma do contrato
  público, a ausência de dependência de frameworks de storage/cripto/rede/
  Android, e a ausência de qualquer noção de campo de confirmação no código
  de produção.

Durante a implementação, a execução real da suíte revelou três falhas que
não eram visíveis em revisão estática:

1. O KDoc original de `MasterPasswordPolicy` mencionava as palavras
   "confirmation" e "Keystore" em prosa explicativa, o que violava os
   próprios testes de arquitetura que proíbem essas strings no arquivo.
   Corrigido reescrevendo o KDoc sem essas palavras-gatilho.
2. Um teste (`multipleSimultaneousErrorsAreAllReported`) assumia
   incorretamente que `" aaaa "` disparava `TRIVIAL_CHARACTER_REPETITION`;
   na implementação real, a regra de repetição exige que **todos** os
   caracteres da senha sejam iguais entre si, e o espaço nas bordas quebra
   essa condição. O teste foi corrigido para refletir o comportamento real,
   e um novo teste (`shortPasswordThatIsAlsoTrivialRepetitionReportsBothErrors`)
   foi adicionado para cobrir o caso de repetição trivial pura combinada com
   comprimento insuficiente.

Após as correções, a suíte completa do módulo (`:app:testDebugUnitTest`,
sem filtro, cobrindo todas as 21 fases) passou com sucesso.

## Critério de aceite

A Phase 21 é considerada concluída porque a política de qualidade da senha
mestra existe como regra de domínio testada e isolada de UI, storage e
criptografia, respeita o boundary de não conhecer confirmação, e a suíte
completa do projeto permanece verde após sua inclusão.
