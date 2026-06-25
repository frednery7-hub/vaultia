# Vaultia – Security Requirements

## Objetivo do Documento

Este documento define os requisitos de segurança obrigatórios do Vaultia Android v1.

Ele transforma as decisões de produto, threat model, classificação de dados e arquitetura criptográfica em requisitos verificáveis.

## Princípio Central

Nenhuma funcionalidade é considerada pronta se violar um requisito deste documento.

Se houver conflito entre conveniência e segurança, a segurança prevalece.

## SR-001 — Modelo Offline-Only

O Vaultia Android v1 deve operar sem backend, sem sincronização remota e sem permissão de internet.

Requisitos:

- o app não deve declarar a permissão `INTERNET`;
- o app não deve fazer chamadas HTTP ou HTTPS;
- o app não deve depender de API remota;
- o app não deve enviar logs, métricas ou crash reports;
- o app não deve usar feature flags remotas;
- o app deve funcionar integralmente offline.

Critérios de aceite:

- `AndroidManifest.xml` não contém `android.permission.INTERNET`;
- testes manuais confirmam funcionamento sem rede;
- revisão de dependências confirma ausência de SDKs remotos desnecessários.

## SR-002 — Senha-Mestra

A Master Password deve ser o principal fator de acesso ao cofre.

Requisitos:

- não armazenar Master Password em texto claro;
- não transmitir Master Password;
- não logar Master Password;
- não incluir Master Password em backup;
- exigir Master Password na criação do cofre;
- exigir confirmação explícita de que não existe recuperação;
- aplicar medidor ou validação mínima de força;
- permitir reautenticação em ações críticas.

Critérios de aceite:

- senha-mestra não aparece em storage;
- senha-mestra não aparece em logs;
- onboarding informa risco de perda definitiva;
- senha fraca gera aviso ou rejeição conforme política definida.

## SR-003 — Criptografia Autenticada

Dados sensíveis devem ser protegidos por criptografia autenticada.

Requisitos:

- usar AEAD;
- preferir AES-256-GCM;
- permitir ChaCha20-Poly1305 apenas mediante revisão;
- proibir criptografia própria;
- proibir AES-ECB;
- proibir AES-CBC sem autenticação;
- proibir Base64 como criptografia;
- verificar integridade antes de aceitar dados descriptografados.

Critérios de aceite:

- dados persistidos não ficam legíveis fora do app;
- dados corrompidos são rejeitados;
- senha errada não retorna plaintext;
- testes validam erro seguro.

## SR-004 — KDF

A Master Password deve passar por uma KDF antes de proteger material criptográfico.

Requisitos:

- usar KDF obrigatoriamente;
- preferir Argon2id se implementação Android for confiável;
- aceitar PBKDF2 forte como alternativa;
- usar salt único por cofre;
- versionar parâmetros da KDF;
- revisar parâmetros em aparelho real antes de release.

Critérios de aceite:

- salt existe por cofre;
- parâmetros da KDF são persistidos/versionados;
- senha do usuário não é usada diretamente como chave AES;
- teste com senha errada falha corretamente.

## SR-005 — Gestão de Chaves

A Vault Key e qualquer Key Encryption Key devem ser tratadas como Nível 3.

Requisitos:

- não hardcodar chaves;
- não salvar Vault Key desprotegida;
- não logar chaves;
- não exportar chaves em texto claro;
- usar Android Keystore quando aplicável;
- preferir hardware-backed ou StrongBox quando disponível;
- descartar referências de chave quando possível ao bloquear o cofre.

Critérios de aceite:

- nenhuma chave aparece no código-fonte;
- nenhuma chave aparece em logs;
- storage não contém chave em texto claro;
- arquitetura documenta fallback quando Keystore forte não estiver disponível.

## SR-006 — Armazenamento Seguro

Dados Nível 3 devem ser protegidos em repouso.

Requisitos:

- proteger conteúdo de notas;
- proteger senhas salvas;
- proteger documentos;
- proteger fotos;
- tratar títulos, tags, URLs e nomes de arquivo como sensíveis;
- evitar índice de busca em texto claro para conteúdo sensível;
- não manter cache descriptografado persistente;
- usar storage privado do app para arquivos.

Critérios de aceite:

- inspeção manual do storage não encontra plaintext;
- título e conteúdo sensível não aparecem em banco aberto;
- arquivos privados não ficam legíveis no filesystem;
- dados não aparecem na galeria por ação do app.

## SR-007 — Backup Seguro

Backup do Vaultia deve ser sempre criptografado.

Requisitos:

- backup não pode ser JSON, CSV ou TXT aberto;
- backup deve ser criptografado;
- backup deve ter integridade verificável;
- backup deve ter formato versionado;
- senha incorreta deve rejeitar importação;
- backup corrompido deve ser rejeitado;
- exportação exige confirmação;
- importação exige confirmação antes de sobrescrever cofre existente.

Critérios de aceite:

- backup não contém plaintext;
- teste de senha errada falha;
- teste de backup corrompido falha;
- fluxo exibe aviso claro ao usuário.

## SR-008 — Proteção de Tela

Telas sensíveis devem impedir exposição visual indevida.

Requisitos:

- usar `FLAG_SECURE` em telas autenticadas e sensíveis;
- ocultar conteúdo ao ir para background;
- impedir preview sensível em tela recente;
- bloquear cofre ao background conforme política de sessão;
- não mostrar dados Nível 3 em notificações ou widgets.

Critérios de aceite:

- screenshot é bloqueado em telas sensíveis;
- tela recente não mostra conteúdo do cofre;
- app bloqueia ou oculta conteúdo ao background.

## SR-009 — Clipboard Seguro

Clipboard só pode receber dados sensíveis por ação explícita.

Requisitos:

- copiar senha apenas com ação do usuário;
- limpar clipboard após tempo definido;
- não copiar automaticamente;
- não logar conteúdo copiado;
- avisar usuário sobre limpeza temporária.

Critérios de aceite:

- senha copiada expira;
- senha não aparece em logs;
- copiar exige ação explícita.

## SR-010 — Logs Sanitizados

Logs não podem conter dados sensíveis.

Requisitos:

- usar logger central;
- proibir `Log.d` fora do logger central;
- proibir `println`;
- proibir `printStackTrace`;
- proibir senha, chave, nota, URL, título, tags, nomes de arquivo e conteúdo em logs;
- separar logs técnicos de auditoria local;
- impedir logs remotos na v1.

Critérios de aceite:

- revisão de Logcat não encontra dados sensíveis;
- release não contém debug logs indevidos;
- CI futuro deve verificar padrões proibidos;
- auditoria local contém apenas eventos sanitizados.

## SR-011 — Auditoria Local

O app deve registrar eventos locais mínimos de segurança.

Requisitos:

- registrar eventos sanitizados;
- limitar retenção;
- não registrar conteúdo do cofre;
- não registrar identificadores sensíveis;
- permitir visão simples de atividade recente.

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
- biometria desativada.

Critérios de aceite:

- auditoria não contém conteúdo sensível;
- auditoria é local;
- auditoria não é enviada para servidor.

## SR-012 — Permissões Android

O app deve solicitar apenas permissões estritamente necessárias.

Requisitos:

- proibir `INTERNET`;
- proibir localização;
- proibir notificações na v1;
- proibir câmera na v1 inicial;
- proibir áudio;
- proibir contatos;
- proibir SMS;
- proibir leitura ampla de storage quando evitável;
- permitir `USE_BIOMETRIC` quando biometria for implementada.

Critérios de aceite:

- manifesto revisado;
- permissões são justificadas no SDD;
- CI futuro falha se `INTERNET` aparecer.

## SR-013 — Backup Automático do Android

O app deve impedir backup automático do sistema para dados do cofre.

Requisitos:

- definir `android:allowBackup="false"`;
- definir `android:fullBackupContent="false"`;
- revisar `dataExtractionRules`;
- não depender de backup do sistema para recuperação.

Critérios de aceite:

- manifesto contém `allowBackup=false`;
- configuração de backup foi revisada;
- release checklist confirma proteção.

## SR-014 — Biometria

Biometria deve ser opcional e não deve substituir totalmente a Master Password.

Requisitos:

- ativar biometria somente após criação do cofre;
- permitir desativar biometria;
- manter fallback por Master Password;
- exigir Master Password em ações críticas;
- invalidar ou restringir biometria após eventos de risco quando possível;
- não usar biometria como recuperação de cofre perdido.

Critérios de aceite:

- usuário acessa com Master Password mesmo sem biometria;
- biometria não recupera cofre sem senha;
- ações críticas podem exigir Master Password.

## SR-015 — Dependências

Dependências devem ser mínimas e justificadas.

Requisitos:

- não adicionar biblioteca sem justificativa;
- revisar dependências criptográficas;
- evitar SDKs com tráfego remoto;
- evitar dependências que adicionem permissões indevidas;
- auditar dependências antes de release.

Critérios de aceite:

- lista de dependências revisada;
- dependências críticas documentadas;
- nenhuma dependência adiciona `INTERNET` sem aprovação.

## SR-016 — Build Release

Build release deve ser endurecido.

Requisitos:

- `debuggable=false`;
- R8/ProGuard habilitado quando aplicável;
- assinatura de release protegida;
- ausência de logs de debug;
- ausência de secrets;
- ausência de dados de teste reais;
- hash SHA-256 do artefato.

Critérios de aceite:

- release checklist aprovado;
- APK/AAB assinado corretamente;
- hash gerado;
- teste em aparelho real executado.

## SR-017 — Dados de Teste

Testes não podem usar dados reais do cofre.

Requisitos:

- usar dados fictícios;
- não usar senhas reais;
- não usar documentos reais;
- não usar fotos privadas reais;
- não versionar backup real;
- não subir screenshots com conteúdo real.

Critérios de aceite:

- arquivos de teste revisados;
- repositório não contém dados reais;
- secret scan não encontra credenciais reais.

## SR-018 — Tratamento de Erros

Erros devem ser seguros, genéricos e não informativos para atacante.

Requisitos:

- não expor stack trace ao usuário;
- não diferenciar erro de senha de forma útil para ataque;
- não revelar metadados sensíveis;
- não logar conteúdo sensível;
- mostrar mensagens compreensíveis, mas genéricas.

Critérios de aceite:

- erro de senha incorreta é seguro;
- erro de backup corrompido é seguro;
- erro de storage não vaza path sensível.

## SR-019 — Revisão de Segurança

Mudanças em segurança exigem revisão explícita.

Requisitos:

- mudanças em `/05-seguranca` exigem revisão;
- mudanças em criptografia exigem revisão;
- mudanças em permissões exigem revisão;
- mudanças em storage exigem revisão;
- mudanças em logs exigem revisão;
- mudanças em backup exigem revisão.

Critérios de aceite:

- PRs relevantes atualizam SDD;
- checklist de revisão foi seguido;
- decisão crítica não é feita apenas no código.

## SR-020 — Critério Geral de Aceite

A v1 não pode ser liberada se violar qualquer requisito crítico deste documento.

Bloqueadores absolutos:

- permissão `INTERNET` sem aprovação;
- senha-mestra armazenada;
- dados Nível 3 em plaintext;
- backup aberto;
- logs com segredo;
- `allowBackup=true`;
- `debuggable=true` em release;
- criptografia própria;
- chave hardcoded;
- dependência crítica não revisada;
- release sem checklist.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/02-security-requirements.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
