package br.edu.ufla.ppoo.zuul;

/**
 * Mantém o cenário e executa os comandos já interpretados do World of Zuul.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public class Jogo {
    private Ambiente ambienteAtual;

    /**
     * Cria o cenário e posiciona o jogador no ambiente inicial.
     */
    public Jogo() {
        ambienteAtual = criarAmbientes();
    }

    private static Ambiente criarAmbientes() {
        Ambiente fora = new Ambiente("do lado de fora da entrada principal de uma universidade");
        Ambiente anfiteatro = new Ambiente("no anfiteatro");
        Ambiente cantina = new Ambiente("na cantina do campus");
        Ambiente laboratorio = new Ambiente("no laboratorio de computacao");
        Ambiente escritorio = new Ambiente("na sala de administracao dos computadores");

        fora.definirSaida("leste", anfiteatro);
        fora.definirSaida("sul", laboratorio);
        fora.definirSaida("oeste", cantina);
        anfiteatro.definirSaida("oeste", fora);
        cantina.definirSaida("leste", fora);
        laboratorio.definirSaida("norte", fora);
        laboratorio.definirSaida("leste", escritorio);
        escritorio.definirSaida("oeste", laboratorio);

        return fora;
    }

    /**
     * Apresenta a mensagem de boas-vindas e o ambiente inicial.
     */
    void iniciar() {
        imprimirBoasVindas();
    }

    /**
     * Imprime a mensagem de abertura para o jogador.
     */
    private void imprimirBoasVindas()
    {
        System.out.println();
        System.out.println("Bem-vindo ao World of Zuul!");
        System.out.println("World of Zuul eh um novo jogo de aventura, incrivelmente chato.");
        System.out.println("Digite 'ajuda' se voce precisar de ajuda.");
        System.out.println();
        
        System.out.println("Voce esta " + ambienteAtual.getDescricao());
    
        System.out.println("Saidas: " + String.join(" ", ambienteAtual.listarSaidas()));
    }

    /**
     * Executa um comando já interpretado.
     *
     * @param comando ação recebida do analisador
     * @return {@code true} se o comando encerra a sessão
     */
    boolean processarComando(Comando comando) {
        boolean querSair = false;

        if(comando.ehDesconhecido()) {
            System.out.println("Eu nao entendi o que voce disse...");
            return false;
        }

        String palavraDeComando = comando.getPalavraDeComando();
        if (palavraDeComando.equals("ajuda")) {
            imprimirAjuda();
        }
        else if (palavraDeComando.equals("ir")) {
            irParaAmbiente(comando);
        }
        else if (palavraDeComando.equals("sair")) {
            querSair = sair(comando);
        }

        return querSair;
    }

    // Implementacoes dos comandos do usuario

    /**
     * Printe informacoes de ajuda.
     * Aqui nos imprimimos algo bobo e enigmatico e a lista de 
     * palavras de comando
     */
    private void imprimirAjuda() 
    {
        System.out.println("Voce esta perdido. Voce esta sozinho. Voce caminha");
        System.out.println("pela universidade.");
        System.out.println();
        System.out.println("Suas palavras de comando sao:");
        System.out.println("   ir sair ajuda");
    }

    /** 
     * Tenta ir em uma direcao. Se existe uma saida entra no 
     * novo ambiente, caso contrario imprime mensagem de erro.
     */
    private void irParaAmbiente(Comando comando) {
        if (!comando.temArgumento()) {
            System.out.println("Ir pra onde?");
            return;
        }

        String direcao = comando.getArgumento();
        Ambiente proximoAmbiente = ambienteAtual.consultarSaida(direcao);
        if (proximoAmbiente == null) {
            System.out.println("Nao ha passagem!");
            return;
        }

        ambienteAtual = proximoAmbiente;
        System.out.println("Voce esta " + ambienteAtual.getDescricao());
        System.out.println("Saidas: " + String.join(" ", ambienteAtual.listarSaidas()));
    }

    /** 
     * "Sair" foi digitado. Verifica o resto do comando pra ver
     * se nos queremos realmente sair do jogo.
     * @return true, se este comando sai do jogo, false, caso contrario
     */
    private boolean sair(Comando comando) 
    {
        if(comando.temArgumento()) {
            System.out.println("Sair o que?");
            return false;
        }
        else {
            return true;  // sinaliza que nos queremos sair
        }
    }
}
