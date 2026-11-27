package br.edu.ufla.ppoo.zuul;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Objects;

/**
 * Mantém o cenário e executa os comandos já interpretados do World of Zuul.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public class Jogo {
    private final PrintStream saida;
    private final Jogador jogador;
    private final Random sorteio = new Random();
    private final List<Porta> portas = new ArrayList<Porta>();
    private int tentativasRestantes;
    private EstadoPartida estadoPartida;

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
        jogador = new Jogador(criarAmbientes());
        tentativasRestantes = 1;
        estadoPartida = EstadoPartida.PREPARADA;
    }

    private Ambiente criarAmbientes() {
        Ambiente quarto1 = new Ambiente("quarto-1", "Quarto 1");
        Ambiente quarto2 = new Ambiente("quarto-2", "Quarto 2");
        Ambiente lavabo = new Ambiente("lavabo", "Lavabo");
        Ambiente banheiro = new Ambiente("banheiro", "banheiro");
        Ambiente salaJantar = new Ambiente("sala-jantar", "Sala de Jantar");
        Ambiente cozinha = new Ambiente("cozinha", "Cozinha");
        Ambiente salaTv = new Ambiente("sala-tv", "Sala de TV");
        Ambiente areaServico = new Ambiente("area-servico", "Área de Serviço");
        Ambiente corredor = new Ambiente("corredor", "Corredor");

        conectarAmbientes(salaTv, "norte", salaJantar, "sul", "porta-tv-jantar");
        conectarAmbientes(salaTv, "leste", areaServico, "oeste", "porta-tv-servico");
        conectarAmbientes(quarto1, "sul", lavabo, "norte", "porta-quarto1-lavabo");
        conectarAmbientes(lavabo, "sul", salaJantar, "norte", "porta-lavabo-jantar");
        conectarAmbientes(lavabo, "leste", corredor, "oeste", "porta-lavabo-corredor");
        conectarAmbientes(quarto2, "sul", banheiro, "norte", "porta-quarto2-banheiro");
        conectarAmbientes(banheiro, "sul", cozinha, "norte", "porta-banheiro-cozinha");
        conectarAmbientes(banheiro, "oeste", corredor, "leste", "porta-banheiro-corredor");
        conectarAmbientes(salaJantar, "leste", cozinha, "oeste", "porta-jantar-cozinha");
        conectarAmbientes(cozinha, "sul", areaServico, "norte", "porta-cozinha-servico");

        areaServico.definirTesouroEnterrado(true);

        return salaTv;
    }

    /**
     * Apresenta a mensagem de boas-vindas e o ambiente inicial.
     */
    void iniciar() {
        estadoPartida = EstadoPartida.EM_ANDAMENTO;
        saida.println();
        saida.println("Bem-vindo à casa mal-assombrada!");
        saida.println("Você é um caçador de tesouros procurando por uma fortuna enterrada.");
        saida.println("Você possui " + tentativasRestantes + " tentativas.");
        saida.println("Digite '" + Comando.AJUDA + "' se voce precisar de ajuda.");
        saida.println();
        imprimirAmbienteAtual();
    }

    private void imprimirAmbienteAtual() {
        Ambiente ambienteAtual = jogador.getAmbienteAtual();
        saida.println("Voce esta em " + ambienteAtual.getDescricao());
        saida.println("Atualmente voce possui " + tentativasRestantes + " tentativas restantes.");
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

        if (Comando.SAIR.equals(palavra)) {
            return sair(comando);
        }
        if (Comando.CAVAR.equals(palavra)) {
            cavar(comando);
            return partidaTerminou();
        }
        if (Comando.IR.equals(palavra)) {
            irParaAmbiente(comando);
            return partidaTerminou();
        }

        saida.println("Eu nao entendi o que voce disse...");
        return false;
    }

    /** 
     * Tenta ir em uma direcao. Se existe uma saida entra no 
     * novo ambiente, caso contrario imprime mensagem de erro.
     */
    private void irParaAmbiente(Comando comando) {
        if(tentativasRestantes <= 0) {
            estadoPartida = EstadoPartida.DERROTA;
            saida.println("Você não possui tentativas restantes. Game over.");
            return;
        }
        if (!comando.temArgumento()) {
            saida.println("Ir pra onde?");
            return;
        }

        String direcao = comando.getArgumento();
        Ambiente proximoAmbiente = jogador.getAmbienteAtual().consultarSaida(direcao);
        if (proximoAmbiente == null) {
            saida.println("Nao ha passagem!");
            return;
        }

        Ambiente ambienteAtual = jogador.getAmbienteAtual();
        Porta porta = ambienteAtual.consultarPorta(proximoAmbiente.getId());
        if (porta == null) {
            saida.println("A passagem não possui uma porta.");
            return;
        }

        boolean abriu = porta.abreNormalmente();
        sortearTodasAsPortas();
        tentativasRestantes--;

        if (!abriu) {
            saida.println("A porta está emperrada. Não foi possível abrir. " + tentativasRestantes + " tentativas restantes.");
            return;
        }

        List<ItemEncontravel> itensEncontrados = jogador.moverPara(proximoAmbiente);
        imprimirItensEncontrados(itensEncontrados);
        imprimirAmbienteAtual();
    }

    private void imprimirItensEncontrados(List<ItemEncontravel> itensEncontrados) {
        for (ItemEncontravel item : itensEncontrados) {
            if (item instanceof Dica) {
                saida.println("Voce encontrou uma dica: " + item.getDescricao());
            } else if (item instanceof ChaveMestra) {
                ChaveMestra chave = (ChaveMestra) item;
                saida.println("Voce encontrou uma chave mestra com durabilidade "
                        + chave.getDurabilidade() + ".");
            }
        }
    }

    private void conectarAmbientes(Ambiente ambienteA, String direcaoA,
                                          Ambiente ambienteB, String direcaoB, String idPorta) {
        ambienteA.definirSaida(direcaoA, ambienteB);
        ambienteB.definirSaida(direcaoB, ambienteA);

        Porta porta = new Porta(idPorta, ambienteA, ambienteB);
        
        portas.add(porta);
        porta.sortearEstado(sorteio);

        ambienteA.definirPorta(ambienteB.getId(), porta);
        ambienteB.definirPorta(ambienteA.getId(), porta);
    }

    private void sortearTodasAsPortas() {
        for (Porta porta : portas) {
            porta.sortearEstado(sorteio);
        }
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

    private void cavar(Comando comando) {
        if(!comando.temArgumento()) {
            if(jogador.consumirCarga()) {
                estadoPartida = EstadoPartida.VITORIA;
                saida.println("Você encontrou o tesouro. Parabéns!");
            } else {
                estadoPartida = EstadoPartida.DERROTA;
                saida.println("Você cavou e não encontrou nada. Game Over.");
                }
        } else {
            saida.println("Cavar o que?"); 
            }
    }

    private boolean partidaTerminou() {
    return estadoPartida == EstadoPartida.VITORIA || estadoPartida == EstadoPartida.DERROTA;
}

    /**
     * Sorteia o número de tentativas da partida
     */
    private int sorteioNumTentativas(){
        int min = 10;
        int max = 30;

        return sorteio.nextInt((max - min) + 1) + min;
    }
}
