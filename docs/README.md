# Trabalho prático de PPOO

As bases corrigidas podem ser executadas separadamente. A base textual usa o cenário da universidade. A demonstração Swing usa o mapa da casa e anima os 72 percursos direcionais entre os nove ambientes.

## Compilar e executar

Use JDK 21, Maven 3.9 ou superior e arquivos em UTF-8. Se o PowerShell estiver usando um `JAVA_HOME` incorreto, ajuste a variável para a sessão atual.

```powershell
$env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent (Get-Command javac).Source)
```

Na raiz do projeto, compile, rode os testes e gere o JAR.

```text
mvn clean verify
```

```text
java -cp target/trabalho-pratico-1.0-SNAPSHOT.jar br.edu.ufla.ppoo.zuul.Programa
java -cp target/trabalho-pratico-1.0-SNAPSHOT.jar br.edu.ufla.ppoo.gui.Principal
```

No terminal, digite `ajuda`, `ir sul` e `sair` para experimentar uma sessão. Na demonstração, os percursos avançam automaticamente. Fechar a janela interrompe a sequência. A execução gráfica requer uma sessão com tela. O JAR disponibiliza as duas classes de entrada e inclui as imagens no classpath.

## Contratos para a integração

| Classe ou método | Contrato da base corrigida |
| --- | --- |
| `zuul.Programa` | `main` conecta os fluxos padrão a `executar(BufferedReader, PrintStream, PrintStream)`. Cada chamada inicia um jogo. A sessão termina com `sair` sem argumento, fim de entrada ou falha de leitura. Uma falha de leitura é informada no fluxo de diagnóstico. Os fluxos permanecem abertos e o chamador pode continuar lendo as linhas posteriores a `sair`. |
| `zuul.Analisador` e `Comando` | `interpretar(String)` recebe uma linha, distingue maiúsculas de minúsculas e preserva o argumento completo. A entrada vazia ou uma palavra fora do vocabulário produz um comando desconhecido. O resultado é imutável. A mesma lista de palavras alimenta o reconhecimento e a ajuda. |
| `zuul.Ambiente` e `Jogo` | Cada ambiente cadastra suas saídas por nome, em ordem de inserção. A volta precisa ser cadastrada no destino. `consultarSaida` devolve `null` para uma saída ausente, e `listarSaidas` devolve uma cópia imutável. `Jogo` descreve o ambiente inicial e cada chegada pela mesma rotina. Uma tentativa inválida preserva o ambiente atual. |
| `gui.Posicao` e `Mapa` | `Posicao` é imutável e possui igualdade e hash por coordenadas. A grade usa colunas e linhas a partir de zero. `Mapa` copia paredes e letreiros, valida os limites e expõe coleções imutáveis. |
| `gui.HeuristicaAEstrela` | `encontrarCaminho(origem, destino)` devolve o caminho mínimo ortogonal com os dois extremos. O retorno é vazio para extremos bloqueados, fora da grade ou sem ligação. Extremos iguais e transitáveis produzem uma lista de um elemento. Argumentos nulos são considerados erro de chamada. |
| `gui.VisaoMapa` e `AnimacaoMapa` | A visão mantém o desenho do mapa e a posição visual. A animação recebe uma cópia do percurso, atualiza os quadros na thread de eventos do Swing e avisa o ouvinte uma vez após a conclusão. O cancelamento mantém o último quadro e encerra a animação sem emitir o evento de conclusão. |
| `gui.Principal` e `Simulacao` | `Principal.main` roda fora da thread de eventos. `Simulacao` cria, exibe e atualiza a janela nessa thread. `executarSimulacao` calcula o percurso na thread que chamou e devolve uma conclusão assíncrona. O valor `true` indica que o resultado foi apresentado, inclusive quando não há caminho. O valor `false` indica fechamento da janela. Uma solicitação sobreposta falha com `IllegalStateException`. Aguarde a conclusão fora da thread de eventos. |

Os fontes ativos estão em `src/main/java/br/edu/ufla/ppoo/{zuul,gui}`. As imagens usadas pela demonstração estão em `src/main/resources/Imagens`. As cópias em [bases-originais](bases-originais) servem para comparação e ficam fora da compilação.

O [enunciado](enunciado/Trabalho%20Pratico.pdf) define a entrega. O [SVG v6](diagrama-uml-corrigido-v6.txt) orienta a integração para a GUI final. Nessa integração, o controlador recebe os comandos, aplica as regras do jogo, apresenta o resultado e anima os deslocamentos autorizados. A configuração em arquivo, o gabarito e as novas regras da casa pertencem à extensão e à integração do grupo.

## Testes das correções

O caso `CorrecaoPontaAPontaTest` cobre as oito etapas em uma execução. Ele verifica uma sessão textual com ajuda e deslocamentos válidos e inválidos, a reutilização dos fluxos, a igualdade das coordenadas usadas pelo mapa, o desvio mínimo do A*, o percurso vazio e a pintura inicial e final da animação. A pintura é feita em memória e funciona sem uma janela.

```text
mvn -Dtest=CorrecaoPontaAPontaTest test
```

Em uma sessão com tela, habilite também os testes que abrem, fecham e interrompem a janela real.

```text
mvn "-Dppoo.testarGui=true" clean verify
```

Para gerar o relatório de linhas e ramos no mesmo ambiente, execute o JaCoCo junto com a suíte.

```text
mvn "-Dppoo.testarGui=true" clean org.jacoco:jacoco-maven-plugin:0.8.13:prepare-agent verify org.jacoco:jacoco-maven-plugin:0.8.13:report
```

Para incluir os caminhos de execução do JAR, rode o primeiro comando Java abaixo duas vezes. Na primeira execução, espere os 72 percursos terminarem e feche a janela. Na segunda, feche a janela durante o primeiro percurso. Depois rode o comando sem tela, que deve falhar com `HeadlessException`, e gere o relatório sem limpar os dados já coletados.

```powershell
$agente = Join-Path $HOME '.m2/repository/org/jacoco/org.jacoco.agent/0.8.13/org.jacoco.agent-0.8.13-runtime.jar'
$jacoco = "-javaagent:${agente}=destfile=target/jacoco.exec"
java "$jacoco" -cp target/trabalho-pratico-1.0-SNAPSHOT.jar br.edu.ufla.ppoo.gui.Principal
java '-Djava.awt.headless=true' "$jacoco" -cp target/trabalho-pratico-1.0-SNAPSHOT.jar br.edu.ufla.ppoo.gui.Principal
mvn org.jacoco:jacoco-maven-plugin:0.8.13:report
```

O relatório consolidado fica em `target/site/jacoco/index.html`.
