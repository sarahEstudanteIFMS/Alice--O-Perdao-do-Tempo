# Como abrir e rodar no NetBeans

Este projeto é Maven puro (sem `module-info.java`), o que evita os erros mais
comuns de "module not found" / "package is not visible" no NetBeans com JavaFX.

## 1. Pré-requisitos

- NetBeans com suporte a Maven (já vem por padrão nas versões recentes)
- JDK 17 ou superior configurado no NetBeans
  (Tools → Java Platforms → confira se existe um JDK 17+)
- PostgreSQL instalado e rodando

## 2. Abrir o projeto

1. Abra o NetBeans
2. **File → Open Project**
3. Selecione a pasta `alice-wonderland` (a que contém o `pom.xml`)
4. Aguarde o NetBeans baixar as dependências (JavaFX, PostgreSQL driver) — pode
   demorar na primeira vez. Acompanhe pela aba "Output".

Se aparecer aviso de "missing dependencies", clique com botão direito no
projeto → **Resolve Project Problems...** e siga as sugestões (geralmente é só
baixar as dependências).

## 3. Configurar o banco

Crie o banco:
```sql
CREATE DATABASE alice_wonderland;
```

Edite `src/main/java/com/alice/util/ConnectionFactory.java` com seu usuário e senha:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/alice_wonderland";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

## 4. Rodar o projeto

### Opção A — Botão Run (recomendado)
Clique com o botão direito no projeto → **Run** (ou F6).

Esse `pom.xml` já está configurado com o `exec-maven-plugin`, que faz o NetBeans
conseguir rodar `com.alice.MainApp` direto pelo botão Run, sem dar erro de módulo.

### Opção B — Pelo terminal
Na pasta do projeto (onde está o `pom.xml`):
```bash
mvn clean compile exec:java
```

ou, usando o plugin oficial do JavaFX:
```bash
mvn clean javafx:run
```

## 5. Erros comuns e soluções

**"Error: JavaFX runtime components are missing" / "Command execution failed"
ao usar exec:java ou o botão Run do NetBeans**
→ Já corrigido neste projeto: foi adicionada a classe `com.alice.Launcher`,
que é usada como Main-Class pelo `exec-maven-plugin`. Ela não estende
`Application`, então a JVM não bloqueia a execução por falta de
`--module-path`. Se o erro persistir, rode `mvn clean compile` antes, ou use
diretamente `mvn clean javafx:run`.

**"package javafx.application does not exist" / erros vermelhos no editor**
→ As dependências do Maven ainda não foram baixadas. Botão direito no projeto
→ "Reload POM" ou "Clean and Build".

**Erro de conexão com PostgreSQL ("Connection refused" ou "FATAL: password
authentication failed")**
→ Confira se o PostgreSQL está rodando e se usuário/senha em
`ConnectionFactory.java` estão corretos. O jogo mostra um alerta no Menu se
não conseguir conectar.

**"relation does not exist" no banco**
→ Normal na primeira execução; as tabelas são criadas automaticamente por
`BancoDados.inicializar()` ao iniciar o app. Se persistir, confira se o banco
`alice_wonderland` foi criado corretamente.

**Versão do Java**
→ O projeto usa `release 17`. Se seu JDK padrão no NetBeans for mais antigo
(8, 11), troque o JDK do projeto: botão direito → Properties → Sources → Source/Binary Format.
