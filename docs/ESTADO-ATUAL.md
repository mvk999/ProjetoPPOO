# Estado atual do projeto PPOO

O projeto está no fim da etapa de correção das duas bases fornecidas para o trabalho prático de Práticas de Programação Orientada a Objetos da UFLA. O último commit de implementação descrito aqui é `760a0de`, de referência para a verificação de 26/11/2026.

O código atual tem dois pontos de entrada independentes. `br.edu.ufla.ppoo.zuul.Programa` executa a aventura textual original na universidade. `br.edu.ufla.ppoo.gui.Principal` abre uma demonstração Swing que percorre a planta da casa do enunciado. A sessão do terminal guarda seu ambiente atual. A demonstração guarda a posição visual do personagem e recebe pares de coordenadas de uma sequência predefinida.

## Onde encontrar cada parte

| Local | Conteúdo |
| --- | --- |
| [`AGENTS.md`](../AGENTS.md) | Escopo e sequência das oito correções, com as regras adotadas para o código. |
| [`pom.xml`](../pom.xml) | Projeto Maven `br.edu.ufla.ppoo:trabalho-pratico:1.0-SNAPSHOT`, Java 21, UTF-8 e JUnit Jupiter 5.11.4 para testes. |
| [`src/main/java/br/edu/ufla/ppoo/zuul`](../src/main/java/br/edu/ufla/ppoo/zuul) | Cinco classes ativas da base textual. |
| [`src/main/java/br/edu/ufla/ppoo/gui`](../src/main/java/br/edu/ufla/ppoo/gui) | Dez classes ativas da demonstração Swing, incluindo grade e busca A*. |
| [`src/main/resources/Imagens`](../src/main/resources/Imagens) | Imagens `pessoa.png` e `tijolo.png` carregadas pelo classpath. As duas também constam no JAR. |
| [`src/test/java/br/edu/ufla/ppoo`](../src/test/java/br/edu/ufla/ppoo) | Onze classes de teste em `zuul` e `gui`. |
| [`docs/README.md`](README.md) | Comandos de execução, contratos resumidos e procedimento de cobertura. |
| [`docs/bases-originais`](bases-originais) | Cópias das duas bases recebidas, fora da compilação Maven. |
| [`docs/enunciado`](enunciado) | PDF oficial e transcrição Markdown. A Figura 1, na página 3, mostra a planta da casa. A Figura 2, na página 4, mostra a GUI pedida. |
| [`diagrama-uml-corrigido-v6.txt`](diagrama-uml-corrigido-v6.txt) e [`diagrama-classes-v6.jpg`](diagramas/diagrama-classes-v6.jpg) | Diagrama SVG textual e versão em imagem da arquitetura prevista para o jogo integrado. |

O código ativo conserva os créditos do material de origem nas classes adaptadas e usa Javadoc nos contratos. Em `src/main/java`, os tipos locais e os argumentos de tipo nas construções são explícitos. As tarefas assíncronas e os ouvintes usam classes anônimas, em conformidade com a restrição do enunciado sobre expressões lambda.

## Histórico das correções

| Etapa | Commit | Mudança realizada |
| --- | --- | --- |
| 1. Igualdade de posições | `5743b28` | Organizou as bases em pacotes Maven, preservou as cópias de referência em `docs` e tornou `Posicao` imutável, com `equals` e `hashCode` por coordenadas. O movimento aleatório legado saiu de `Posicao`. |
| 2. Contrato do A* | `5b97b37` | Validou os extremos da busca, fez do caminho vazio o resultado para ausência de percurso e passou a ignorar nós da fila quando a mesma posição já foi fechada. A simulação ganhou tratamento para caminho vazio e posição inicial igual à final. |
| Testes das etapas 1 e 2 | `1ff2e9e` | Adicionou JUnit Jupiter e testes de regressão para igualdade em coleções, caminho mínimo e casos de simulação. |
| 3. Encapsulamento de saídas | `60d765c` | Substituiu os quatro campos públicos de direção por um mapa privado de saídas nomeadas em `Ambiente`. `Jogo` passou a navegar pelas operações de consulta dessa classe. |
| 4. Interpretação e terminal | `0c3ec32` | Moveu a leitura e o laço de sessão para `Programa`. `Analisador` passou a receber texto, e `Comando` passou a carregar uma palavra reconhecida e o argumento completo em campos finais. |
| 5. Ajuda e descrição | `79580a8` | Centralizou o vocabulário em `Analisador` e passou a gerar a lista da ajuda a partir dele. A descrição do ambiente inicial e a de cada chegada passaram a usar a mesma rotina em `Jogo`. `PalavrasComando` foi removida dos fontes ativos. |
| 6. Geometria e desenho | `f59b1b7` | Introduziu `CenarioDemonstracao` para os dados fixos da casa. `Mapa` passou a representar a geometria com coleções imutáveis, e `VisaoMapa` assumiu a renderização e a posição visual. As antigas classes gráficas `Jogador`, `Parede` e `Letra` saíram dos fontes ativos. |
| 7. Pintura e animação Swing | `351fc69` | Introduziu `AnimacaoMapa` com `javax.swing.Timer`. A interface passou a ser atualizada na thread de eventos, e `Simulacao` começou a devolver a conclusão assíncrona de cada pedido. Os testes passaram a cobrir redimensionamento, cancelamento e fechamento. |
| 8. Pontos de entrada e verificação conjunta | `760a0de` | Formalizou os contratos no Javadoc e em `docs/README.md`, ajustou a propriedade dos fluxos do terminal e o fechamento da janela quando a demonstração é interrompida. Acrescentou o teste conjunto das oito correções e ampliou os testes de recursos gráficos e dos pontos de entrada. |

## Fluxo da base textual

1. [`Programa.main`](../src/main/java/br/edu/ufla/ppoo/zuul/Programa.java) conecta `System.in`, `System.out` e `System.err` a `executar(BufferedReader, PrintStream, PrintStream)`. Cada chamada cria um `Jogo` e um `Analisador`. O programa exibe a abertura e o ambiente atual, imprime o prompt e lê uma linha por vez.
2. [`Analisador.interpretar(String)`](../src/main/java/br/edu/ufla/ppoo/zuul/Analisador.java) retira espaços em branco das extremidades e separa a primeira palavra do restante. O espaço em branco dentro do argumento é preservado. Por exemplo, `ir Sala de Jantar` resulta em `ir` e `Sala de Jantar`, embora a sessão da universidade só cadastre direções como saídas. O reconhecimento distingue maiúsculas de minúsculas. Linha vazia e palavra fora do vocabulário produzem um comando desconhecido. Uma linha nula é erro de chamada.
3. [`Comando`](../src/main/java/br/edu/ufla/ppoo/zuul/Comando.java) é imutável. `getPalavraDeComando()` devolve `null` quando a palavra é desconhecida. `getArgumento()` devolve o restante da linha, ou `null` quando está ausente. `ehDesconhecido()` e `temArgumento()` expõem essas duas condições. O vocabulário efetivo, na ordem apresentada pela ajuda, é `ir sair ajuda`.
4. `Programa` trata `ajuda` usando `Analisador.consultarAjuda()`. O texto de ajuda continua contextualizado na universidade. Os demais comandos vão para [`Jogo.processarComando(Comando)`](../src/main/java/br/edu/ufla/ppoo/zuul/Jogo.java), que tem visibilidade de pacote e devolve `true` quando a sessão deve terminar. `Jogo` mantém `ambienteAtual` e escreve suas mensagens no `PrintStream` recebido. O construtor público sem argumentos usa a saída padrão.
5. `ir` sem argumento pede uma direção. Uma direção ausente no ambiente produz a mensagem de falta de passagem e conserva o ambiente atual. Um deslocamento válido troca o ambiente e imprime a descrição e as saídas registradas. `sair` com argumento produz `Sair o que?` e a sessão continua. Comando desconhecido produz uma mensagem textual e também mantém a sessão aberta.
6. A sessão termina com `sair` sem argumento, fim da entrada ou `IOException` na leitura. Em caso de erro, `Programa` escreve o diagnóstico no fluxo de erros e imprime a despedida no fluxo normal. Os fluxos continuam abertos e pertencem ao chamador. Depois de um `sair`, o leitor ainda pode fornecer as linhas seguintes.

### Grafo da universidade

[`Ambiente`](../src/main/java/br/edu/ufla/ppoo/zuul/Ambiente.java) tem uma descrição fixa e um `LinkedHashMap<String, Ambiente>` para preservar a ordem de cadastro. `definirSaida` cadastra ou substitui o destino de um nome. `removerSaida` remove o cadastro. `consultarSaida` devolve `null` para nome ausente, e `listarSaidas` devolve uma cópia imutável dos nomes, em ordem de inserção. O retorno entre dois ambientes é um cadastro separado. Direções e destinos nulos são rejeitados no cadastro, assim como direções vazias ou compostas apenas por espaços em branco.

`Jogo.criarAmbientes()` define o grafo em Java. A sessão começa do lado de fora da universidade.

| Ambiente | Saídas na ordem exibida |
| --- | --- |
| Lado de fora da universidade | `leste` para o anfiteatro, `sul` para o laboratório, `oeste` para a cantina |
| Anfiteatro | `oeste` para o lado de fora |
| Cantina | `leste` para o lado de fora |
| Laboratório de computação | `norte` para o lado de fora, `leste` para a sala de administração dos computadores |
| Sala de administração dos computadores | `oeste` para o laboratório |

## Fluxo da demonstração Swing

### Geometria e busca

- [`Posicao`](../src/main/java/br/edu/ufla/ppoo/gui/Posicao.java) guarda `x` e `y` em campos finais. Na grade, `x` é a coluna e `y` é a linha, a partir de zero. Instâncias com as mesmas coordenadas são equivalentes como chaves de mapas e membros de conjuntos. Os limites pertencem a `Mapa`.
- [`Mapa`](../src/main/java/br/edu/ufla/ppoo/gui/Mapa.java) recebe largura, altura, `Set<Posicao>` de paredes e `Map<Posicao, String>` de letreiros. Copia as coleções, expõe visões imutáveis e valida dimensões positivas, coordenadas dentro da grade e ausência de letreiros em paredes. `podePassar(int x, int y)` considera transitável uma célula dentro dos limites e sem parede. Os letreiros ficam em células transitáveis.
- [`CenarioDemonstracao`](../src/main/java/br/edu/ufla/ppoo/gui/CenarioDemonstracao.java) define uma planta de 20 colunas por 17 linhas em uma lista fixa de strings. `#` indica parede, `.` indica célula livre e os demais caracteres formam letreiros. A comparação com a base original confirmou as mesmas 121 paredes e os mesmos 62 letreiros nas mesmas posições. Um mapa separado associa os nove nomes de ambientes às posições de referência.
- [`HeuristicaAEstrela`](../src/main/java/br/edu/ufla/ppoo/gui/HeuristicaAEstrela.java) mantém uma referência a `Mapa` e usa movimentos ortogonais de custo unitário. A distância Manhattan é a heurística. A fila de prioridade ordena os nós pela soma de custo percorrido e estimativa até o destino. A busca mantém os melhores custos por posição e um conjunto de posições fechadas. [`No`](../src/main/java/br/edu/ufla/ppoo/gui/No.java) carrega posição, predecessor e custos `G` e `H`. O caminho é reconstruído pelos predecessores, com origem e destino incluídos.
- `encontrarCaminho(Posicao, Posicao)` devolve a lista vazia para extremos fora da grade, bloqueados ou sem ligação. Devolve uma lista de um elemento para origem igual ao destino em uma célula transitável. Mapa nulo ou extremo nulo são erros de chamada. O caminho usa a grade inteira aberta conforme a planta e pode atravessar várias passagens entre cômodos.

| Ambiente na demonstração | Referência `(x, y)` |
| --- | --- |
| Quarto 1 | `(4, 2)` |
| Quarto 2 | `(16, 2)` |
| WC 1 | `(4, 6)` |
| WC 2 | `(16, 6)` |
| Sala de Jantar | `(4, 10)` |
| Cozinha | `(16, 10)` |
| Sala de TV | `(4, 14)` |
| Área de Serviço | `(16, 14)` |
| Corredor | `(9, 6)` |

### Desenho, eventos e ciclo de vida

- [`JanelaSimulacao`](../src/main/java/br/edu/ufla/ppoo/gui/JanelaSimulacao.java) é um `JFrame` de 500 × 500 intitulado `World of Zuul`. Ele contém `VisaoMapa` e usa `DISPOSE_ON_CLOSE`. A janela atual apresenta o mapa e a movimentação automática.
- [`VisaoMapa`](../src/main/java/br/edu/ufla/ppoo/gui/VisaoMapa.java) é um `JPanel`. Carrega `tijolo.png` e `pessoa.png` pelo classpath e pré-renderiza a grade, paredes e letreiros em um `BufferedImage` com células de 40 pixels. Guarda a posição visual separadamente e redesenha o personagem sobre o fundo a cada quadro. Ao sair de uma célula com letreiro, o fundo volta a ser exibido integralmente. O redimensionamento usa escala uniforme e centraliza a imagem. Recurso ausente ou formato inválido produz `IllegalStateException`, e falha de leitura produz `UncheckedIOException`.
- [`AnimacaoMapa`](../src/main/java/br/edu/ufla/ppoo/gui/AnimacaoMapa.java) recebe uma operação para exibir cada quadro e um ouvinte opcional de término. Copia e valida o percurso, exibe a origem imediatamente e agenda os quadros restantes com um timer Swing de 100 ms. Um percurso unitário termina no próprio evento. Nos demais, o último quadro fica disponível por um intervalo antes da notificação. A animação rejeita um percurso vazio ou outra animação quando o timer está ativo. `cancelar()` para o timer, mantém a última posição visual e encerra o percurso sem emitir o evento de conclusão.
- [`Simulacao`](../src/main/java/br/edu/ufla/ppoo/gui/Simulacao.java) é construída na thread de eventos do Swing, onde cria e exibe a janela. `executarSimulacao` calcula o caminho na thread chamadora e agenda a apresentação no Swing. O `CompletionStage<Boolean>` resolve com `true` quando o resultado foi apresentado, inclusive quando a lista é vazia e a posição visual é preservada. Resolve com `false` quando a janela fecha antes do término ou já está fechada. Pedidos sobrepostos terminam excepcionalmente com `IllegalStateException`. O fechamento cancela a animação ativa e resolve a execução pendente.
- [`Principal.main`](../src/main/java/br/edu/ufla/ppoo/gui/Principal.java) roda fora da thread de eventos e agenda a criação da simulação no Swing. Percorre todos os pares ordenados com nomes distintos, aguarda um resultado por vez e inicia o par seguinte. São 72 percursos, nove origens vezes oito destinos. Fechar a janela encerra a sequência. Ao terminar normalmente, o processo informa `Simulação concluída.` e a janela fica aberta até ser fechada. Em caso de interrupção ou falha durante a execução, `Principal` agenda o fechamento de uma janela que tenha sido criada.

## Como reproduzir o funcionamento atual

O ambiente verificado usou Java 21 e Maven 3.9.16. Os comandos abaixo partem da raiz do repositório. A demonstração gráfica exige uma sessão com tela.

```text
mvn clean verify
java -cp target/trabalho-pratico-1.0-SNAPSHOT.jar br.edu.ufla.ppoo.zuul.Programa
java -cp target/trabalho-pratico-1.0-SNAPSHOT.jar br.edu.ufla.ppoo.gui.Principal
```

Uma sessão textual curta pode usar `ajuda`, `ir sul`, `ir oeste` e `sair`, cada um em uma linha. A sessão mostra a ajuda, muda da entrada da universidade para o laboratório, informa que ali não há passagem para oeste e encerra.

```text
mvn -Dtest=CorrecaoPontaAPontaTest test
mvn "-Dppoo.testarGui=true" clean verify
```

## Testes e resultados verificados

| Teste | O que verifica |
| --- | --- |
| `PosicaoTest` | Igualdade, hash e busca ou substituição por coordenadas equivalentes em `HashSet` e `HashMap`. |
| `MapaTest` | Tamanho, contagem e referências da casa, cópias defensivas, limites e rejeição de letreiro sobre parede. |
| `HeuristicaAEstrelaTest` | Custos do nó, movimentos ortogonais, caminhos mínimos comparados com busca em largura entre as referências da casa, extremos inválidos, origem igual ao destino e regiões desconectadas. |
| `AmbienteTest` | Ordem, múltiplas saídas, cadastro separado do retorno, remoção, substituição e cópia imutável da lista. |
| `AnalisadorTest` | Vocabulário e ajuda, argumento composto, espaços, entrada vazia, palavra desconhecida e rejeição de texto nulo. |
| `JogoNavegacaoTest` | Navegação pelos cinco ambientes, comandos inválidos, ajuda, saída, fim de entrada e falha de leitura. |
| `VisaoMapaTest` | Mudança de célula, restauração do letreiro, preservação da parede, redimensionamento e falhas de recursos gráficos. |
| `AnimacaoMapaTest` | Quadros em ordem, processamento de outros eventos, percurso unitário, sobreposição, cancelamento e novo percurso. |
| `SimulacaoTest` | Janela real, ausência de percurso, origem igual ao destino, sobreposição e fechamento com percurso ativo. |
| `PrincipalTest` | Restrição de entrada fora da thread de eventos e fechamento da janela quando a execução é interrompida. |
| `CorrecaoPontaAPontaTest` | Sessão textual e reutilização dos fluxos, seguidas de busca em uma grade de teste, animação e conferência dos pixels inicial e final. As duas verificações usam instâncias e cenários separados. |

Na verificação de 26/11/2026, `mvn -o -B -ntp clean verify` compilou as 15 classes de produção, empacotou o JAR e passou em 49 casos. Quatro casos de janela ficaram condicionados à propriedade `ppoo.testarGui`. Com a propriedade habilitada, todos os **53 casos** passaram, sem falhas, erros ou casos ignorados. Com JaCoCo 0.8.13, a suíte com janela cobriu 441 de 450 linhas instrumentadas (98%) e 129 de 134 ramos (96,3%). Esses números descrevem a base atual e foram coletados somente na execução da suíte.

Também foram confirmados a sessão textual a partir do JAR, os **72 percursos distintos concluídos** pela demonstração Swing a partir do JAR, a falha esperada com causa `HeadlessException` quando a execução gráfica foi forçada sem tela e a geração de Javadoc com `-Xdoclint:all`. A compilação e os testes foram feitos em cópia temporária com o mesmo `pom.xml` e os mesmos arquivos de `src` do projeto. Os fontes do repositório foram preservados.

## Estado em relação ao enunciado e ao UML v6

As duas primeiras etapas do enunciado, a correção da base Zuul e a correção da base GUI, correspondem ao trabalho concluído. As etapas de extensão das regras, extensão da GUI e integração em um jogo executado pela interface gráfica permanecem em aberto no commit documentado. O cenário Java da universidade e a demonstração Java da casa são os fluxos que podem ser executados hoje. [Enunciado oficial](enunciado/Trabalho%20Pratico.pdf)

| Área | Estado atual | Escopo registrado na documentação |
| --- | --- | --- |
| Cenário | `Jogo` monta a universidade e `CenarioDemonstracao` declara a planta e as referências da casa em Java. | O enunciado exige um arquivo de texto único para os dados dos ambientes, paredes e letreiros. `CarregadorConfiguracao` e `ConfiguracaoJogo` aparecem no UML v6. |
| Deslocamento | O terminal consulta saídas por direção. A demonstração calcula percursos entre coordenadas com a geometria fixa. | O UML representa uma `Porta` entre ambientes e documenta uma assinatura do A* que recebe o identificador da porta. |
| Regras da casa | A sessão textual executa navegação, ajuda e saída. A casa está presente como demonstração visual. | O Anexo I descreve início na Sala de TV, tesouro, duas dicas, chave mestra, carga única, portas emperradas, tentativas e condições de vitória e derrota. `Jogador`, `ItemEncontravel`, `Dica`, `ChaveMestra` e `EstadoPartida` aparecem no diagrama do jogo integrado. |
| Gabarito | Os dois pontos de entrada operam com os respectivos cenários fixos. | O enunciado pede um arquivo de texto separado com os sorteios de localização do tesouro, das dicas e da chave mestra. `EscritorGabarito` é a classe representada no UML v6. |
| Interface | `JanelaSimulacao` contém o mapa com o personagem animado. A ajuda e os comandos estão no terminal. | A Figura 2 mostra mapa, entrada de comandos, mensagens, tentativas restantes, durabilidade da chave e dicas encontradas. `InterfaceGrafica`, `ControladorJogo` e `ResultadoAcao` pertencem ao desenho da integração no UML v6. |
| Entrega | O Maven gera um JAR com duas classes de entrada. O diagrama v6 está em SVG textual e JPG. | O enunciado pede a pasta e o ZIP `TrabalhoPratico_GrupoX`, com os fontes e o UML em PDF. |

No código, `Jogo.iniciar()` apresenta a abertura da sessão e `Jogo.processarComando(...)` devolve um sinal de encerramento. O UML v6 descreve `Jogo.iniciar()` e `Jogo.processar(...)` retornando `ResultadoAcao`, com os dados da partida e do deslocamento para apresentação. As assinaturas em uso estão nos fontes relacionados acima. [UML v6 em SVG](diagrama-uml-corrigido-v6.txt)
