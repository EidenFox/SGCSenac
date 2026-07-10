# SGC Senac - Sistema de Gestão Corporativa

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swing](https://img.shields.io/badge/Desktop-Java_Swing-blue?style=for-the-badge)

Um sistema desktop robusto concebido para a gestão integral de utilizadores, produtos, inventário e processamento de pedidos. Desenvolvido como Produto Mínimo Viável (MVP) académico, este projeto aplica fortes princípios de Engenharia de Software, separação de responsabilidades (padrão DAO/Model/View) e segurança de dados.

## 📌 Visão Geral

O SGC Senac foi projetado para simular o ambiente interno de uma empresa, separando rigorosamente os privilégios entre Funcionários e Administradores (RBAC). O sistema não confia apenas na interface para garantir a integridade dos dados, implementando regras de negócio diretas na base de dados relacional através de `Views` e `Triggers` de auditoria.

## ✨ Principais Funcionalidades

### Segurança e Autenticação
* **Criptografia de Ponta:** As passwords nunca são armazenadas em texto limpo. Utilização da biblioteca `BCrypt` para hashing com *salt* dinâmico.
* **Rate Limiting (Proteção contra Brute-Force):** O sistema de redefinição de password possui um bloqueio temporal automático de 30 minutos após 5 tentativas falhadas consecutivas, com registo de timestamps nativo na base de dados.

### Gestão de Utilizadores (RBAC)
* **Fluxo de Aprovação:** Novos funcionários podem registar-se via ecrã de login, mas entram num estado "Pendente" (isolado do sistema). O acesso só é libertado após análise e aprovação num painel exclusivo para Administradores.
* **Gestão de Estados:** Contas podem ser desativadas sem comprometer a integridade referencial dos pedidos históricos.

### Inventário e Logística
* **Auditoria de Produtos:** Implementação de `Triggers` no MySQL que escutam alterações na tabela de produtos e gravam automaticamente num histórico (`ProdutoLog`) os valores antigos e novos, identificando qual o ID do administrador responsável pela alteração.
* **Controle de Pedidos:** Acompanhamento do ciclo de vida completo de uma requisição interna, com atualização dinâmica de estado.

---

## 🛠️ Arquitetura e Tecnologias

* **Linguagem:** Java 21 (OpenJDK).
* **Interface Gráfica (GUI):** Java Swing / AWT (gerado nativamente com conversão de formulários do IntelliJ para código fonte puro, garantindo compatibilidade universal de compilação).
* **Base de Dados:** MySQL 8.x (Script SQL incluso com DDL, Views complexas com `LEFT JOIN` e Triggers de log).
* **Gestão de Dependências:** Apache Maven.
* **Build Automático:** Configuração do `maven-assembly-plugin` para geração de um executável independente (*Fat JAR*) com todas as bibliotecas embutidas.

---

## 🚀 Como Executar (Utilizador Final)

Não é necessário configurar ambientes de desenvolvimento nem instalar uma base de dados local para testar a aplicação. **A versão pré-compilada das Releases já vem configurada de fábrica com acesso a um banco de dados de testes na nuvem**, contendo todos os dados de exemplo baseados no repositório.

1. Faça o download da versão mais recente na secção de [Releases](../../releases/latest).
2. Certifique-se de que possui o **Java 21** instalado no seu sistema.
3. Execute o ficheiro descarregado:
```bash
java -jar SGCSenac_V1_0.jar

```

*Nota para utilizadores Linux (Wayland): O pacote completo `java-21-openjdk` é necessário para suportar bibliotecas gráficas (Headful).*

---

## 💻 Compilação, Desenvolvimento e Banco de Dados Local

Se desejar clonar o projeto para alterar o código ou utilizar um servidor MySQL local em vez do ambiente de testes em nuvem, siga estes passos:

1. Clone o repositório:

```bash
git clone [https://github.com/](https://github.com/EidenFox/SGCSenac)

```

2. Importe o ficheiro `SGCScript.sql` para o seu servidor MySQL local (para produção remover os inserts de teste do final do arquivo).
3. Crie o ficheiro **`db.properties`** na diretoria de recursos do projeto (`src/main/resources/db.properties`) e insira as suas credenciais de acesso locais:

```properties
db.url=jdbc:mysql://localhost:3306/SGCSenac
db.user=root
db.password=sua_senha_aqui

```

4. **Atenção:** Como as configurações de banco de dados são embutidas dentro do executável final, **sempre que o `db.properties` for alterado, é obrigatório recompilar o projeto**. Utilize o Maven para gerar o novo executável isolado:

```bash
mvn clean package

```

5. O seu ficheiro final personalizado estará disponível na pasta `target/`.

---

## 👤 Autores
### **Daniel Rocha** Estudante de ADS na faculdade Senac.
- 🐙 [GitHub](https://github.com/EidenFox/)

### **Nickolas Anderson** Estudante de ADS na faculdade Senac.
- 🐙 [GitHub](https://github.com/JovemPadrawn)

### **Matheus Costa** Estudante de ADS na faculdade Senac.
- 🐙 [GitHub](https://github.com/1tsc0sta)

### **Kauan Cotes** Estudante de ADS na faculdade Senac.
- 🐙 [GitHub](https://github.com/kauan-cotes)

### **Eduardo Nunes** Estudante de ADS na faculdade Senac.
- 🐙 [GitHub](https://github.com/EdwardLeywin)

### **Ana Camila** EEstudante de ADS na faculdade Senac.
- 🐙 [GitHub](https://github.com/AnaaRosa)


Todos os direitos reservados [©EidenFox](https://github.com/EidenFox/)


