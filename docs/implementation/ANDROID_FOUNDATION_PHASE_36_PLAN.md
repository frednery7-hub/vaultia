# Android Foundation — Phase 36 Plan

## Nome

Secure Frontend Architecture Pause

## Objetivo

Pausar antes de implementar frontend real e definir a arquitetura segura da interface do Vaultia, evitando exposição indevida de secrets, logs, previews, screenshots, clipboard, navegação insegura, estados visuais inconsistentes e mistura entre UI demonstrativa e cofre real.

A Phase 36 não deve criar telas reais de vault, não deve criar storage real, não deve renderizar secrets reais e não deve conectar o fluxo criptográfico à interface. Esta fase cria apenas documentação técnica e contratos arquiteturais para orientar a implementação segura das fases seguintes.

---

## Contexto Técnico

As fases anteriores criaram a fundação criptográfica e os contratos de item:

- Phase 30 — boundary AES-GCM.
- Phase 31 — encryption de payload em memória.
- Phase 32 — decryption de payload em memória.
- Phase 33 — serialização textual versionada de payload cifrado.
- Phase 34 — draft de item criptografado em memória.
- Phase 35 — serialização textual versionada de item criptografado.

Até este ponto, o app ainda não possui storage real, UI real do cofre, Android Keystore, biometria, backup/export ou persistência operacional. A UI existente permanece demonstrativa/bootstrapping.

Antes de criar qualquer frontend real, a arquitetura precisa estabelecer regras de segurança para renderização, navegação, bloqueio visual, previews, logs e ciclo de vida da sessão.

---

## Decisão Arquitetural

A Phase 36 será uma pausa deliberada de arquitetura. Ela não adiciona código de UI real. Ela cria uma especificação de frontend seguro para que as próximas fases implementem telas com limites claros.

A decisão central é separar explicitamente três camadas:

```text
1. Demo UI atual
2. Secure UI shell futuro
3. Vault data rendering futuro
```

A Demo UI atual não deve ser tratada como cofre real.

O Secure UI shell futuro deve controlar sessão, bloqueio, navegação, redaction, screenshot policy, clipboard policy e timeout visual.

O Vault data rendering futuro só poderá renderizar secrets depois que houver autenticação real, derivação de chave real, decrypt controlado e política de exposição visual.

---

## Arquivos Planejados

### Documentação

```text
docs/implementation/ANDROID_FOUNDATION_PHASE_36_PLAN.md
docs/architecture/SECURE_FRONTEND_ARCHITECTURE.md
docs/architecture/SECURE_FRONTEND_RENDERING_RULES.md
docs/architecture/SECURE_FRONTEND_STATE_MODEL.md
```

### Código

Nenhum arquivo de código de produção deve ser criado nesta fase.

### Testes

Nenhum teste novo é obrigatório nesta fase, porque a entrega é arquitetural/documental. A validação técnica continua exigindo `./gradlew testDebugUnitTest` e `./scripts/check-android.sh` para provar que a documentação não alterou comportamento do app.

---

## Regras Arquiteturais Planejadas

### 1. Separação entre Demo UI e Vault Real

- A UI atual deve continuar marcada como demonstrativa.
- Demo data não deve ser confundida com secrets reais.
- Nenhum item real deve ser exibido pela UI demonstrativa.
- A entrada no vault real deve exigir fluxo próprio.
- O estado visual do vault real não deve reutilizar nomes ambíguos de demo.

### 2. Modelo de Estado Visual Seguro

Estados planejados:

```text
BOOTSTRAPPING
LOCKED
AUTHENTICATING
UNLOCKED_REDACTED
UNLOCKED_REVEALED
LOCKING
ERROR_REDACTED
```

Regras:

- `LOCKED` não pode manter secrets renderizados.
- `AUTHENTICATING` não pode mostrar conteúdo do vault.
- `UNLOCKED_REDACTED` pode listar itens sem expor segredo.
- `UNLOCKED_REVEALED` deve ser temporário e explícito.
- `LOCKING` deve limpar ou substituir a renderização sensível.
- `ERROR_REDACTED` não pode vazar detalhes de segredo, chave, payload ou decrypt.

### 3. Renderização de Secrets

- Secrets não devem aparecer em listas por padrão.
- Secrets devem exigir ação explícita de revelar.
- Secrets revelados devem ter tempo de exposição limitado.
- Secrets revelados devem voltar para estado redacted automaticamente.
- Campos sensíveis devem usar componentes dedicados, não Text genérico espalhado.
- Estados de erro nunca devem imprimir valor sensível.

### 4. Screenshot e Recents Screen

- O app deve bloquear screenshots em telas sensíveis quando a implementação real começar.
- A tela de recentes do Android deve esconder conteúdo sensível.
- A política deve ser aplicada antes de renderizar vault real.
- A política não deve depender de o usuário lembrar de ocultar manualmente.

### 5. Clipboard

- Copiar segredo deve ser ação explícita.
- Clipboard deve ter timeout de limpeza quando possível.
- UI deve avisar que clipboard é uma superfície de risco.
- Clipboard não deve receber segredo automaticamente.
- Não deve haver cópia silenciosa.

### 6. Logs, Analytics e Crashes

- UI não deve logar secrets.
- UI não deve logar plaintext.
- UI não deve logar key material.
- UI não deve logar payload serializado.
- UI não deve incluir secrets em exceptions.
- Analytics continua fora de escopo nesta fundação.
- Crash reporting continua fora de escopo nesta fundação.

### 7. Navegação Segura

- Telas sensíveis não devem ser acessíveis quando o vault está bloqueado.
- Back stack deve ser limpo ao bloquear.
- Deep link para tela sensível deve cair em `LOCKED`.
- Erros de sessão devem redirecionar para estado bloqueado ou redacted.
- Navegação não deve transportar secrets como argumentos de rota.

### 8. Ciclo de Vida e Auto-Lock

- App em background deve redigir ou bloquear conteúdo sensível.
- App em pausa deve remover estado visual revelado.
- Auto-lock por inatividade deve ser requisito antes de vault real.
- Configuração de timeout deve ter padrão conservador.
- Sessão em memória deve ser limpa em lock explícito.

### 9. Previews, Screenshots de Desenvolvimento e Dados Fake

- Previews devem usar somente fake data.
- Fake data deve ser claramente marcada.
- Screenshots de desenvolvimento não devem conter secrets reais.
- Dados de seed da UI não devem parecer credenciais reais.
- Testes de UI futuros devem usar fixtures redacted.

### 10. Fronteira entre Decrypt e UI

- Decrypt não deve retornar diretamente objetos renderizáveis sem política de redaction.
- UI deve consumir modelos próprios de apresentação segura.
- ViewModel futuro não deve manter secrets revelados além do necessário.
- Estado revelado deve ser escopo curto.
- Dados sensíveis não devem entrar em navigation args.

---

## Requisitos de Segurança

- Não implementar UI real nesta fase.
- Não conectar decrypt à UI nesta fase.
- Não renderizar secrets reais.
- Não criar storage real.
- Não escrever em disco.
- Não ler disco.
- Não criar SQLite.
- Não criar Room.
- Não criar DataStore.
- Não criar SharedPreferences.
- Não adicionar permissão Android.
- Não adicionar `android.permission.INTERNET`.
- Não implementar Android Keystore.
- Não implementar biometria.
- Não implementar backup/export.
- Não implementar modo discreto/decoy vault.
- Não alterar roadmap formal nesta fase.

---

## Escopo

Esta fase cria:

- plano da Phase 36;
- documento de arquitetura segura de frontend;
- documento de regras de renderização segura;
- documento de modelo de estado visual seguro;
- validação de que testes existentes continuam passando;
- validação de que o APK permanece sem `android.permission.INTERNET`;
- relatório DONE.

---

## Fora de Escopo

Esta fase não implementa:

- tela real do vault;
- lista real de itens;
- detalhe real de item;
- revelar senha;
- copiar senha;
- screenshot blocking em código;
- clipboard em código;
- navegação real do vault;
- ViewModel real de vault;
- storage real;
- Android Keystore;
- biometria;
- backup/export;
- alteração no roadmap formal.

---

## Testes Planejados

Como esta fase é documental, não haverá testes novos obrigatórios. A validação mínima é:

- `./gradlew testDebugUnitTest` deve passar.
- `./scripts/check-android.sh` deve passar.
- APK deve permanecer sem `android.permission.INTERNET`.
- Nenhum arquivo perigoso deve aparecer no Git status.
- Nenhum path local deve ser inserido nos documentos.
- Nenhum código de UI real deve ser criado.
- Nenhum storage real deve ser criado.

---

## Critérios de Aceite

- `ANDROID_FOUNDATION_PHASE_36_PLAN.md` criado.
- `SECURE_FRONTEND_ARCHITECTURE.md` criado.
- `SECURE_FRONTEND_RENDERING_RULES.md` criado.
- `SECURE_FRONTEND_STATE_MODEL.md` criado.
- Nenhum código de UI real criado.
- Nenhum decrypt conectado à UI.
- Nenhum storage real criado.
- Nenhuma escrita em disco criada.
- Nenhuma leitura de disco criada.
- Nenhuma permissão Android adicionada.
- Nenhum Android Keystore criado.
- Nenhuma biometria criada.
- `./gradlew testDebugUnitTest` passa.
- `./scripts/check-android.sh` passa.
- APK permanece sem `android.permission.INTERNET`.
- Roadmap formal não é alterado nesta fase.
