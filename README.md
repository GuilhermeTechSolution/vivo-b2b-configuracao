# 📘 Vivo B2B - Configuração

## 📖 Descrição
O **Vivo B2B Configuração** é um sistema backend desenvolvido em **Java Spring Boot** para automação, gerenciamento e configuração de equipamentos de rede no contexto B2B da Vivo.  
Ele provê APIs REST para integração com sistemas externos, permitindo gerenciar processos de configuração, autenticação e persistência de dados em banco MySQL.

---

## 🏗️ Arquitetura e Tecnologias Utilizadas
- **Java 11+**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **MySQL**
- **Maven**
- **Docker**
- **GitLab CI/CD** (pipeline de integração/entrega contínua)

### Camadas
- **Backend monolítico em Spring Boot**
- **API (Controllers/Resources):** Pontos de entrada das requisições REST.
- **Service/Commons:** lógica de negócio e integração com equipamentos.
- **Config:** Configurações de segurança, persistência e web.
- **DAO (Data Access Objects):** Acesso ao banco de dados.
- **Domain:** Entidades de negócio.
- **Enums:** Definições de valores fixos para padronização de processos.

### Dependências
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data JPA
- MySQL Connector
- Lombok
- Maven Wrapper
---

## ⚙️ Requisitos e Ambiente
- **Java 11+**
- **Maven 3.6+**
- **MySQL 5.7+ ou 8** configurado com schema `vivo_b2b_configuracao`
- **Docker** (opcional para containerização)
- **Variáveis de ambiente:**
  - `DB_HOST`, `DB_USER`, `DB_PASS`
  - `SERVER_PORT`
  - (opcional) `LOG_LEVEL`
---

## 🚀 Instalação e Execução
### Clonar repositório
```bash
git clone https://g
```

### Configurar Banco de Dados
```bash
src/main/java/br/com/iatapp/config/MysqlConfig.java
```
---

## 🔄 Fluxo de Execução
### Executar Aplicação
```bash
./mvnw spring-boot:run
```
1. O cliente envia requisição HTTP para a API (`/api/vivo2`, `/api/switch`, etc.).
2. O **Resource (Controller)** recebe a chamada e encaminha para os métodos de negócio.
3. Os **Commons** auxiliam na execução de scripts/comandos de configuração nos equipamentos.
4. O **DAO** persiste logs, configurações e resultados no banco MySQL.
5. O retorno é padronizado em JSON para o cliente.
--- 
## 📂 Estrutura de Diretórios
```bash
vivo-b2b-configuracao/
├── .gitignore
├── .gitlab-ci.yml
├── Dockerfile
├── pom.xml
├── src/main/java/br/com/iatapp/
│   ├── OttapStaterTemplateApplication.java  # Classe principal (Spring Boot)
│   ├── api/                                # Controladores REST
│   ├── commons/                            # Utilitários e integrações
│   ├── config/                             # Configurações (DB, Security, Web)
│   ├── dao/                                # Data Access Objects
│   ├── domain/                             # Entidades de negócio
│   ├── enums/                              # Enumerações fixas
│   └── ...
```
---
## 🔑 Classes Principais e Relevância
- OttapStaterTemplateApplication.java → Classe de inicialização Spring Boot.
- MainResource.java / Vivo2Resource.java / SwitchSwaConnectorResource.java → Endpoints principais da API.
- MysqlConfig.java → Configuração de datasource MySQL.
- SecurityConfig.java → Configuração de autenticação/autorização.
- VivoB2BDao.java, AtivacaoDao.java, UsuariosDao.java → Persistência no banco de dados.
- Domains (ConfigSwitch, ProcessoId, Vivo2*, etc.)** → Representam os objetos de negócio.
- Enums (ModelosEquipamentosEnum, CodigoErroAcessoEquipamentoEnum, etc.) → Padronização de códigos e mensagens.

### **OttapStaterTemplateApplication**
- Classe principal que inicializa o Spring Boot.
- Contém o método `main`.

### **Resources (Controllers)**
- `MainResource` → API principal.
- `SipOneCoreResource` → Operações sobre o SIP One Core.
- `SwitchSwaConnectorResource` → Conexões com switches.
- `Vivo2Resource` → Configurações da plataforma Vivo2.

### **Commons**
- `SwitchCoriantCommons`, `Vivo2CiscoCommons`, `Vivo2HuaweiCommons` → Implementações específicas por fabricante de equipamento.

### **Config**
- `MysqlConfig` → Configuração de DataSource para MySQL.
- `SecurityConfig` → Políticas de autenticação/autorização.
- `WebConfig` → Configurações de CORS, serialização etc.

### **DAO**
- `UsuariosDao`, `SenhasDao`, `AtivacaoDao`, `VivoB2BDao` → CRUD de tabelas principais.

### **Domain**
- Entidades como `ConfigSwitchDomain`, `ProcessoIdDomain`, `Vivo2ScriptDomain` representam dados persistentes.

### **Enums**
- `ModelosEquipamentosEnum`, `CodigoErroAcessoEquipamentoEnum`, `BandwidthEnum` padronizam valores fixos.
--- 

## 🧪 Exemplos de Uso
### Exemplo: Chamada de API para configurar switch
```bash
POST /api/vivo2/configuracao
Content-Type: application/json

{
  "equipamento": "Huawei",
  "porta": "Gi0/1",
  "bandwidth": "1Gbps"
}
```
### Resposta:
```bash
{
  "status": "SUCESSO",
  "mensagem": "Configuração aplicada ao equipamento Huawei porta Gi0/1"
}
```
