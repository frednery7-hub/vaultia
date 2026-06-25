# Vaultia – Data Classification

## Objetivo do Documento

Este documento define a classificação de dados do Vaultia Android v1.

O objetivo é determinar quais dados o aplicativo manipula, qual nível de sensibilidade cada dado possui e quais regras mínimas de proteção devem ser aplicadas.

## Princípio Central

No Vaultia, metadados também podem ser sensíveis.

Títulos, nomes de arquivos, URLs, tags e datas podem revelar informações privadas do usuário. Portanto, a v1 deve tratar o banco do cofre como sensível por padrão.

## Níveis de Classificação

O Vaultia usa quatro níveis de classificação:

1. Nível 0 — Público
2. Nível 1 — Operacional
3. Nível 2 — Confidencial
4. Nível 3 — Restrito / Crítico

## Nível 0 — Público

### Definição

Dados que podem ser expostos publicamente sem risco relevante ao usuário ou ao cofre.

### Exemplos

- nome público do app;
- versão pública do app;
- política de privacidade;
- página pública de segurança;
- documentação pública sem detalhes sensíveis;
- release notes genéricas;
- textos institucionais;
- informações da Play Store.

### Regras de Proteção

- pode ser publicado;
- pode estar em site público;
- pode estar em repositório público, se aplicável;
- não deve conter segredo, chave, dados de usuário ou detalhe operacional sensível.

## Nível 1 — Operacional

### Definição

Dados usados para operação, build, documentação interna ou funcionamento técnico sem conter conteúdo do cofre.

### Exemplos

- configuração de build;
- configuração de lint;
- configuração de CI sem secrets;
- documentação SDD sem segredos;
- checklist de release;
- lista de dependências;
- versão do schema;
- versão do app;
- logs técnicos sanitizados;
- eventos genéricos sem conteúdo sensível.

### Regras de Proteção

- pode ser versionado no repositório;
- deve ser revisado;
- não pode conter secrets;
- não pode conter dados reais do cofre;
- não pode conter credenciais;
- não pode conter material criptográfico.

## Nível 2 — Confidencial

### Definição

Dados que não são o conteúdo principal do cofre, mas podem afetar segurança, privacidade ou operação se expostos.

### Exemplos

- configurações de segurança;
- preferência de auto-lock;
- preferência de biometria;
- estado de onboarding;
- configuração local do app;
- eventos de auditoria local sanitizados;
- contagem de tentativas falhas;
- timestamps de eventos de segurança;
- indicação de ambiente de risco;
- informações de release interno;
- scripts internos de auditoria;
- configurações de ferramentas internas.

### Regras de Proteção

- armazenar localmente de forma protegida quando aplicável;
- não enviar para servidor na v1;
- não logar com identificadores sensíveis;
- não expor em tela sem necessidade;
- não incluir em backup aberto;
- revisar antes de exportar qualquer informação relacionada.

## Nível 3 — Restrito / Crítico

### Definição

Dados cuja exposição pode comprometer diretamente a privacidade, segurança ou integridade do cofre do usuário.

### Exemplos de Conteúdo do Cofre

- senha-mestra;
- senhas salvas;
- notas privadas;
- documentos;
- fotos privadas;
- arquivos importados;
- seed phrases;
- chaves privadas;
- dados bancários;
- dados pessoais sensíveis;
- observações internas de itens;
- conteúdo temporariamente descriptografado.

### Exemplos de Metadados Sensíveis

- títulos de itens;
- nomes reais de arquivos;
- tags;
- URLs salvas;
- nomes de contas;
- nomes de usuários;
- e-mails salvos;
- tamanho de arquivos, quando associado a item;
- datas de criação de itens;
- datas de modificação de itens;
- tipo de arquivo associado a item privado;
- estrutura interna do cofre.

### Exemplos Criptográficos

- Vault Key;
- Key Encryption Key;
- chaves derivadas;
- material temporário de derivação;
- dados descriptografados em memória;
- backup criptografado;
- parâmetros criptográficos quando associados ao cofre.

Observação: salt, nonce e IV não são necessariamente secretos em modelos criptográficos corretos, mas não devem ser logados nem expostos desnecessariamente na v1.

### Regras de Proteção

- nunca armazenar em texto claro;
- nunca logar;
- nunca enviar pela internet na v1;
- nunca expor em crash report;
- nunca expor em analytics;
- nunca colocar em storage público;
- nunca incluir em backup aberto;
- criptografar em repouso;
- limitar tempo em memória quando possível;
- proteger telas com `FLAG_SECURE`;
- limpar temporários;
- proteger contra preview em tela recente;
- exigir cofre desbloqueado para acesso;
- aplicar menor exposição possível na UI.

## Classificação por Tipo de Dado

| Dado | Classificação |
|---|---|
| Nome do app | Nível 0 |
| Política de privacidade | Nível 0 |
| Release notes públicas | Nível 0 |
| Documentação SDD sem segredos | Nível 1 |
| Configuração de build sem secrets | Nível 1 |
| Logs técnicos sanitizados | Nível 1 |
| Configuração de auto-lock | Nível 2 |
| Preferência de biometria | Nível 2 |
| Estado de onboarding | Nível 2 |
| Auditoria local sanitizada | Nível 2 |
| Título de nota | Nível 3 |
| Conteúdo de nota | Nível 3 |
| Senha salva | Nível 3 |
| URL salva | Nível 3 |
| Usuário/e-mail salvo | Nível 3 |
| Nome de arquivo privado | Nível 3 |
| Foto privada | Nível 3 |
| Documento privado | Nível 3 |
| Backup criptografado | Nível 3 |
| Senha-mestra | Nível 3 |
| Vault Key | Nível 3 |
| Chaves derivadas | Nível 3 |

## Regras para Banco Local

O banco local do cofre deve ser tratado como Nível 3 por padrão.

Regras:

- não armazenar conteúdo sensível em texto claro;
- não armazenar título sensível em texto claro, salvo decisão explícita posterior;
- não criar índice de busca em texto claro para dados sensíveis;
- não persistir cache descriptografado;
- versionar schema;
- proteger migrações;
- testar se dados salvos ficam ilegíveis fora do app.

## Regras para Arquivos

Arquivos privados devem ser tratados como Nível 3.

Regras:

- armazenar em área privada do app;
- criptografar individualmente;
- não manter nome real do arquivo em texto claro;
- não deixar cópia permanente na galeria por ação do app;
- limpar arquivos temporários;
- validar tipo e tamanho;
- proteger visualização com `FLAG_SECURE`;
- excluir arquivo criptografado quando o item for removido.

## Regras para Backups

Backup do cofre é Nível 3.

Regras:

- backup sempre criptografado;
- backup nunca exportado em JSON, CSV ou TXT aberto na v1;
- backup deve ter integridade verificável;
- backup deve ter formato versionado;
- importação deve rejeitar senha incorreta;
- importação deve rejeitar arquivo corrompido;
- exportação deve exigir confirmação do usuário;
- usuário deve receber aviso claro sobre guarda do arquivo.

## Regras para Logs

Logs não podem conter dados Nível 3.

Proibido em logs:

- senha-mestra;
- senhas salvas;
- conteúdo de notas;
- conteúdo de documentos;
- conteúdo de fotos;
- título de item;
- URL salva;
- e-mail salvo;
- nome real de arquivo;
- tags;
- caminho completo de arquivo sensível;
- Vault Key;
- chave derivada;
- plaintext;
- ciphertext;
- conteúdo do clipboard.

Permitido em logs sanitizados:

- evento genérico;
- categoria;
- timestamp local;
- resultado genérico;
- erro técnico sem conteúdo;
- correlation ID local não identificável.

## Regras para UI

Dados Nível 3 só podem aparecer na UI quando:

- o cofre estiver desbloqueado;
- o usuário tiver solicitado a visualização;
- a tela estiver protegida contra screenshot;
- o app não estiver em background;
- o conteúdo não estiver visível em tela recente.

Dados Nível 3 não devem aparecer em:

- preview do app;
- notificações;
- widgets;
- logs;
- crash reports;
- analytics;
- clipboard sem ação explícita.

## Regras para Clipboard

Clipboard pode receber dado Nível 3 apenas por ação explícita do usuário.

Regras:

- copiar senha apenas manualmente;
- limpar clipboard após tempo definido;
- avisar usuário;
- nunca copiar automaticamente;
- nunca registrar conteúdo copiado em logs.

## Regras para Memória

Dados Nível 3 podem existir temporariamente em memória enquanto o cofre estiver desbloqueado.

Regras:

- reduzir tempo de exposição;
- limpar referências quando possível;
- bloquear app ao background;
- limpar temporários;
- evitar persistência desnecessária;
- não manter cache em texto claro.

## Regras para Ferramentas

Dados reais do cofre não podem ser usados em:

- Docker;
- Postman;
- Terraform;
- CI/CD;
- scripts Python;
- screenshots de documentação;
- issues públicas;
- pull requests;
- logs de build;
- arquivos de teste versionados.

Testes devem usar dados fictícios.

## Critérios de Aceite

Este documento será considerado aceito quando:

- todos os tipos principais de dados estiverem classificados;
- metadados sensíveis estiverem tratados como Nível 3;
- regras de banco local estiverem definidas;
- regras de arquivos estiverem definidas;
- regras de backup estiverem definidas;
- regras de logs estiverem definidas;
- regras de UI estiverem definidas;
- regras de ferramentas estiverem definidas;
- o documento estiver alinhado com threat model e escopo v1.

## Status do Documento

- **Documento:** `docs/SDD/05-seguranca/03-data-classification.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Fundação e SDD Essencial
