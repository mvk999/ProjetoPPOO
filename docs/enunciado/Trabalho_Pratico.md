**Universidade Federal de Lavras**  
**Departamento de Computação Aplicada**

# Trabalho Prático de Práticas de Programação Orientada a Objetos

> **Instrução para a IA**  
> Use este Markdown junto com o PDF original [Trabalho Pratico.pdf](Trabalho%20Pratico.pdf). Ao encontrar uma referência visual, abra a página indicada no PDF e leia a figura diretamente. Se o PDF não estiver disponível no seu contexto, peça que ele seja anexado.

O trabalho a ser desenvolvido corresponde a um jogo implementado na linguagem Java. Ele deve ser um jogo estilo *Adventure* <u>baseado na implementação do jogo</u> *World of Zuul* apresentado em aula e discutido no capítulo “*Design de Classes*” do livro *BARNES, David J.; KOLING, Michael. Programação orientada a objetos com Java: uma introdução prática usando o BlueJ. 4. ed., Pearson, 2009.* Este livro está disponível na biblioteca virtual da UFLA.

O jogo deve seguir a mesma ideia central do World of Zuul, ou seja, o jogador utiliza comandos textuais para informar o que deseja fazer. Portanto, <u>deve-se usar a implementação base disponibilizada no Campus Virtual</u>. Vale ressaltar que a versão base do *World of Zuul* disponibilizada no Campus Virtual possui diversas falhas relacionadas ao *design* de classes, cujos ajustes estão discutidos no capítulo do livro supramencionado. Obviamente, <u>o trabalho deverá estar com todas essas falhas corrigidas</u>.

Apesar de o jogador utilizar comandos textuais para informar o que deseja fazer, <u>esses comandos textuais devem ser inseridos em uma GUI</u>, que além da área de inserção de comandos textuais, deverá ter o mapa do jogo, onde será exibida a movimentação do jogador, e áreas adicionais que apresentam informações gerais do jogo (ver Figura 2). A implementação inicial da GUI contendo apenas o mapa do jogo e toda a lógica de movimentação do jogador (utilizando o algoritmo de busca em grafos denominado A*) está sendo fornecida e deve ser aprimorada para composição da GUI final (ver Figura 2). Vale ressaltar que a versão base da *GUI* disponibilizada no Campus Virtual possui diversas falhas relacionadas ao *design* de classes, cujos ajustes devem ser realizados.

Portanto, o desenvolvimento deste trabalho inclui:

1. <u>Corrigir</u> as diversas <u>falhas</u> relacionadas ao *design* de classes <u>na versão base do *World of Zuul*</u> disponibilizada no Campus Virtual.
2. <u>Corrigir</u> as diversas <u>falhas</u> relacionadas ao *design* de classes <u>na versão base da *GUI*</u> disponibilizada no Campus Virtual.
3. <u>Estender</u> a implementação da <u>versão base do jogo *World of Zuul*</u> para que ela contemple todas as características apresentadas na Proposta do Jogo no Anexo I.
4. <u>Estender</u> a implementação da <u>versão base do jogo *GUI*</u> para que ela contemple todas as características apresentadas na Proposta do Jogo no Anexo I.
5. <u>Juntar as implementações</u> estendidas nas etapas anteriores para que o jogo funcione apenas a partir da *GUI*.

O trabalho deve atender os seguintes requisitos:

- Usar corretamente os conceitos de Orientação a Objetos.
- Fazer uso de composição e/ou agregação.
- Fazer uso de herança.
- Fazer uso de polimorfismo (variáveis e métodos polimórficos).
- Ter um bom *design* de classes.
- Utilizar um arquivo de texto para o gabarito do jogo (ver Anexo I).
- Utilizar outro arquivo de texto para armazenar tanto a lista de posições das paredes e letreiros que definem o mapa do jogo (na classe Mapa da implementação da *GUI*) como os dados utilizados na criação dos ambientes (na classe Jogo da implementação do *World of Zuul*). Ou seja, a definição dos ambientes e do mapa do jogo deve ser obtida a partir da leitura desse arquivo texto.
- Fazer uso de tratamento de exceção.
- O jogo deverá ter uma interface gráfica (GUI).
- Deve ser entregue junto com a implementação o diagrama de classes UML.
- Deve ter um código com boa legibilidade, organização e comentários relevantes.
- O código deve estar devidamente documentado no formato Javadoc.

Obs.: Não usar inferência de tipo nem expressão lambda na sua implementação.

## Entrega

Para a **entrega** do trabalho deve haver uma pasta raiz contendo todo o código da implementação do trabalho (somente arquivos .java) e o diagrama de classes UML (.pdf). Essa pasta, que deve ter o nome “*TrabalhoPratico_GrupoX*” (*X* é o número do grupo), deve ser **compactada** (.zip) **em um único arquivo** com o mesmo nome da pasta (ex: “*TrabalhoPratico_GrupoX.zip*”). Esse arquivo ZIP deve ser enviado **até 23h50 do dia 29/11/2026** pelo **Campus Virtual**.

**ATENÇÃO:** O grupo que não entregar o trabalho prático pelo Campus Virtual até 29/11/2026 ficará com nota zero e não participará da etapa de entrevista.

## ANEXO I

### Proposta do Jogo

Nesse jogo, o jogador será um caçador de tesouro em uma casa abandonada e mal assombrada. O jogador irá se movimentar entre os diferentes ambientes da casa por meio da abertura de portas em busca de um tesouro que está enterrado embaixo de algum dos ambientes da mesma. Ao chegar no ambiente onde o caçador julga estar o tesouro, ele deverá usar a única carga explosiva que carrega em sua mochila para abrir um buraco no solo de modo a recuperar o tesouro. O jogador vencerá o jogo somente se conseguir recuperar o tesouro.

O mapa da casa é mostrado na Figura 1. Cada ambiente, além das portas para os demais ambientes, pode conter itens (tesouro, chave mestra e dica). Nesse jogo, haverá um tesouro enterrado no ambiente **A**, duas dicas com o texto “O tesouro não está no(a) **X**” e uma chave mestra que possui uma durabilidade **Z** (número de portas que ela pode abrir). Como a casa é mal assombrada, às vezes, algumas portas não abrirão quando o jogador tenta passar pelas mesmas. Os valores de **X's**, **Y** e **Z** (inteiro entre 1 e 10) devem ser definidos aleatoriamente no início do jogo.

> **Instrução para a IA**  
> Leia a Figura 1 na [página 3 do PDF original](Trabalho%20Pratico.pdf#page=3). Consulte a imagem para identificar os ambientes, os letreiros, as paredes e as passagens.

*Figura 1 – Mapa da casa*

A localização do tesouro, das dicas e da chave mestra devem ser aleatoriamente definidos no início do jogo e armazenados em um arquivo texto (que serve como um gabarito do jogo). Além disso, cada porta da casa pode estar no estado “funcionando corretamente” ou “emperrada”, sendo que esse estado deve ser aleatoriamente redefinido a cada vez que o caçador tenta abrir uma porta. Ou seja, ao iniciar o jogo e após cada tentativa do caçador de abrir uma porta, o estado das portas da casa deve ser aleatoriamente redefinido. A quantidade de tentativas de abertura de portas define a duração máxima do jogo. Esse valor deve ser um número inteiro entre 10 e 30 aleatoriamente definido no início do jogo e apresentado para o jogador (Número de Tentativas Restantes na GUI). Ao longo do jogo, a única situação que não altera o número de tentativas de abertura de portas realizadas pelo jogador é quando o mesmo faz uso da chave mestra. Nesse caso, apenas decrementa-se uma unidade na durabilidade da chave mestra e a porta será aberta independentemente do estado da mesma. Se o caçador não recuperar o tesouro antes de acabarem suas tentativas de abertura de porta ou se utilizar sua carga explosiva no ambiente errado (onde não existe tesouro), o jogador perderá o jogo e o mesmo terminará.

Nesse jogo, o jogador sempre o começa na Sala de TV. Somente ao entrar pela primeira vez em um ambiente que contém uma dica ou a chave mestra, a dica ou a duração da chave mestra encontrada deve ser exibida na GUI (ver Figura 2). Além disso, a geração da dica com o texto “O tesouro não está no(a) **X**” será realizada escolhendo-se aleatoriamente um ambiente **X** onde o tesouro não está localizado. Vale frisar que o jogo deverá ter uma interface gráfica (GUI) como a apresentada na Figura 2.

> **Instrução para a IA**  
> Leia a Figura 2 na [página 4 do PDF original](Trabalho%20Pratico.pdf#page=4). Consulte a imagem para verificar a disposição da interface, os textos dos painéis e a área de mensagens e comandos.

*Figura 2 – GUI final*
