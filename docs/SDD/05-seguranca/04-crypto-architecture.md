# Vaultia – Crypto Architecture

## Objetivo do Documento

Este documento define a arquitetura criptográfica do Vaultia Android v1.

O objetivo é estabelecer como o cofre protege dados sensíveis em repouso, como chaves são derivadas ou protegidas, como dados são criptografados, como backups são protegidos e quais práticas são proibidas.

Este documento não contém implementação de código. Ele define regras arquiteturais obrigatórias.

## Princípio Central

O Vaultia não inventa criptografia.

A v1 deve usar primitivas criptográficas conhecidas, autenticadas, disponíveis em bibliotecas confiáveis e com comportamento previsível no Android.

Qualquer implementação criptográfica própria é proibida.

## Objetivos Criptográficos

A arquitetura criptográfica da v1 deve garantir:

- confidencialidade dos dados do cofre;
- integridade dos dados criptografados;
- autenticação do conteúdo criptografado;
- proteção contra leitura offline simples;
- resistência razoável contra tentativa de senha offline;
- separação entre senha-mestra e chave de dados;
- versionamento do formato criptográfico;
- rejeição segura de senha incorreta;
- rejeição segura de dados corrompidos;
- proteção de backups exportados.

## Fora de Objetivo Criptográfico

A v1 não promete:

- proteção total contra sistema operacional comprometido;
- proteção total contra malware com root;
- proteção total contra keylogger;
- proteção total contra atacante observando a tela durante o uso;
- proteção contra senha-mestra fraca escolhida pelo usuário;
- recuperação de dados sem senha-mestra;
- invulnerabilidade forense;
- criptografia própria superior a padrões existentes.

## Componentes Conceituais

A arquitetura usa os seguintes conceitos:

- Master Password;
- KDF;
- Vault Key;
- Key Encryption Key;
- Android Keystore;
- Salt;
- Nonce ou IV;
- AEAD;
- Crypto Metadata;
- Backup Package;
- Crypto Version.

## Master Password

A Master Password é a senha memorizada pelo usuário.

Regras:

- não deve ser armazenada em texto claro;
- não deve ser enviada pela internet;
- não deve ser registrada em logs;
- não deve ser incluída em backup;
- não deve aparecer em crash report;
- deve ser usada apenas localmente;
- deve ser usada para derivar ou desbloquear material criptográfico;
- deve ser exigida para criar e recuperar acesso ao cofre.

Se a Master Password for perdida, o cofre pode se tornar irrecuperável.

## Vault Key

A Vault Key é a chave usada para proteger os dados do cofre.

Regras:

- não deve ser hardcoded;
- não deve ser logada;
- não deve ser exportada em texto claro;
- não deve ser salva desprotegida;
- deve ser protegida por material derivado da Master Password e/ou Android Keystore;
- deve ser tratada como Nível 3 — Restrito / Crítico;
- deve ser carregada apenas durante sessão desbloqueada;
- deve ser descartada da memória quando possível ao bloquear o cofre.

## Key Encryption Key

A Key Encryption Key é usada para proteger a Vault Key.

Ela pode ser derivada da Master Password por meio de KDF e/ou protegida com Android Keystore, conforme o desenho final de implementação.

Regras:

- não deve criptografar diretamente todos os dados do usuário se houver Vault Key dedicada;
- não deve ser armazenada em texto claro;
- não deve ser logada;
- deve ser recriada ou desbloqueada localmente quando necessário;
- deve permitir troca futura da Master Password sem recriptografar todos os dados, se possível.

## KDF

KDF significa Key Derivation Function.

A KDF transforma a Master Password em material criptográfico resistente a tentativas offline.

Decisão inicial:

- Argon2id é preferido se houver implementação Android confiável, mantida e adequada.
- PBKDF2 com parâmetros fortes é alternativa aceitável se a opção Argon2id disponível não for confiável.

Regras:

- KDF é obrigatória;
- salt por cofre é obrigatório;
- parâmetros da KDF devem ser versionados;
- parâmetros devem considerar desempenho real em aparelhos Android;
- parâmetros devem ser revisados antes de release;
- senha fraca continua sendo risco residual;
- KDF não substitui exigência de senha-mestra forte.

## Salt

Salt é valor aleatório usado na derivação de chave.

Regras:

- deve ser único por cofre;
- deve ser gerado com fonte segura de aleatoriedade;
- deve ser armazenado junto aos metadados criptográficos do cofre;
- não precisa ser secreto;
- não deve ser reutilizado entre cofres;
- não deve ser logado desnecessariamente.

## AEAD

A v1 deve usar criptografia autenticada.

Algoritmo preferencial:

- AES-256-GCM.

Alternativa possível, mediante revisão:

- ChaCha20-Poly1305.

Regras:

- AES-ECB é proibido;
- AES-CBC sem autenticação é proibido;
- Base64 não é criptografia;
- hash não é criptografia reversível;
- algoritmo próprio é proibido;
- modo sem autenticação é proibido;
- integridade deve ser verificada antes de aceitar dados descriptografados.

## Nonce / IV

Cada operação de criptografia que exija nonce ou IV deve usar valor único.

Regras:

- nonce ou IV não deve ser reutilizado com a mesma chave;
- nonce ou IV deve ser gerado com fonte segura ou conforme recomendação da primitiva usada;
- nonce ou IV deve ser armazenado com o ciphertext;
- nonce ou IV não precisa ser secreto;
- nonce ou IV não deve ser logado;
- falha de geração de nonce deve abortar a operação.

## Randomness

Toda geração criptográfica deve usar fonte segura de aleatoriedade da plataforma Android.

Usos:

- Vault Key;
- salt;
- nonce ou IV;
- identificadores internos não previsíveis;
- material de backup quando necessário.

Regras:

- não usar Random comum para material criptográfico;
- não usar timestamp como fonte de segredo;
- não usar UUID como chave criptográfica;
- não usar valor determinístico para nonce;
- não usar senha do usuário diretamente como chave AES.

## Formato Criptográfico Versionado

Todo dado criptografado deve carregar metadados mínimos de versão.

Campos conceituais:

- cryptoVersion: versão do formato criptográfico;
- kdf: função de derivação usada;
- cipher: algoritmo de criptografia autenticada;
- saltRef: referência ao salt do cofre;
- nonce: valor único por registro ou arquivo;
- createdAt: metadado local, se necessário.

Regras:

- versão criptográfica é obrigatória;
- mudanças no formato exigem nova versão;
- migração deve ser planejada antes de alterar formato;
- app deve rejeitar versão desconhecida de forma segura;
- erro não deve vazar conteúdo sensível.

## Criptografia de Notas

Notas seguras são Nível 3 — Restrito / Crítico.

Regras:

- conteúdo da nota deve ser criptografado;
- título da nota deve ser tratado como sensível;
- tags devem ser tratadas como sensíveis;
- busca não deve criar índice em texto claro para conteúdo sensível na v1;
- nota só pode ser descriptografada com cofre desbloqueado;
- nota não deve aparecer em logs;
- nota não deve ser exposta em preview do app.

## Criptografia de Senhas

Itens de senha são Nível 3 — Restrito / Crítico.

Regras:

- senha salva deve ser criptografada;
- título deve ser tratado como sensível;
- usuário ou e-mail deve ser tratado como sensível;
- URL deve ser tratada como sensível;
- observações devem ser tratadas como sensíveis;
- senha não deve aparecer em logs;
- senha só pode ir para clipboard por ação explícita;
- clipboard deve ser limpo após tempo definido.

## Criptografia de Arquivos

Fotos e documentos são Nível 3 — Restrito / Crítico.

Regras:

- cada arquivo deve ser criptografado antes de persistir;
- arquivo criptografado deve ficar em storage privado do app;
- nome original do arquivo deve ser tratado como sensível;
- metadados do arquivo devem ser tratados como sensíveis;
- temporários descriptografados devem ser evitados ou limpos rapidamente;
- visualização deve usar telas protegidas;
- arquivo não deve aparecer na galeria por ação do app;
- arquivo grande não deve travar a UI.

## Backup Criptografado

Backup é Nível 3 — Restrito / Crítico.

Regras:

- backup deve ser criptografado;
- backup deve ter integridade verificável;
- backup deve ter formato versionado;
- backup não deve conter plaintext;
- backup não deve ser JSON, CSV ou TXT aberto;
- backup deve exigir senha correta para restauração;
- backup corrompido deve ser rejeitado;
- senha errada deve falhar de forma segura;
- exportação deve exigir confirmação do usuário;
- importação deve exigir confirmação antes de substituir cofre existente.

Campos conceituais do pacote de backup:

- backupVersion;
- cryptoVersion;
- kdf;
- cipher;
- encryptedPayload;
- integrityProtectedMetadata.

## Troca de Senha-Mestra

A troca de Master Password deve ser tratada como operação crítica.

Regras:

- exigir Master Password atual;
- exigir nova Master Password forte;
- confirmar nova Master Password;
- reprocessar proteção da Vault Key;
- não expor Vault Key em logs;
- não recriptografar todo conteúdo se a arquitetura permitir apenas rewrap da Vault Key;
- registrar evento de auditoria sanitizado;
- falha no processo não deve corromper cofre existente.

## Sessão Desbloqueada

Durante sessão desbloqueada, dados Nível 3 podem existir temporariamente em memória.

Regras:

- manter sessão desbloqueada pelo menor tempo razoável;
- bloquear ao background;
- bloquear por inatividade;
- limpar referências quando possível;
- não persistir cache descriptografado;
- não escrever temporários sem necessidade;
- não enviar dados a outros apps sem ação explícita.

## Android Keystore

Android Keystore deve ser usado quando aplicável para proteger material criptográfico local.

Regras:

- preferir chaves não exportáveis;
- preferir hardware-backed ou StrongBox quando disponível;
- exigir autenticação do usuário quando adequado;
- lidar com indisponibilidade de hardware-backed Keystore;
- não assumir que todos os aparelhos têm o mesmo nível de proteção;
- registrar modo de proteção local apenas de forma sanitizada;
- nunca vender Keystore como garantia absoluta.

## Biometria e Criptografia

Biometria é mecanismo de conveniência.

Regras:

- biometria não substitui totalmente a Master Password;
- biometria não recupera cofre perdido;
- biometria pode desbloquear material local protegido;
- ações críticas podem exigir Master Password;
- alteração de biometria no dispositivo deve invalidar ou restringir o acesso biométrico quando possível;
- fallback para Master Password deve existir.

## Tratamento de Erros Criptográficos

Erros criptográficos devem ser seguros e genéricos.

Permitido:

- "Senha incorreta ou dados inválidos."
- "Não foi possível abrir o cofre."
- "Backup inválido ou corrompido."

Proibido:

- revelar se a senha estava quase correta;
- revelar detalhes internos da chave;
- revelar plaintext parcial;
- revelar stack trace ao usuário;
- logar material criptográfico;
- diferenciar erro de forma útil para atacante offline.

## Logs Criptográficos

A categoria crypto pode registrar apenas eventos sanitizados.

Permitido:

- VAULT_DECRYPTION_FAILED;
- BACKUP_INTEGRITY_CHECK_FAILED;
- CRYPTO_VERSION_UNSUPPORTED;
- KEYSTORE_OPERATION_FAILED.

Proibido:

- Master Password;
- Vault Key;
- Key Encryption Key;
- salt;
- nonce;
- IV;
- plaintext;
- ciphertext;
- hash de senha;
- conteúdo do cofre;
- caminho completo de arquivo sensível.

## Práticas Proibidas

São proibidas na v1:

- criptografia própria;
- senha do usuário usada diretamente como chave AES;
- chave hardcoded;
- segredo no repositório;
- AES-ECB;
- AES-CBC sem autenticação;
- nonce reutilizado com mesma chave;
- Base64 como criptografia;
- MD5;
- SHA-1 para segurança criptográfica;
- armazenamento de senha-mestra;
- logs com conteúdo sensível;
- backup aberto;
- cache persistente em texto claro;
- exportação plaintext;
- uso de dados reais em testes automatizados;
- dependência criptográfica sem revisão.

## Testes Obrigatórios

A arquitetura criptográfica exige testes para:

- criar cofre;
- desbloquear com senha correta;
- rejeitar senha errada;
- rejeitar dados corrompidos;
- criptografar nota;
- recuperar nota após reabrir app;
- confirmar ausência de plaintext no storage;
- exportar backup criptografado;
- rejeitar backup corrompido;
- rejeitar backup com senha errada;
- validar versionamento criptográfico;
- validar ausência de logs sensíveis.

## Decisões Abertas

As seguintes decisões ficam abertas até implementação técnica:

- escolha final entre Argon2id e PBKDF2;
- biblioteca específica de KDF;
- uso de SQLCipher ou criptografia por campo/registro;
- modelo exato de wrapping da Vault Key;
- estratégia final de busca local sem índice plaintext;
- parâmetros finais de KDF por classe de aparelho;
- política exata de expiração de sessão;
- formato binário final do backup.

Essas decisões exigem revisão antes da implementação.

## Critérios de Aceite

Este documento será considerado aceito quando:

- Master Password estiver definida conceitualmente;
- Vault Key estiver definida conceitualmente;
- KDF estiver definida com decisão preferencial e alternativa;
- AEAD estiver definido como obrigatório;
- AES-256-GCM estiver definido como preferencial;
- salt, nonce e versionamento estiverem definidos;
- backup criptografado estiver especificado;
- práticas proibidas estiverem listadas;
- riscos residuais estiverem claros;
- decisões abertas estiverem declaradas;
- o documento estiver alinhado com threat model e data classification.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/04-crypto-architecture.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
