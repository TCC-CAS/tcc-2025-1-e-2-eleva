# Parada Certa

Sistema integrado de gestão de estacionamentos privados — reserva, sessão, pagamento, avaliações e painel administrativo. O projeto é composto por uma API REST central, um painel web para administradores de estacionamento, um aplicativo Android para motoristas e uma API auxiliar de geração de QR Code.

O motorista localiza vagas no mapa, faz reserva ou sessão por QR Code, paga via PIX e avalia o estabelecimento. O administrador gerencia preços, vagas, horários, fotos, relatórios e plano de assinatura do estacionamento (Basic / Standard / Premium).

---

## Sumário

- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Como rodar](#como-rodar)
- [Exemplos de uso](#exemplos-de-uso)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Contribuindo](#contribuindo)
- [Licença](#licença)

---

## Arquitetura

```
┌──────────────────────┐     ┌──────────────────────┐
│  App Android         │     │  Painel Web Admin    │
│  (Kotlin / Compose)  │     │  (HTML + Tailwind)   │
└──────────┬───────────┘     └──────────┬───────────┘
           │ HTTPS                       │ HTTPS
           ▼                             ▼
        ┌────────────────────────────────────┐
        │   API Parada Certa (Spring Boot)   │
        │     /api/...   porta 8080          │
        └──────────────────┬─────────────────┘
                           │ JDBC
                           ▼
                   ┌──────────────────┐
                   │   SQL Server     │
                   └──────────────────┘
```

Integrações externas opcionais: **Gmail SMTP** (recuperação de senha), **OpenAI Moderation** (comentários), **Google Cloud Vision SafeSearch** (fotos) e **Google Maps / Places** (localização).

---

## Tecnologias

### Backend — `APIs-ParadaCerta/paradacerta-api`
![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apachemaven&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-CC2927?logo=microsoftsqlserver&logoColor=white)
![JPA](https://img.shields.io/badge/Hibernate-JPA-59666C?logo=hibernate&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-1.18-BC4521)
![JBcrypt](https://img.shields.io/badge/JBcrypt-hash%20senhas-555)

### Aplicativo Mobile — `AplicacaoMobile-ParadaCerta/ParadaCerta`
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-SDK%2024--34-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02-4285F4?logo=jetpackcompose&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2.9-48B983)
![CameraX](https://img.shields.io/badge/CameraX-1.3-3DDC84)
![ML Kit](https://img.shields.io/badge/ML%20Kit-Barcode-FF6F00?logo=google&logoColor=white)
![Google Maps](https://img.shields.io/badge/Google%20Maps-Compose-4285F4?logo=googlemaps&logoColor=white)
![Coil](https://img.shields.io/badge/Coil-2.7-FF7043)

### Painel Web — `AplicacoesWEB/AplicacaoWEB-ParadaCerta/Parada-Certa-Front`
![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-CDN-06B6D4?logo=tailwindcss&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-vanilla-F7DF1E?logo=javascript&logoColor=black)
![Node.js](https://img.shields.io/badge/Node.js-testes-339933?logo=nodedotjs&logoColor=white)

### Banco de dados — `SQL - Parada Certa`
Scripts T-SQL de criação de tabelas, *alters* e *seed* de demonstração para SQL Server.

---

## Pré-requisitos

| Componente              | Versão mínima                                       |
|-------------------------|-----------------------------------------------------|
| JDK                     | 17 (OpenJDK / Temurin)                              |
| Maven                   | 3.8+                                                |
| SQL Server              | 2019 ou superior (Express também serve)             |
| Android Studio          | Hedgehog (2023.1.1) ou superior — só para o app     |
| Navegador               | Chrome / Edge / Firefox recente — só para o painel  |
| Servidor estático local | Live Server (extensão VS Code) na porta **5500**    |

> ⚠️ O CORS da API só libera **`http://localhost:5500`** e **`http://127.0.0.1:5500`** por padrão. Se servir o front em outra porta, ajuste em [WebConfig.java](APIs-ParadaCerta/paradacerta-api/src/main/java/com/paradacerta/api/controller/WebConfig.java).

---

## Instalação

### 1) Clonar o repositório

```bash
git clone https://github.com/<sua-conta>/SistemaParadaCerta.git
cd SistemaParadaCerta
```

### 2) Criar e popular o banco

No SQL Server Management Studio (ou Azure Data Studio):

1. Crie um banco vazio chamado `ParadaCerta`.
2. Rode os scripts da pasta [SQL - Parada Certa/](SQL%20-%20Parada%20Certa/) **nesta ordem**:
   - `CREATE TABLES - ParadaCerta.sql` — schema completo + seeds básicos.
   - `ALTER - ConfirmacaoReserva.sql` — migrações posteriores.

### 3) Configurar variáveis de ambiente (`.env`)

Crie um arquivo `.env` na raiz do repositório (já está no `.gitignore`):

```env
# --- Banco SQL Server ---
SPRING_DATASOURCE_URL=jdbc:sqlserver://localhost:1433;databaseName=ParadaCerta;encrypt=false;trustServerCertificate=true
SPRING_DATASOURCE_USERNAME=SEU_USUARIO
SPRING_DATASOURCE_PASSWORD=SUA_SENHA

# --- API ---
PORT=8080
QR_API_PORT=8081

# --- E-mail (recuperação de senha) ---
# Senha de app do Google: Conta Google > Segurança > Verificação em 2 etapas > Senhas de app
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx

# --- Pagamentos ---
EMPRESA_PIX_KEY=paradacerta@gmail.com
PARADACERTA_TAXA_PLATAFORMA=0.05

# --- Upload de fotos ---
PARADACERTA_UPLOADS_DIR=./uploads

# --- Integrações externas (opcionais) ---
GCP_VISION_API_KEY=
GOOGLE_MAPS_API_KEY=
OPENAI_MODERATION_KEY=

# --- Build do app Android ---
ANDROID_API_BASE_URL=http://10.0.2.2:8080/
```

> Sem `OPENAI_MODERATION_KEY`, os comentários são aceitos sem verificação por IA.
> Sem `GCP_VISION_API_KEY`, as fotos passam pela validação local (extensão, MIME, magic bytes, tamanho), mas a moderação por IA fica desligada — apropriado para ambiente acadêmico.

### 4) Configurar o front-end web

Copie o arquivo de exemplo de configuração:

```bash
cp AplicacoesWEB/AplicacaoWEB-ParadaCerta/Parada-Certa-Front/pc-config.example.js \
   AplicacoesWEB/AplicacaoWEB-ParadaCerta/Parada-Certa-Front/pc-config.js
```

Edite `pc-config.js` apontando para a API (deixe vazio para usar a default `http://localhost:8080`):

```js
window.PC_API_BASE = ""; // ou "https://sua-api.up.railway.app"
```

### 5) Configurar o app Android

Abra a pasta [AplicacaoMobile-ParadaCerta/ParadaCerta](AplicacaoMobile-ParadaCerta/ParadaCerta) no Android Studio.

O `build.gradle.kts` lê automaticamente o `.env` da raiz e injeta `ANDROID_API_BASE_URL` e `GOOGLE_MAPS_API_KEY` no `BuildConfig`. Para emulador, use `http://10.0.2.2:8080/`; para device físico na mesma rede, troque pelo IP do host.

---

## Como rodar

### API principal (porta 8080)

```bash
cd APIs-ParadaCerta/paradacerta-api
mvn spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/api/health
# { "sucesso": true, "mensagem": "API Parada Certa está no ar!" }
```

### Painel Web (porta 5500)

No VS Code, instale a extensão **Live Server**, abra a pasta [AplicacoesWEB/AplicacaoWEB-ParadaCerta/Parada-Certa-Front](AplicacoesWEB/AplicacaoWEB-ParadaCerta/Parada-Certa-Front/) e clique em **Go Live**.

- Acesso motorista/admin: <http://localhost:5500/escolha-login.html>
- Cadastro: <http://localhost:5500/cadastro.html>

### App Android

No Android Studio, escolha um emulador (API 24+) ou device físico e clique em **Run ▶**.

### Testes do front

```bash
cd AplicacoesWEB/AplicacaoWEB-ParadaCerta/Parada-Certa-Front
npm test
```

### Testes do backend

```bash
cd APIs-ParadaCerta/paradacerta-api
mvn test
```

---

## Exemplos de uso

### Fluxo do motorista (app Android)

1. Cadastra-se com e-mail/senha.
2. Visualiza estacionamentos próximos no mapa.
3. Filtra por preço, distância e avaliação.
4. Faz **reserva** (com 15% de taxa de cancelamento) ou inicia uma **sessão por QR Code**.
5. Paga via **PIX** com a chave devolvida pela API.
6. Ao finalizar, avalia o estacionamento.

### Fluxo do administrador (painel web)

1. Login em `/login-admin.html`.
2. Dashboard com receita, ocupação e ranking.
3. Gestão de **vagas**, **preços**, **horários** e **fotos** (até 3 no plano Standard, 5 no Premium).
4. **Relatórios regionais** e **mapa de calor** — exclusivos do plano **Premium**.
5. Contratação de plano em `/plano-premium.html`:
   - **Basic**: trial de 30 dias — R$ 0,00.
   - **Standard**: R$ 149,90/mês ou R$ 1.798,80/ano.
   - **Premium**: R$ 199,90/mês ou R$ 2.398,80/ano.

### Exemplo de chamada à API

Listar estacionamentos:

```bash
curl http://localhost:8080/api/estacionamentos
```

Criar reserva:

```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "estacionamentoId": 2,
    "vagaId": 5,
    "horaInicio": "2026-06-01T14:00:00",
    "horaFim":    "2026-06-01T18:00:00"
  }'
```

---

## Estrutura do repositório

```
SistemaParadaCerta/
├── APIs-ParadaCerta/
│   └── paradacerta-api/                 # API REST principal (Spring Boot)
├── AplicacaoMobile-ParadaCerta/
│   └── ParadaCerta/                     # App Android (Kotlin + Compose)
├── AplicacaoQRCode-ParadaCerta/
│   └── paradacerta-generateqrcode-api/  # API auxiliar de QR Code
├── AplicacoesWEB/
│   └── AplicacaoWEB-ParadaCerta/
│       └── Parada-Certa-Front/          # Painel web admin (HTML/CSS/JS)
├── SQL - Parada Certa/                  # Scripts T-SQL
├── .env                                 # Variáveis locais (NÃO versionar)
├── LICENSE
└── README.md
```

---

## Contribuindo

Contribuições são bem-vindas. O fluxo padrão é:

1. **Fork** este repositório.
2. Crie uma branch a partir de `main`:
   ```bash
   git checkout -b feature/nome-curto-da-feature
   ```
3. Faça commits pequenos e descritivos, em português, no formato:
   ```
   feat(area): descricao curta no imperativo

   Detalhes opcionais no corpo.
   ```
   Prefixos sugeridos: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`.
4. Rode os testes (`mvn test` para o backend, `npm test` para o front).
5. **Não comite segredos** — arquivos `.env`, `pc-config.js`, `local.properties` e a pasta `uploads/` já estão no `.gitignore`. Confira antes de subir.
6. Abra um **Pull Request** para `main` descrevendo:
   - O que mudou e por quê;
   - Como testar;
   - Prints/GIFs se a mudança for visual.

### Reportar bugs

Abra uma **issue** com:
- Passo a passo para reproduzir;
- Comportamento esperado vs. observado;
- Versão do JDK, do navegador ou do Android;
- Logs/stacktrace, se aplicável.

---

## Licença

Distribuído sob a **Licença MIT**. Veja o arquivo [LICENSE](LICENSE) para os termos completos.

```
MIT License — Copyright (c) 2025 TCC-CAS
```

Você pode usar, copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias do software, desde que mantenha o aviso de copyright e a permissão em todas as cópias ou partes substanciais do software.
