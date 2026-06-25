# Android Foundation — Phase 14 Plan

## Nome

Debug APK Refresh for Physical Test

## Objetivo

Gerar um novo APK debug para teste físico no Samsung Galaxy S25 Ultra após as fases de sessão, home shell, modelo de metadados, repository em memória e seed demo.

## Escopo

Esta fase não altera código funcional.

Ela executa:

- testes unitários;
- build Android completo;
- auditoria de permissão INTERNET;
- cópia do APK para `releases/android`;
- cálculo de SHA-256;
- documentação da evidência.

## Fora de escopo

Esta fase não implementa:

- nova UI;
- persistência;
- storage criptografado;
- senha real;
- biometria;
- criptografia;
- banco de dados;
- rede;
- permissão INTERNET.

## Critérios de aceite

- Testes unitários passam.
- Build Android passa.
- APK é gerado.
- APK permanece sem android.permission.INTERNET.
- APK é copiado para releases/android.
- SHA-256 é registrado.
- APK pode ser enviado ao celular para teste manual.
