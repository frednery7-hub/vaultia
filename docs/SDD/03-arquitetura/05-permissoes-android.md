# Vaultia – Permissões Android

## Objetivo do Documento

Este documento define a política de permissões Android do Vaultia Android v1.

O objetivo é garantir que o aplicativo solicite apenas permissões estritamente necessárias e que nenhuma permissão viole o modelo offline-only, local-first e security-first do produto.

## Princípio Central

Permissão Android é superfície de ataque.

Na v1, qualquer permissão deve ser considerada suspeita até ser justificada.

## Decisão Fundamental

O Vaultia Android v1 não deve declarar a permissão `INTERNET`.

Essa decisão é obrigatória para preservar o modelo offline-only.

## Permissões Permitidas na v1

A v1 deve começar com o menor conjunto possível de permissões.

Permissões permitidas condicionalmente:

- `USE_BIOMETRIC`, somente se biometria for implementada;
- permissões estritamente exigidas pelo Android para fluxos locais aprovados;
- permissões necessárias para seletor de arquivos via mecanismo seguro do sistema, quando aplicável.

Qualquer permissão adicional exige revisão do SDD.

## Permissões Proibidas na v1

São proibidas na v1:

- `android.permission.INTERNET`;
- `android.permission.ACCESS_NETWORK_STATE`;
- `android.permission.ACCESS_FINE_LOCATION`;
- `android.permission.ACCESS_COARSE_LOCATION`;
- `android.permission.CAMERA`;
- `android.permission.RECORD_AUDIO`;
- `android.permission.READ_CONTACTS`;
- `android.permission.WRITE_CONTACTS`;
- `android.permission.READ_SMS`;
- `android.permission.SEND_SMS`;
- `android.permission.RECEIVE_SMS`;
- `android.permission.POST_NOTIFICATIONS`;
- `android.permission.READ_CALENDAR`;
- `android.permission.WRITE_CALENDAR`;
- `android.permission.BLUETOOTH`;
- `android.permission.BLUETOOTH_CONNECT`;
- `android.permission.NFC`;
- permissões de tracking, ads ou analytics;
- permissões amplas de storage quando houver alternativa segura.

## INTERNET

Status:

- proibida na v1.

Justificativa:

- o produto é offline-only;
- não há backend;
- não há API remota;
- não há conta;
- não há sync;
- não há telemetry;
- não há crash reporting remoto;
- não há analytics remoto.

Critério de aceite:

- `AndroidManifest.xml` não contém `android.permission.INTERNET`;
- dependências não adicionam `INTERNET`;
- CI futuro bloqueia qualquer ocorrência de `INTERNET`;
- release checklist valida ausência da permissão.

## ACCESS_NETWORK_STATE

Status:

- proibida na v1.

Justificativa:

- o app não precisa saber se há rede;
- a presença dessa permissão contradiz o desenho offline-only;
- pode indicar dependência indevida de conectividade.

Critério de aceite:

- manifesto não contém `ACCESS_NETWORK_STATE`.

## Localização

Status:

- proibida na v1.

Permissões proibidas:

- `ACCESS_FINE_LOCATION`;
- `ACCESS_COARSE_LOCATION`;
- `ACCESS_BACKGROUND_LOCATION`.

Justificativa:

- o app é um cofre digital;
- localização não é necessária para notas, senhas, fotos, documentos ou backup local;
- localização ampliaria desnecessariamente a superfície de privacidade.

Critério de aceite:

- manifesto não contém permissões de localização.

## Câmera

Status:

- proibida na v1 inicial.

Justificativa:

- fotos e documentos podem ser importados por seletor do sistema;
- captura direta por câmera aumenta superfície de privacidade;
- câmera pode ser avaliada em versão futura com novo threat model.

Critério de aceite:

- manifesto não contém `CAMERA`.

## Microfone

Status:

- proibido na v1.

Justificativa:

- o app não possui notas de áudio;
- microfone não é necessário para cofre local;
- áudio aumenta superfície de privacidade.

Critério de aceite:

- manifesto não contém `RECORD_AUDIO`.

## Contatos

Status:

- proibido na v1.

Justificativa:

- o app não precisa ler contatos;
- não há compartilhamento social;
- não há convite de usuários;
- não há conta remota.

Critério de aceite:

- manifesto não contém `READ_CONTACTS` ou `WRITE_CONTACTS`.

## SMS

Status:

- proibido na v1.

Justificativa:

- não há 2FA remoto;
- não há leitura automática de códigos;
- não há login;
- SMS ampliaria superfície de privacidade.

Critério de aceite:

- manifesto não contém permissões de SMS.

## Notificações

Status:

- proibidas na v1 inicial.

Justificativa:

- dados do cofre não devem aparecer em notificações;
- não há backend;
- não há alertas remotos;
- notificações ampliam risco de exposição visual.

Critério de aceite:

- manifesto não contém `POST_NOTIFICATIONS`;
- app não exibe conteúdo Nível 3 em notificações.

## Biometria

Status:

- permitida condicionalmente.

Permissão possível:

- `USE_BIOMETRIC`.

Condições:

- biometria precisa estar documentada;
- biometria deve ser opcional;
- biometria não substitui totalmente a Master Password;
- Master Password deve continuar como fallback;
- ações críticas podem exigir Master Password;
- alteração biométrica no dispositivo deve ser tratada com segurança quando possível.

Critério de aceite:

- `USE_BIOMETRIC` só aparece se a funcionalidade estiver implementada;
- fluxo sem biometria continua funcionando.

## Storage

Status:

- uso restrito.

Política:

- preferir storage privado do app;
- preferir seletor de arquivos do sistema para importação/exportação;
- evitar permissões amplas de leitura/escrita;
- não usar storage público para conteúdo do cofre;
- não salvar arquivos descriptografados permanentemente fora do app.

Permissões amplas devem ser evitadas:

- `READ_EXTERNAL_STORAGE`;
- `WRITE_EXTERNAL_STORAGE`;
- `MANAGE_EXTERNAL_STORAGE`;
- permissões modernas de mídia quando não forem estritamente necessárias.

Critério de aceite:

- importação e exportação usam mecanismo seguro;
- arquivos do cofre ficam criptografados;
- nenhuma permissão ampla de storage é adicionada sem revisão.

## Backup Automático Android

Configuração obrigatória:

- `android:allowBackup="false"`;
- `android:fullBackupContent="false"` ou configuração equivalente;
- `dataExtractionRules` revisado, quando aplicável.

Justificativa:

- backup automático pode copiar dados locais para fora do controle do app;
- recuperação deve ocorrer apenas por backup local criptografado controlado pelo Vaultia.

Critério de aceite:

- manifesto bloqueia backup automático;
- release checklist valida a configuração.

## Exported Components

Activities, services, receivers e providers devem ser revisados.

Regras:

- componentes devem ter `exported=false`, salvo necessidade explícita;
- nenhum componente sensível deve ser exportado;
- intents externas devem ser minimizadas;
- providers não devem expor dados do cofre.

Critério de aceite:

- manifesto não expõe componentes sensíveis.

## Deep Links

Status:

- fora da v1.

Justificativa:

- deep links ampliam superfície de entrada;
- o app não possui fluxos remotos;
- abertura externa de conteúdo pode criar risco de bypass.

Critério de aceite:

- nenhum deep link público na v1.

## File Sharing

Status:

- permitido apenas quando necessário e controlado.

Regras:

- compartilhamento automático proibido;
- exportação exige ação explícita;
- backup exportado deve estar criptografado;
- arquivos descriptografados não devem ser compartilhados sem confirmação explícita;
- FileProvider, se usado, deve ser restritivo.

Critério de aceite:

- nenhum provider expõe conteúdo sensível indevidamente.

## Política de Revisão de Permissões

Toda nova permissão exige:

- justificativa de produto;
- justificativa técnica;
- análise de ameaça;
- atualização deste documento;
- atualização do release checklist;
- revisão no manifesto;
- teste em build release.

Se a permissão não for essencial, ela não entra.

## CI Futuro

O CI deve bloquear automaticamente:

- `android.permission.INTERNET`;
- `android:allowBackup="true"`;
- `debuggable=true` em release;
- permissões proibidas;
- componentes exportados indevidos.

## Comandos de Auditoria Esperados

Auditorias futuras devem procurar:

- `INTERNET`;
- `ACCESS_NETWORK_STATE`;
- `ACCESS_FINE_LOCATION`;
- `ACCESS_COARSE_LOCATION`;
- `CAMERA`;
- `RECORD_AUDIO`;
- `READ_CONTACTS`;
- `READ_SMS`;
- `POST_NOTIFICATIONS`;
- `allowBackup`;
- `debuggable`;
- `exported`.

## Critérios de Aceite

Este documento será considerado aceito quando:

- permissões permitidas estiverem definidas;
- permissões proibidas estiverem definidas;
- ausência de `INTERNET` estiver explícita;
- política de biometria estiver definida;
- política de storage estiver definida;
- backup automático Android estiver tratado;
- componentes exportados estiverem tratados;
- regra de revisão de novas permissões estiver definida;
- documento estiver alinhado com security requirements e release checklist.

## Status do Documento

- **Documento:** `docs/SDD/03-arquitetura/05-permissoes-android.md`
- **Versão:** `0.1`
- **Status:** Aprovado como baseline inicial
- **Escopo:** Vaultia Android v1
- **Fase atual:** Núcleo 0 — Arquitetura Detalhada Pré-Implementação
