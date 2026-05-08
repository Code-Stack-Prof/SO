Aqui está um modelo estruturado em Java puro (Console):
---

```markdown
# Projeto Java Puro

Este repositório contém um projeto desenvolvido em **Java Standard Edition (Java SE)**,
sem a necessidade de frameworks ou gerenciadores de dependência externos (como Maven ou Gradle).

* Aplicação em JAVA Sequencial sem concorrência (sem Threads) e sem a Interface Gráfica.
* O objetivo é manter a lógica sequencial (sem Threads), o código se torna um clássico aplicativo de console (CLI). 
* A tela é substituída por saídas no terminal (System.out.println), o que torna o código mais enxuto e ideal para focar puramente na lógica de controle de fluxo.

"Aqui está um código Java, que funciona (roda) direto no terminal usando a sintaxe do Java 5"


## 🚀 Pré-requisitos

Antes de começar, você precisa ter o **JDK (Java Development Kit)** instalado em sua máquina. 
- Recomendado: JDK 17 ou superior.
- Verifique a instalação com: `java -version`

---

## 🛠️ Como trabalhar no projeto

### 1. Editar o código
Você pode utilizar qualquer editor de texto ou IDE. Sugestões:
*   **VS Code** (com a extensão "Extension Pack for Java")
*   **IntelliJ IDEA**
*   **Eclipse**
*   Ou até o bom e velho **Notepad++ / Vim**.

### 2. Compilar
Para transformar o código-fonte (`.java`) em bytecode (`.class`), utilize o compilador `javac`.
No terminal, navegue até a pasta onde estão os arquivos e execute:

```bash
javac NomeDoArquivo.java
```
*Isso gerará um arquivo chamado `NomeDoArquivo.class`.*

### 3. Executar
Após a compilação, você pode rodar o programa com o comando `java`:

```bash
java NomeDoArquivo
```
> **Nota:** Não inclua a extensão `.class` ao executar o comando.

---

## 📂 Estrutura de Pastas

Se o seu projeto crescer e usar pacotes (packages), a estrutura recomendada é:

```text
/projeto
│
├── /src              # Arquivos fonte (.java)
│   └── Main.java
└── /bin              # Arquivos compilados (.class)
```

Para compilar enviando os arquivos para a pasta `bin`:
```bash
javac -d bin src/*.java
java -cp bin Main
```

---

## 📝 Comandos Úteis

| Objetivo | Comando |
| :--- | :--- |
| Versão do Java | `java -version` |
| Compilar tudo | `javac *.java` |
| Executar Jar | `java -jar nome-do-arquivo.jar` |

---

Desenvolvido por [Prof, Douglas Cunha](https://github.com/dscunha)
```
