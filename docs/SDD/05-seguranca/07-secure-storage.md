# Vaultia – Secure Storage

## Objetivo do Documento

Este documento define a política de armazenamento seguro do Vaultia Android v1.

O objetivo é estabelecer como notas, senhas, arquivos, metadados, configurações, auditoria local e backups devem ser armazenados no dispositivo sem expor dados sensíveis em texto claro.

## Princípio Central

Nenhum dado Nível 3 deve ser persistido em plaintext.

No Vaultia, o armazenamento local deve ser tratado como ambiente potencialmente inspecionável por atacante com acesso físico ao aparelho, backup extraído, filesystem copiado ou dispositivo comprometido parcialmente.

## Escopo

Este documento cobre:

- banco local;
- arquivos privados;
- metadados;
- notas;
- senhas;
- fotos;
- documentos;
- backups;
- configurações;
- auditoria local;
- cache;
- temporários;
- exclusão;
- migração;
- inspeção;
- critérios de aceite.

## SS-001 — Dados Nível 3

Dados Nível 3 devem ser armazenados apenas de forma protegida.

Inclui:

- conteúdo de notas;
- senhas salvas;
- URLs salvas;
- nomes de usuários;
- e-mails salvos;
- observações;
- fotos;
- documentos;
- nomes reais de arquivos;
- títulos sensíveis;
- tags;
- backup;
- Vault Key;
- material criptográfico;
- conteúdo temporariamente descriptografado.

Regras:

- não persistir plaintext;
- não criar cache persistente descriptografado;
- não criar índice plaintext;
- não salvar em storage público;
- não salvar em logs;
- não salvar em crash reports.

Critérios de aceite:

- inspeção manual do storage não encontra dados Nível 3 legíveis;
- busca por strings conhecidas não encontra conteúdo sensível em plaintext.

## SS-002 — Modelo de Armazenamento

A v1 deve usar modelo local protegido.

Modelo preferencial:

- banco local para registros e metadados protegidos;
- storage privado do app para arquivos binários criptografados;
- nomes físicos não semânticos para arquivos;
- backup local criptografado como pacote externo.

Decisões abertas:

- Room com criptografia por campo/registro;
- SQLCipher;
- abordagem híbrida.

Critérios de aceite:

- decisão final documentada antes da implementação;
- armazenamento respeita Data Classification e Crypto Architecture.

## SS-003 — Banco Local

O banco local pode armazenar registros do cofre apenas se dados sensíveis estiverem protegidos.

Regras:

- não armazenar conteúdo de nota em plaintext;
- não armazenar senha em plaintext;
- não armazenar URL sensível em plaintext;
- não armazenar usuário/e-mail salvo em plaintext;
- não armazenar título sensível em plaintext sem decisão explícita;
- não armazenar tags em plaintext sem decisão explícita;
- não armazenar nomes reais de arquivos em plaintext;
- schema deve ser versionado;
- migrations devem ser planejadas;
- erro de migration deve falhar de forma segura.

Critérios de aceite:

- abrir banco fora do app não revela conteúdo útil;
- strings de teste não aparecem em plaintext;
- schema não cria colunas plaintext para Nível 3 sem justificativa.

## SS-004 — Metadados

Metadados podem revelar conteúdo privado e devem ser tratados com cuidado.

Metadados sensíveis:

- título;
- URL;
- usuário;
- e-mail;
- tags;
- nome real de arquivo;
- MIME type, quando revelador;
- tamanho de arquivo, quando associado a item;
- data de criação/modificação, quando sensível;
- categoria manual sensível.

Regras:

- metadados sensíveis devem ser protegidos;
- metadados técnicos mínimos podem existir se não revelarem conteúdo;
- decisões de plaintext em metadados exigem justificativa explícita;
- busca local não deve depender de índice plaintext de metadados sensíveis na v1.

Critérios de aceite:

- revisão do schema identifica quais metadados são protegidos;
- nenhum metadado sensível fica aberto por conveniência sem aprovação.

## SS-005 — Notas

Notas são Nível 3.

Regras:

- conteúdo deve ser criptografado;
- título deve ser tratado como sensível;
- tags devem ser tratadas como sensíveis;
- nota não deve ser salva em cache aberto;
- nota não deve aparecer em logs;
- nota não deve aparecer em backup aberto;
- nota não deve aparecer em app switcher.

Critérios de aceite:

- texto conhecido de nota não aparece em banco, arquivo ou log;
- nota só aparece com cofre desbloqueado.

## SS-006 — Senhas

Itens de senha são Nível 3.

Campos sensíveis:

- título;
- usuário;
- e-mail;
- senha;
- URL;
- observações.

Regras:

- senha deve ser criptografada;
- campos associados devem ser protegidos;
- senha não deve ser armazenada em clipboard persistente;
- senha não deve aparecer em logs;
- senha não deve aparecer em auditoria;
- senha não deve aparecer em plaintext no banco.

Critérios de aceite:

- senha de teste não aparece no storage;
- senha copiada não aparece em logs;
- auditoria registra apenas evento genérico.

## SS-007 — Arquivos Privados

Fotos e documentos são Nível 3.

Regras:

- criptografar antes de persistir;
- armazenar em storage privado do app;
- usar nome físico não semântico;
- evitar extensão real quando ela revelar conteúdo;
- não salvar cópia permanente na galeria por ação do app;
- não salvar em Downloads sem ação explícita;
- limpar temporários;
- remover arquivo associado ao excluir item;
- validar tamanho e tipo de arquivo.

Critérios de aceite:

- arquivo importado não abre fora do app em plaintext;
- nome físico não revela conteúdo;
- exclusão remove arquivo associado.

## SS-008 — Storage Privado do App

O app deve preferir storage privado.

Regras:

- usar diretórios privados da aplicação;
- não usar storage público para cofre;
- não usar pasta Downloads para dados internos;
- não usar galeria como storage do cofre;
- não depender de permissões amplas de storage;
- usar seletor do sistema para importação/exportação quando necessário.

Critérios de aceite:

- arquivos internos ficam no espaço privado do app;
- permissões amplas de storage não são adicionadas sem revisão.

## SS-009 — Backup Local Criptografado

Backup exportado é Nível 3.

Regras:

- backup deve ser criptografado;
- backup deve ter integridade verificável;
- backup deve ter versão;
- backup não pode ser JSON aberto;
- backup não pode ser CSV aberto;
- backup não pode ser TXT aberto;
- backup não deve conter plaintext;
- backup não deve conter Master Password;
- backup não deve conter Vault Key aberta;
- usuário escolhe destino;
- exportação exige confirmação.

Critérios de aceite:

- inspeção do backup não revela dados;
- backup com senha errada falha;
- backup corrompido falha.

## SS-010 — Importação de Backup

Importação deve ser segura e não destrutiva por padrão.

Regras:

- validar formato;
- validar versão;
- validar integridade;
- rejeitar senha errada;
- rejeitar arquivo corrompido;
- não sobrescrever cofre existente sem confirmação;
- falha de importação não deve corromper cofre atual;
- path sensível não deve ser logado.

Critérios de aceite:

- backup inválido falha sem perda de dados;
- backup válido restaura localmente;
- erro de importação é genérico.

## SS-011 — Configurações Locais

Configurações podem ser Nível 2 ou Nível 3, dependendo do conteúdo.

Configurações possíveis:

- onboarding concluído;
- auto-lock;
- biometria ativada;
- versão local;
- tema;
- preferências de segurança.

Regras:

- não armazenar Master Password;
- não armazenar Vault Key;
- não armazenar conteúdo do cofre;
- não armazenar metadados sensíveis em configurações;
- preferir DataStore apenas para dados não críticos;
- proteger configuração sensível quando necessário.

Critérios de aceite:

- configurações não contêm dados Nível 3;
- preferências não revelam conteúdo do cofre.

## SS-012 — Auditoria Local

Auditoria local deve ser sanitizada.

Regras:

- registrar apenas eventos genéricos;
- não registrar título de item;
- não registrar URL;
- não registrar usuário/e-mail salvo;
- não registrar nome real de arquivo;
- não registrar conteúdo;
- não registrar senha;
- não registrar path sensível;
- aplicar retenção limitada.

Critérios de aceite:

- auditoria não contém dados Nível 3;
- eventos são úteis sem vazar conteúdo.

## SS-013 — Cache

Cache é área de risco.

Regras:

- não persistir plaintext;
- não criar cache aberto de notas;
- não criar cache aberto de arquivos;
- não criar cache aberto de senhas;
- não salvar thumbnails sensíveis em plaintext;
- não manter previews persistentes;
- limpar cache temporário quando possível.

Critérios de aceite:

- busca em cache não encontra conteúdo sensível;
- thumbnails ou previews não ficam abertos.

## SS-014 — Arquivos Temporários

Temporários devem ser evitados ou controlados.

Regras:

- evitar criar plaintext temporário;
- quando inevitável, limitar tempo de vida;
- usar storage privado;
- limpar após uso;
- limpar em erro;
- não logar caminho sensível;
- não deixar temporário em storage público.

Critérios de aceite:

- fluxo de visualização não deixa cópia aberta persistente;
- erro durante importação/exportação limpa temporários.

## SS-015 — Clipboard

Clipboard não é armazenamento seguro.

Regras:

- copiar senha apenas por ação explícita;
- limpar clipboard após tempo definido, quando possível;
- não copiar automaticamente;
- não salvar histórico de clipboard no app;
- não logar conteúdo copiado.

Critérios de aceite:

- senha copiada expira;
- logs não contêm conteúdo copiado.

## SS-016 — Exclusão Local

Exclusão deve remover registros e arquivos associados.

Regras:

- excluir nota remove registro;
- excluir senha remove registro;
- excluir arquivo remove registro e arquivo criptografado;
- exclusão deve registrar auditoria sanitizada;
- app não deve prometer destruição forense absoluta;
- app deve evitar órfãos óbvios.

Critérios de aceite:

- item excluído desaparece;
- arquivo associado é removido;
- auditoria não vaza conteúdo.

## SS-017 — Backup Automático Android

Backup automático do Android deve ser bloqueado.

Regras:

- `android:allowBackup="false"`;
- `android:fullBackupContent="false"` ou equivalente;
- `dataExtractionRules` revisado;
- não depender de backup automático para recuperação;
- recuperação ocorre por backup local criptografado.

Critérios de aceite:

- manifesto contém proteção contra backup automático;
- release checklist valida configuração.

## SS-018 — Migrações

Migrações de storage devem ser seguras.

Regras:

- migration deve ser versionada;
- migration não deve gerar plaintext persistente;
- migration deve preservar dados;
- falha deve levar a estado seguro;
- backup antes de migração pode ser exigido em versões futuras;
- mudança criptográfica exige versão nova.

Critérios de aceite:

- migration é testada;
- falha não destrói cofre silenciosamente;
- versão incompatível é rejeitada com erro seguro.

## SS-019 — Inspeção e Auditoria

O release deve permitir auditoria de storage.

Auditorias esperadas:

- buscar plaintext no banco;
- buscar plaintext em arquivos;
- buscar senha de teste no storage;
- buscar nota de teste no storage;
- buscar URL de teste no storage;
- buscar nome real de arquivo no storage;
- buscar dados em logs;
- validar ausência de backup aberto.

Critérios de aceite:

- auditoria não encontra conteúdo sensível legível.

## SS-020 — Proibições Absolutas

São proibidos:

- plaintext persistente de Nível 3;
- banco aberto com dados sensíveis;
- backup aberto;
- arquivo privado em storage público;
- nome real de arquivo exposto sem necessidade;
- cache persistente descriptografado;
- thumbnail sensível aberta;
- `allowBackup=true`;
- logs com conteúdo;
- storage remoto na v1;
- dependência de internet para storage;
- sincronização cloud.

## Decisões Abertas

Ficam abertas até prova técnica:

- Room com criptografia por campo/registro versus SQLCipher;
- formato final dos registros criptografados;
- formato final dos arquivos criptografados;
- política de thumbnails;
- política de limpeza de temporários;
- ferramenta de auditoria de plaintext;
- estratégia final de busca local protegida;
- estrutura final de diretórios privados.

## Critérios de Aceite do Documento

Este documento será considerado aceito quando:

- banco local estiver regulado;
- arquivos privados estiverem regulados;
- metadados estiverem regulados;
- backup estiver regulado;
- configurações estiverem reguladas;
- auditoria estiver regulada;
- cache e temporários estiverem regulados;
- exclusão local estiver definida;
- backup automático Android estiver bloqueado;
- auditoria de plaintext estiver definida;
- proibições absolutas estiverem claras;
- documento estiver alinhado com data classification, key management e security requirements.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/07-secure-storage.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Segurança Pré-Implementação
