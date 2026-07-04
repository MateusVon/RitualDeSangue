# Como compilar e rodar

Pré-requisito: JDK 17+ instalado (não apenas JRE) e o banco `ritual_de_sangue` criado no MySQL local (usuário `root`, senha em branco, ajuste em `src/database/Conexao.java` se for diferente).

## Linux / Mac (bash)

```bash
cd RitualDeSangue
mkdir -p bin
javac -encoding UTF-8 -d bin -cp "lib/mysql-connector-j-9.7.0.jar" $(find src -name "*.java")
java -cp "bin:lib/mysql-connector-j-9.7.0.jar" main.Jogo
```

## Windows (cmd/PowerShell)

```bat
cd RitualDeSangue
mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin -cp "lib\mysql-connector-j-9.7.0.jar" @sources.txt
java -cp "bin;lib\mysql-connector-j-9.7.0.jar" main.Jogo
```

O `-encoding UTF-8` é importante: o código usa acentuação (á, ç, ã...) e sem essa flag o `javac` pode usar a codificação padrão do sistema (ex: cp1252 no Windows) e corromper os textos.
