# Vaultia – Logging Policy

## Objetivo do Documento

Este documento define a política de logs do Vaultia Android v1.

O objetivo é impedir que logs técnicos, auditoria local, mensagens de erro, testes, builds e evidências exponham dados sensíveis do cofre.

## Princípio Central

Log é superfície de vazamento.

No Vaultia, nenhum log pode conter conteúdo do cofre, segredo criptográfico, senha, chave, metadado sensível ou dado privado identificável.

## Escopo

Este documento cobre:

- logs técnicos;
- Logcat;
- auditoria local;
- mensagens de erro;
- logs de build;
- logs de teste;
- logs de CI;
- screenshots e evidências;
- proibições;
- padrões permitidos;
- critérios de aceite.

## LP-001 — Logger Central

O app deve usar logger central ou wrapper controlado.

Regras:

- proibir uso livre de `Log.d`;
- proibir uso livre de `Log.i`;
- proibir uso livre de `Log.w`;
- proibir uso livre de `Log.e`;
- proibir `println`;
- proibir `printStackTrace`;
- centralizar sanitização;
- permitir desligar logs sensíveis em release;
- impedir logs acidentais de conteúdo do usuário.

Critérios de aceite:

- código usa logger central;
- revisão busca usos proibidos;
- release não contém debug logs indevidos.

## LP-002 — Dados Proibidos em Logs

É proibido logar:

- Master Password;
- senha salva;
- conteúdo de nota;
- título de nota;
- URL salva;
- usuário/e-mail salvo;
- tags;
- nome real de arquivo;
- conteúdo de documento;
- conteúdo de foto;
- Vault Key;
- Key Encryption Key;
- chaves derivadas;
- hash de senha;
- plaintext;
- ciphertext;
- clipboard;
- backup;
- path sensível;
- stack trace com path ou conteúdo sensível.

Critérios de aceite:

- busca em Logcat não encontra dados Nível 3;
- logs de teste não contêm dados reais;
- logs de build não contêm secrets.

## LP-003 — Dados Permitidos em Logs Técnicos

Logs técnicos podem conter apenas dados sanitizados.

Permitido:

- tipo genérico de evento;
- código interno de erro;
- timestamp local;
- resultado genérico;
- componente lógico;
- estado genérico;
- identificador local não sensível;
- versão do app;
- versão do schema;
- versão criptográfica;
- mensagem técnica sem conteúdo privado.

Exemplos permitidos:

- `VAULT_UNLOCK_FAILED`;
- `BACKUP_IMPORT_FAILED`;
- `CRYPTO_VERSION_UNSUPPORTED`;
- `STORAGE_WRITE_FAILED`;
- `KEYSTORE_OPERATION_FAILED`;
- `SESSION_LOCKED_BY_TIMEOUT`.

Critérios de aceite:

- log permite diagnóstico sem vazar conteúdo;
- eventos são genéricos.

## LP-004 — Logs Criptográficos

Logs criptográficos exigem máxima restrição.

Permitido:

- falha genérica de descriptografia;
- versão criptográfica não suportada;
- operação de Keystore falhou;
- integridade inválida;
- backup inválido;
- KDF indisponível.

Proibido:

- chave;
- senha;
- salt;
- nonce;
- IV;
- plaintext;
- ciphertext;
- hash de senha;
- parâmetro sensível associado a usuário;
- detalhe útil para ataque offline.

Critérios de aceite:

- falhas criptográficas não revelam material;
- logs não ajudam atacante a reduzir espaço de busca.

## LP-005 — Auditoria Local Não é Log Técnico

Auditoria local é destinada ao usuário e deve ser separada de log técnico.

Eventos permitidos:

- cofre criado;
- cofre desbloqueado;
- falha de desbloqueio;
- cofre bloqueado;
- item criado;
- item editado;
- item excluído;
- backup exportado;
- backup importado;
- biometria ativada;
- biometria desativada;
- Master Password alterada.

Proibido na auditoria:

- título do item;
- conteúdo do item;
- URL;
- usuário/e-mail salvo;
- senha;
- nome real de arquivo;
- path;
- chave;
- erro técnico detalhado.

Critérios de aceite:

- auditoria é útil sem revelar dados Nível 3;
- auditoria fica local;
- auditoria possui retenção limitada.

## LP-006 — Mensagens de Erro para Usuário

Mensagens para usuário devem ser seguras.

Permitido:

- "Não foi possível abrir o cofre."
- "Senha incorreta ou dados inválidos."
- "Backup inválido ou corrompido."
- "Não foi possível concluir a operação."
- "Tente novamente."

Proibido:

- stack trace;
- nome de classe interna;
- path de arquivo sensível;
- detalhe de chave;
- detalhe de KDF;
- conteúdo parcial;
- diferença útil entre senha errada e estrutura interna inválida.

Critérios de aceite:

- mensagens são compreensíveis;
- mensagens não ajudam atacante.

## LP-007 — Logs de Build

Logs de build não podem conter secrets ou dados reais.

Proibido:

- keystore password;
- alias sensível;
- senha de assinatura;
- path privado com dados sensíveis;
- dados reais de cofre;
- backup real;
- credenciais;
- tokens;
- conteúdo de arquivo sensível.

Critérios de aceite:

- build output não revela segredo;
- evidências de build são sanitizadas.

## LP-008 — Logs de Teste

Testes devem usar dados fictícios.

Regras:

- usar senhas falsas;
- usar notas falsas;
- usar URLs falsas;
- usar arquivos fictícios;
- não usar backup real;
- não imprimir conteúdo sensível;
- não versionar logs com dados reais.

Critérios de aceite:

- logs de teste não contêm dados privados;
- fixtures são artificiais.

## LP-009 — Logs de CI

CI futuro deve seguir política de mínimo vazamento.

Regras:

- não imprimir secrets;
- mascarar variáveis sensíveis;
- não armazenar keystore release sem decisão explícita;
- não processar dados reais;
- não publicar artefatos com dados sensíveis;
- falhar se padrões proibidos forem encontrados.

Critérios de aceite:

- CI não expõe segredo;
- CI bloqueia padrões perigosos.

## LP-010 — Screenshots e Evidências

Evidências visuais também são superfície de vazamento.

Proibido em screenshots públicos:

- senha real;
- nota real;
- documento real;
- foto privada;
- URL real sensível;
- e-mail real salvo;
- nome real de arquivo sensível;
- backup real;
- path sensível.

Regras:

- usar dados fictícios;
- sanitizar prints;
- evitar mostrar tela com conteúdo Nível 3;
- evidências de release devem focar status, build, hash e checklist.

Critérios de aceite:

- prints públicos não vazam dados reais;
- evidências técnicas são sanitizadas.

## LP-011 — Política para Release

Build release deve ser conservador.

Regras:

- remover debug logs;
- não logar fluxo sensível;
- não incluir verbose logging;
- não incluir logs de desenvolvimento;
- não incluir stack traces visíveis ao usuário;
- logger central deve respeitar modo release.

Critérios de aceite:

- release não contém logs indevidos;
- release checklist valida Logcat.

## LP-012 — Padrões Proibidos no Código

Padrões a bloquear em revisão e CI futuro:

- `println`;
- `printStackTrace`;
- `Log.d`;
- `Log.v`;
- `Log.i` fora do logger central;
- concatenação de exception com dados sensíveis;
- log de request/response;
- log de payload;
- log de arquivo importado;
- log de clipboard;
- log de senha;
- log de backup.

Critérios de aceite:

- revisão manual não encontra padrões proibidos;
- CI futuro bloqueia padrões.

## LP-013 — Exceções

Exceções à política de logs devem ser raras.

Regras:

- exceção exige justificativa;
- exceção exige revisão de segurança;
- exceção deve ser documentada;
- exceção não pode incluir dado Nível 3;
- exceção não pode ir para release sem aprovação.

Critérios de aceite:

- nenhuma exceção silenciosa;
- exceções ficam rastreáveis.

## LP-014 — Retenção

Logs e auditoria devem ter retenção limitada.

Regras:

- não manter logs indefinidamente;
- auditoria local deve ter política de retenção;
- logs técnicos sensíveis não devem ser persistidos;
- usuário pode visualizar auditoria básica;
- app não deve acumular histórico excessivo.

Critérios de aceite:

- retenção definida antes do release;
- auditoria não cresce sem controle.

## LP-015 — Proibições Absolutas

São proibidos:

- log de Master Password;
- log de senha salva;
- log de nota;
- log de arquivo privado;
- log de chave;
- log de plaintext;
- log de ciphertext;
- log de backup;
- log remoto;
- analytics remoto;
- crash reporting remoto;
- stack trace visível ao usuário;
- print público com dados reais.

## Decisões Abertas

Ficam abertas até implementação:

- nome final do logger central;
- formato final dos eventos técnicos;
- formato final da auditoria local;
- retenção exata de auditoria;
- comandos finais de CI para detectar padrões proibidos;
- política de logs em debug local.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- logger central estiver definido como obrigatório;
- dados proibidos em logs estiverem listados;
- eventos permitidos estiverem definidos;
- auditoria local estiver separada de log técnico;
- mensagens de erro estiverem reguladas;
- logs de build, teste e CI estiverem regulados;
- screenshots e evidências estiverem regulados;
- padrões proibidos estiverem claros;
- documento estiver alinhado com data classification, secure storage e release checklist.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/08-logging-policy.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Segurança Pré-Implementação
