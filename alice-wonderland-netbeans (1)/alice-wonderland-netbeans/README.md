# País das Maravilhas: O Tempo Esquecido

Jogo desenvolvido em **Java + JavaFX (FXML)**, seguindo o padrão **MVC**, com persistência em **PostgreSQL via JDBC**.

Tema baseado na história de Alice no País das Maravilhas: o Chapeleiro Maluco, em sua loucura
causada pelo mercúrio, ofendeu o Tempo, que parou de correr, condenando o Coelho Branco a
estar eternamente atrasado e sendo sempre "hora do chá". Alice precisa coletar xícaras de chá,
evoluir e enfrentar o Tempo para conseguir o perdão.

---

## 1. Requisitos

- **Java 21+** (JDK)
- **Maven 3.8+**
- **PostgreSQL** instalado e rodando

---

## 2. Configuração do Banco de Dados

Crie o banco no PostgreSQL:

```sql
CREATE DATABASE alice_wonderland;
```

As tabelas (`jogadores`, `partidas`, `inventario`) são criadas **automaticamente** na primeira
execução, pelo método `BancoDados.inicializar()`.

### Configurar usuário/senha

Edite o arquivo:

```
src/main/java/com/alice/util/ConnectionFactory.java
```

E ajuste conforme suas credenciais locais:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/alice_wonderland";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

---

## 3. Como executar

Na raiz do projeto (onde está o `pom.xml`):

```bash
mvn clean javafx:run
```

Ou gere um JAR executável:

```bash
mvn clean package
```

---

## 4. Como jogar

### Menu Principal
- Digite seu nome e clique em **Jogar** (cria/busca o jogador no banco)
- **Ranking**: mostra o top 10 jogadores, com exportação em CSV/TXT
- **Créditos**: dados da dupla e tecnologias
- **Encerrar**: fecha o jogo

### Gameplay (mecânica principal — cíclica)
- Mova Alice com **WASD** ou **setas**
- Colete as **xícaras de chá ☕** (pontos + moedas)
- Desvie dos **relógios loucos ⏰** (perdem vida)
- Pressione **I** para abrir o **Inventário** (nova janela)
- Ao coletar todas as xícaras (ou perder todas as vidas), pressione **ESPAÇO** para ir aos Upgrades

### Upgrades
- Gaste moedas ganhas na run para melhorar:
  - **Mais Dano** → mais pontos por xícara
  - **Mais Velocidade** → Alice se move mais rápido
  - **Mais Sorte** → mais moedas por xícara
- Pode jogar novamente ou ir à Loja

### Loja
- **Chapéu do Chapeleiro** (8☕): +2 dano permanente
- **Poção de Alice** (6☕): item de inventário
- **Chave das Maravilhas** (15☕): necessária para o confronto
- **Confrontar o Tempo**: disponível a partir do **nível 3**

### Boss / Finalização — O Tempo
- Desvie do ponteiro vermelho giratório
- Toque nos pontos verdes (pontos fracos do relógio) para derrotar o Tempo
- Vencer = final da história (perdão concedido)

---

## 5. Progressão

- Cada xícara coletada dá XP; a cada 100 XP, o jogador sobe de nível
- Níveis mais altos aumentam: meta de xícaras por run, quantidade e velocidade dos relógios
- O boss também fica mais rápido conforme o nível do jogador

---

## 6. Arquitetura (MVC)

```
src/main/java/com/alice/
├── MainApp.java          → ponto de entrada / troca de telas
├── model/                → Jogador, ItemInventario, Sessao (estado da run)
├── view/                 → (FXML em resources/com/alice/fxml)
├── controller/           → Menu, Gameplay, Upgrades, Loja, Boss, Ranking, Créditos, Inventário
├── dao/                  → BancoDados (acesso a dados / persistência)
└── util/                 → ConnectionFactory (conexão JDBC)
```

---

## 7. Tecnologias

- Java 21
- JavaFX 21 (Controls + FXML)
- PostgreSQL + JDBC (driver 42.7.3)
- CSS para estilização do tema "País das Maravilhas"
- Maven (javafx-maven-plugin)

---

## 8. Observações

- Caso o banco não conecte, o jogo exibirá um alerta no Menu ao tentar "Jogar".
- O Ranking mostra mensagem amigável caso ainda não haja partidas registradas.
- Edite `creditos.fxml` para inserir os nomes reais da dupla.
