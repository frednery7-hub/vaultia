# Android Foundation — Phase 8B Done

## Status

Aprovado.

## Objetivo

Validar a fundação Android do Vaultia em ambiente real de execução, com APK instalado em dispositivo físico.

## Resultado

O APK debug local-only foi gerado, auditado e instalado manualmente em um Samsung Galaxy S25 Ultra via Google Drive.

A tela inicial do app abriu corretamente no dispositivo físico.

## Evidências técnicas

- Build Android: OK
- APK debug: OK
- APK sem INTERNET: OK
- Instalação em dispositivo físico: OK
- Execução visual no S25 Ultra: OK

## APK validado

Arquivo:

releases/android/Vaultia-0.1.0-debug-local-only.apk

SHA-256:

a57d2b77e1fffa954c5bc24873ebf361b45f394ceb22a65a13ccd21d0bcc0e1d

Pacote Android:

com.vaultia.app

Versão:

versionName 0.1.0
versionCode 1

SDK:

minSdk 26
targetSdk 35

## Auditoria de permissões

O APK foi auditado e não contém android.permission.INTERNET.

## Tela validada

A tela bootstrap exibiu:

Vaultia
Cofre local para dados sensíveis
Este app é local-first. Sem nuvem, sem conta e sem recuperação remota.

## Observação sobre emulador

Foi criado um AVD no pen drive:

Vaultia_Pixel5_API36_USB

Local:

<local-android-avd>

O emulador não foi usado como evidência final porque o Mac estava com pouco espaço interno livre, tornando o Android Emulator instável mesmo com o AVD salvo no pen drive.

A decisão técnica foi priorizar teste físico real.

## Critério de aceite

A Phase 8B é considerada concluída porque o APK foi compilado, auditado sem internet, instalado em dispositivo físico e executado com sucesso.

## Próximo passo

Prosseguir para a próxima fase de fundação local do app, mantendo o escopo local-first e sem backend.
