# Vaultia – Tecnologias

## Objetivo do Documento

Este documento define as tecnologias permitidas, preferenciais, condicionais e proibidas para o Vaultia Android v1.

O objetivo é evitar decisões improvisadas durante a implementação e garantir alinhamento com o modelo offline-only, security-first e local-first do produto.

## Princípio Central

Tecnologia no Vaultia deve ser escolhida por segurança, previsibilidade, manutenção e simplicidade.

Não será adotada biblioteca apenas por conveniência visual, moda, velocidade aparente ou excesso de abstração.

## Plataforma Alvo

A v1 terá foco em Android.

Decisão:

- plataforma inicial: Android;
- abordagem: nativa;
- linguagem principal: Kotlin;
- UI: Jetpack Compose;
- IDE principal: Android Studio;
- editor auxiliar: VS Code.

Fora da v1:

- iOS;
- Web;
- Desktop;
- backend;
- painel administrativo;
- extensão de navegador.

## Linguagem Principal

Kotlin é a linguagem principal do app Android.

Motivos:

- integração nativa com Android;
- compatibilidade com Jetpack;
- suporte forte a coroutines;
- tipagem estática;
- ecossistema maduro;
- menor atrito com APIs de segurança Android.

Regras:

- código Android v1 deve ser Kotlin;
- Java pode existir apenas por dependência ou interoperabilidade inevitável;
- Python não será usado dentro do app mobile;
- scripts Python podem existir apenas como ferramentas internas fora do app.

## Interface

A interface será construída com Jetpack Compose.

Motivos:

- stack moderna Android;
- menor dependência de XML;
- bom suporte a estados;
- integração com ViewModel;
- boa produtividade;
- manutenção futura mais clara.

Regras:

- Compose deve ser usado para novas telas;
- UI não deve executar criptografia;
- UI não deve acessar banco diretamente;
- UI não deve acessar Keystore diretamente;
- telas sensíveis devem respeitar política de proteção visual.

## Arquitetura de UI

Tecnologias preferenciais:

- Jetpack Compose;
- ViewModel;
- StateFlow ou equivalente;
- Navigation Compose, se necessário.

Regras:

- estado sensível deve ser minimizado;
- ViewModel não deve persistir plaintext além do necessário;
- estado de UI não deve conter Vault Key;
- telas bloqueadas não devem manter conteúdo sensível visível.

## Concorrência e Assincronismo

Tecnologias preferenciais:

- Kotlin Coroutines;
- Flow ou StateFlow.

Usos:

- operações de storage;
- operações criptográficas;
- importação e exportação de backup;
- leitura de arquivos;
- bloqueio por inatividade;
- eventos de sessão.

Regras:

- operações pesadas não devem travar a UI;
- KDF não deve bloquear thread principal;
- criptografia de arquivos grandes não deve travar a UI;
- erros assíncronos devem ser tratados de forma segura.

## Persistência Local

Opções permitidas para avaliação:

- Room;
- SQLCipher;
- DataStore;
- armazenamento privado de arquivos;
- abordagem híbrida com banco local + arquivos criptografados.

Decisão preliminar:

- metadados e registros: banco local;
- arquivos binários: storage privado do app;
- conteúdo sensível: criptografado antes de persistir;
- nomes reais de arquivos: tratados como sensíveis.

A escolha final entre Room com criptografia por campo/registro e SQLCipher fica aberta até prova técnica.

## DataStore

DataStore pode ser usado para configurações não críticas ou confidenciais de baixo risco.

Usos possíveis:

- onboarding concluído;
- preferência de tema;
- configuração de auto-lock;
- flag de biometria ativada;
- versão local de configuração.

Regras:

- não armazenar senha-mestra;
- não armazenar Vault Key;
- não armazenar conteúdo do cofre;
- não armazenar títulos, URLs, tags ou nomes reais de arquivos;
- avaliar proteção adicional para configurações sensíveis.

## Banco Local

Banco local pode ser usado para registros do cofre, desde que dados Nível 3 não fiquem em plaintext.

Regras:

- banco não deve conter plaintext sensível;
- migrations devem ser versionadas;
- schema deve ser revisado;
- busca local não deve exigir índice plaintext de conteúdo sensível na v1;
- inspeção manual deve confirmar ausência de conteúdo legível.

## Arquivos Locais

Arquivos privados serão armazenados no storage privado do app.

Regras:

- arquivo deve ser criptografado antes de persistir;
- nome físico deve usar identificador não semântico;
- extensão real não deve revelar conteúdo sensível se evitável;
- temporários devem ser limpos;
- app não deve salvar cópia permanente na galeria;
- app não deve usar storage público sem necessidade.

## Criptografia

Tecnologias e primitivas preferenciais:

- AEAD;
- AES-256-GCM;
- Android Keystore;
- SecureRandom;
- Argon2id, se biblioteca Android confiável for aprovada;
- PBKDF2 forte como alternativa aceitável.

Regras:

- não usar criptografia própria;
- não usar senha diretamente como chave AES;
- não usar MD5;
- não usar SHA-1 para segurança criptográfica;
- não usar AES-ECB;
- não usar AES-CBC sem autenticação;
- não usar Base64 como criptografia;
- biblioteca criptográfica exige revisão explícita.

## Android Keystore

Android Keystore deve ser usado quando aplicável.

Regras:

- preferir chaves não exportáveis;
- preferir hardware-backed ou StrongBox quando disponível;
- lidar com fallback seguro;
- não prometer segurança absoluta baseada apenas em Keystore;
- não usar Keystore para esconder arquitetura ruim.

## Biometria

Tecnologia preferencial:

- Android BiometricPrompt;
- Jetpack Biometric, se necessário.

Regras:

- biometria é opcional;
- biometria é conveniência;
- biometria não substitui totalmente Master Password;
- Master Password continua sendo fallback;
- ações críticas podem exigir Master Password.

## Logs

Tecnologia:

- logger central próprio ou wrapper controlado sobre logging Android.

Regras:

- proibir uso livre de `Log.d`;
- proibir `println`;
- proibir `printStackTrace`;
- logs devem ser sanitizados;
- release não deve conter debug logs sensíveis;
- nenhum log remoto na v1.

## Auditoria Local

A auditoria local deve ser implementada como componente interno.

Tecnologias possíveis:

- tabela local;
- arquivo local protegido;
- storage interno estruturado.

Regras:

- não enviar auditoria para servidor;
- não registrar conteúdo do cofre;
- não registrar título, URL, senha, nota ou nome real de arquivo;
- limitar retenção;
- exibir eventos compreensíveis ao usuário.

## Build System

Tecnologias esperadas:

- Gradle;
- Android Gradle Plugin;
- Kotlin Gradle Plugin.

Regras:

- build release deve ser separado de debug;
- release deve ter `debuggable=false`;
- assinatura release deve ser protegida;
- versionCode e versionName devem ser controlados;
- comandos de build devem ser documentados.

## Testes

Tecnologias possíveis:

- JUnit;
- Kotlin Test;
- AndroidX Test;
- Compose UI Test;
- Robolectric, se fizer sentido;
- scripts locais para inspeção.

Tipos de teste obrigatórios no futuro:

- teste de domínio;
- teste de criptografia;
- teste de storage;
- teste de backup;
- teste de permissões;
- teste de ausência de plaintext;
- teste de UI sensível;
- teste em aparelho real.

## CI/CD

Ferramenta permitida:

- GitHub Actions.

Regras:

- CI não deve receber dados reais do cofre;
- CI não deve armazenar keystore de release sem decisão explícita;
- CI deve rodar checks;
- CI futuro deve bloquear permissão `INTERNET`;
- CI futuro deve bloquear `allowBackup=true`;
- CI futuro deve procurar padrões proibidos de logs.

## Docker

Docker pode ser usado apenas para tooling auxiliar.

Usos possíveis:

- scripts de auditoria;
- documentação;
- ambiente de checks;
- ferramentas auxiliares não mobile.

Proibido:

- processar dados reais do cofre;
- simular backend v1;
- criar dependência operacional;
- armazenar secrets reais.

## Python

Python é permitido apenas como ferramenta interna fora do app.

Usos possíveis:

- geração de documentação;
- auditoria de arquivos;
- validação de checklist;
- scripts de inspeção;
- utilitários locais.

Proibido:

- usar Python como runtime do app Android;
- processar dados reais do cofre;
- incluir scripts que exijam internet para v1.

## Postman

Postman não é necessário para a v1 offline-only.

Uso permitido:

- documentação futura de contratos caso um backend seja planejado em versão posterior.

Proibido na v1:

- criar dependência de API;
- simular backend obrigatório;
- enviar dados reais do cofre.

## Terraform

Terraform não é necessário para a v1 offline-only.

Uso permitido:

- infraestrutura pública futura;
- documentação futura de ambiente externo, se houver.

Proibido na v1:

- criar backend obrigatório;
- criar storage remoto do cofre;
- usar secrets reais do produto.

## Dependências Proibidas

São proibidas na v1:

- SDK de analytics remoto;
- SDK de crash reporting remoto;
- SDK de ads;
- SDK de tracking;
- SDK de social login;
- SDK de cloud sync;
- SDK que exija `INTERNET`;
- biblioteca criptográfica obscura sem revisão;
- dependência não mantida para função crítica;
- dependência que adicione permissões indevidas.

## Critérios para Aprovar Dependência

Antes de adicionar dependência, responder:

- qual problema ela resolve?
- é necessária para v1?
- adiciona permissão?
- faz tráfego de rede?
- é mantida?
- é amplamente usada?
- é crítica para segurança?
- tem alternativa nativa?
- aumenta superfície de ataque?
- foi documentada?

Se a resposta for incerta em segurança, a dependência não deve entrar.

## Tecnologias Fora de Escopo

Fora da v1:

- React Native;
- Flutter;
- backend NestJS;
- Node.js runtime;
- banco remoto;
- Redis;
- PostgreSQL;
- Firebase;
- Supabase;
- AWS Amplify;
- Google Analytics;
- Sentry remoto;
- OAuth;
- JWT;
- GraphQL;
- REST API própria;
- sincronização cloud.

## Decisões Abertas

Ficam abertas até prova técnica:

- Room com criptografia por campo versus SQLCipher;
- biblioteca final de Argon2id;
- parâmetros finais de KDF;
- uso exato de DataStore;
- organização física em módulos Gradle ou pacotes;
- estratégia final de busca local sem índice plaintext;
- ferramenta de auditoria local de plaintext.

## Critérios de Aceite

Este documento será considerado aceito quando:

- stack Android estiver definida;
- Kotlin estiver definido como linguagem principal;
- Compose estiver definido como UI;
- tecnologias permitidas estiverem listadas;
- tecnologias proibidas estiverem listadas;
- dependências críticas exigirem revisão;
- ferramentas auxiliares estiverem delimitadas;
- decisões abertas estiverem explícitas;
- documento estiver alinhado com product decisions e security requirements.

## Status do Documento

- **Documento:** `docs/SDD/03-arquitetura/03-tecnologias.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Arquitetura Detalhada Pré-Implementação
