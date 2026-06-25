# Vaultia – Key Management

## Objetivo do Documento

Este documento define a política de gerenciamento de chaves do Vaultia Android v1.

O objetivo é estabelecer como senhas, chaves derivadas, Vault Key, Key Encryption Key, Android Keystore e material temporário devem ser criados, protegidos, usados, rotacionados e descartados.

## Princípio Central

Chaves são o núcleo de segurança do Vaultia.

Nenhuma chave deve ser hardcoded, logada, exportada em texto claro, armazenada sem proteção ou exposta desnecessariamente para UI, logs, testes, backup ou ferramentas externas.

## Escopo

Este documento cobre:

- Master Password;
- KDF;
- salt;
- Vault Key;
- Key Encryption Key;
- Android Keystore;
- chaves biométricas;
- material temporário em memória;
- rotação de senha;
- backup;
- logs;
- testes;
- critérios de aceite.

## KM-001 — Master Password

A Master Password é a senha memorizada pelo usuário.

Regras:

- nunca armazenar Master Password em texto claro;
- nunca logar Master Password;
- nunca enviar Master Password pela internet;
- nunca incluir Master Password em backup;
- nunca salvar Master Password em DataStore;
- nunca salvar Master Password em banco;
- nunca salvar Master Password em arquivo;
- usar Master Password apenas para derivar/desbloquear material criptográfico;
- descartar referência da Master Password quando possível.

Critérios de aceite:

- busca em storage não encontra Master Password;
- busca em logs não encontra Master Password;
- app não oferece recuperação falsa de senha.

## KM-002 — Ausência de Recuperação

A arquitetura de chaves não deve permitir recuperação remota.

Regras:

- não existe chave guardada por servidor;
- não existe chave mestra do fornecedor;
- não existe escrow;
- não existe reset remoto;
- não existe suporte capaz de abrir o cofre;
- perda da Master Password pode significar perda definitiva do acesso.

Critérios de aceite:

- onboarding comunica ausência de recuperação;
- política pública não contradiz essa regra;
- implementação não cria mecanismo oculto de recuperação.

## KM-003 — KDF

A Master Password deve passar por KDF antes de proteger material criptográfico.

Decisão inicial:

- Argon2id é preferencial se houver biblioteca Android confiável;
- PBKDF2 forte é alternativa aceitável se Argon2id não for aprovado.

Regras:

- KDF é obrigatória;
- parâmetros devem ser versionados;
- parâmetros devem considerar aparelho real;
- senha não pode ser usada diretamente como chave AES;
- parâmetros devem ser revisados antes de release;
- KDF deve falhar de forma segura.

Critérios de aceite:

- parâmetros persistidos e versionados;
- senha errada não desbloqueia;
- teste em aparelho real mede tempo aceitável.

## KM-004 — Salt

Cada cofre deve possuir salt próprio para KDF.

Regras:

- salt deve ser gerado com fonte segura de aleatoriedade;
- salt deve ser único por cofre;
- salt pode ser persistido como metadado criptográfico;
- salt não precisa ser secreto;
- salt não deve ser reutilizado entre cofres;
- salt não deve ser logado desnecessariamente.

Critérios de aceite:

- cofre possui salt;
- salt não é valor fixo;
- salt não é hardcoded.

## KM-005 — Vault Key

Vault Key é a chave de dados do cofre.

Regras:

- deve ser aleatória;
- deve ser gerada com fonte segura;
- não deve derivar diretamente de timestamp;
- não deve ser UUID;
- não deve ser hardcoded;
- não deve ser logada;
- não deve ser exportada em texto claro;
- não deve ser armazenada desprotegida;
- deve ser protegida por KEK ou mecanismo equivalente;
- deve ser tratada como Nível 3.

Critérios de aceite:

- Vault Key não aparece no código;
- Vault Key não aparece nos logs;
- Vault Key não aparece em storage aberto;
- dados do cofre dependem da Vault Key para acesso.

## KM-006 — Key Encryption Key

Key Encryption Key protege a Vault Key.

Regras:

- pode ser derivada da Master Password por KDF;
- pode ser integrada ao Android Keystore conforme desenho final;
- não deve criptografar diretamente todos os dados se houver Vault Key dedicada;
- não deve ser armazenada em texto claro;
- não deve ser logada;
- deve permitir troca de Master Password via rewrap da Vault Key, se possível.

Critérios de aceite:

- troca de senha não exige recriptografar todo conteúdo se modelo permitir;
- KEK não aparece em logs;
- KEK não aparece em storage aberto.

## KM-007 — Android Keystore

Android Keystore deve ser usado quando aplicável.

Regras:

- preferir chaves não exportáveis;
- preferir hardware-backed ou StrongBox quando disponível;
- lidar com fallback quando indisponível;
- não depender exclusivamente de Keystore para segurança total;
- não usar Keystore como desculpa para armazenar plaintext;
- não expor chaves do Keystore para UI.

Critérios de aceite:

- implementação detecta suporte adequado;
- fallback é documentado;
- app não promete segurança absoluta baseada em hardware.

## KM-008 — Biometria e Chaves

Biometria pode proteger desbloqueio local, mas não substitui Master Password.

Regras:

- biometria é opcional;
- biometria só pode ser ativada após criação do cofre;
- Master Password permanece fallback;
- ações críticas podem exigir Master Password;
- alteração biométrica deve invalidar ou restringir acesso quando possível;
- biometria não deve ser comunicada como recuperação garantida.

Critérios de aceite:

- app funciona sem biometria;
- biometria não recupera cofre perdido;
- desativação de biometria funciona.

## KM-009 — Sessão Desbloqueada

Durante sessão desbloqueada, material sensível pode existir temporariamente em memória.

Regras:

- manter sessão pelo menor tempo razoável;
- bloquear por background;
- bloquear por inatividade;
- evitar cache global de plaintext;
- evitar singleton expondo Vault Key;
- descartar referências quando possível;
- não persistir material descriptografado.

Critérios de aceite:

- dados Nível 3 só aparecem com sessão desbloqueada;
- app bloqueia ao background conforme política;
- não há cache persistente aberto.

## KM-010 — Troca de Master Password

A troca de Master Password é operação crítica.

Regras:

- exigir Master Password atual;
- exigir nova Master Password;
- confirmar nova Master Password;
- validar força mínima;
- derivar novo material de proteção;
- reproteger Vault Key;
- não logar chaves;
- não corromper cofre em caso de falha;
- registrar auditoria sanitizada.

Critérios de aceite:

- senha antiga deixa de funcionar;
- senha nova funciona;
- dados permanecem acessíveis;
- falha não destrói cofre.

## KM-011 — Rotação de Chaves

Rotação de Vault Key não é obrigatória na v1 inicial.

Regras:

- arquitetura deve permitir evolução futura;
- rotação futura exige migração controlada;
- rotação futura exige backup/testes;
- mudança de formato exige versionamento;
- não implementar rotação improvisada.

Critérios de aceite:

- decisão fica documentada;
- versão criptográfica permite evolução futura.

## KM-012 — Backup e Chaves

Backup deve proteger dados e material necessário à restauração sem expor chaves em texto claro.

Regras:

- backup deve ser criptografado;
- backup deve ter integridade verificável;
- backup deve conter metadados criptográficos necessários;
- backup não deve conter Vault Key aberta;
- backup não deve conter Master Password;
- backup não deve conter plaintext;
- importação exige senha correta;
- senha errada deve falhar.

Critérios de aceite:

- backup não contém plaintext;
- backup não contém chave aberta;
- backup corrompido é rejeitado;
- senha errada é rejeitada.

## KM-013 — Logs

Chaves e material derivado nunca devem aparecer em logs.

Proibido:

- Master Password;
- Vault Key;
- Key Encryption Key;
- chaves biométricas;
- hash de senha;
- plaintext;
- ciphertext;
- salt, salvo decisão técnica explícita e sanitizada;
- nonce;
- IV;
- path sensível associado a chave.

Critérios de aceite:

- revisão de Logcat não encontra material sensível;
- logger central bloqueia padrões proibidos.

## KM-014 — Testes

Testes devem usar material fictício.

Regras:

- não usar senha real;
- não usar backup real;
- não usar chave real;
- não usar dados reais;
- fixtures devem ser artificiais;
- testes de erro não devem imprimir segredo.

Critérios de aceite:

- repositório não contém segredo real;
- logs de teste não contêm material sensível.

## KM-015 — Proibições Absolutas

São proibidos:

- chave hardcoded;
- Master Password armazenada;
- Vault Key em plaintext;
- KEK em plaintext;
- senha usada diretamente como chave AES;
- UUID como chave;
- timestamp como segredo;
- `Random` comum para chave;
- log de chave;
- backup com chave aberta;
- recuperação oculta;
- crypto própria.

## Decisões Abertas

Ficam abertas até prova técnica:

- biblioteca final de Argon2id;
- parâmetros finais de KDF;
- modelo exato de wrapping da Vault Key;
- uso exato do Android Keystore;
- comportamento em aparelhos sem hardware-backed Keystore;
- política final de expiração de sessão;
- política final de invalidação biométrica.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- Master Password estiver regulada;
- KDF estiver regulada;
- salt estiver regulado;
- Vault Key estiver regulada;
- KEK estiver regulada;
- Android Keystore estiver regulado;
- biometria e chaves estiverem delimitadas;
- troca de senha estiver definida;
- backup e chaves estiverem definidos;
- logs e testes estiverem definidos;
- proibições absolutas estiverem claras;
- documento estiver alinhado com crypto architecture e security requirements.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/05-key-management.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Segurança Pré-Implementação
