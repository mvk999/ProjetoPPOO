package br.edu.ufla.ppoo.zuul;

import java.io.PrintStream;
import java.util.Objects;

/**
 * Mantém o cenário e executa os comandos já interpretados do World of Zuul.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public class Jogo {
    private final PrintStream saida;
    private Ambiente ambienteAtual;

    /**
     * Cria o cenário e posiciona o jogador no ambiente inicial.
     * As mensagens são direcionadas à saída padrão atual.
     */
    public Jogo() {
        this(System.out);
    }

    /**
     * Cria uma sessão textual com a saída fornecida pelo ponto de entrada.
     *
     * @param saida destino das mensagens da sessão
     * @throws NullPointerException se a saída for nula
     */
    Jogo(PrintStream saida) {
        this.saida = Objects.requireNonNull(saida, "A saída é obrigatória.");
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
        saida.println();
        saida.println("Bem-vindo ao World of Zuul!");
        saida.println("World of Zuul eh um novo jogo de aventura, incrivelmente chato.");
        saida.println("Digite '" + Comando.AJUDA + "' se voce precisar de ajuda.");
        saida.println();
        imprimirAmbienteAtual();
    }

    private void imprimirAmbienteAtual() {
        saida.println("Voce esta " + ambienteAtual.getDescricao());
        saida.println("Saidas: " + String.join(" ", ambienteAtual.listarSaidas()));
    }

    /**
     * Executa um comando do jogo já interpretado. A apresentação da ajuda
     * cabe ao ponto de entrada.
     *
     * @param comando ação recebida do analisador
     * @return {@code true} se o comando encerra a sessão
     */
    boolean processarComando(Comando comando) {
        String palavra = comando.getPalavraDeComando();
        if (Comando.IR.equals(palavra)) {
            irParaAmbiente(comando);
            return false;
        }
        if (Comando.SAIR.equals(palavra)) {
            return sair(comando);
        }
        saida.println("Eu nao entendi o que voce disse...");
        return false;
    }

    /** 
     * Tenta ir em uma direcao. Se existe uma saida entra no 
     * novo ambiente, caso contrario imprime mensagem de erro.
     */
    private void irParaAmbiente(Comando comando) {
        if (!comando.temArgumento()) {
            saida.println("Ir pra onde?");
            return;
        }

        String direcao = comando.getArgumento();
        Ambiente proximoAmbiente = ambienteAtual.consultarSaida(direcao);
        if (proximoAmbiente == null) {
            saida.println("Nao ha passagem!");
            return;
        }

        ambienteAtual = proximoAmbiente;
        imprimirAmbienteAtual();
    }

    /** 
     * "Sair" foi digitado. Verifica o resto do comando pra ver
     * se nos queremos realmente sair do jogo.
     * @return true, se este comando sai do jogo, false, caso contrario
     */
    private boolean sair(Comando comando) 
    {
        if(comando.temArgumento()) {
            saida.println("Sair o que?");
            return false;
        }
        else {
            return true;  // sinaliza que nos queremos sair
        }
    }
}
